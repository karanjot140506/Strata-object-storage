package com.app.service.impl;

import com.app.dto.CopyRequest;
import com.app.dto.MoveRequest;
import com.app.dto.RenameRequest;
import com.app.entity.FileMetadata;
import com.app.entity.FileStatus;
import com.app.exception.FileDownloadException;
import com.app.exception.FileUploadException;
import com.app.exception.MetadataNotFoundException;
import com.app.exception.ValidationException;
import com.app.repository.FileMetadataRepository;
import com.app.service.BucketService;
import com.app.service.ObjectService;
import com.app.util.ChecksumUtil;
import com.app.util.FileNameGenerator;
import com.app.util.FileTypeUtil;
import com.app.validator.UploadValidator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ObjectServiceImpl implements ObjectService {

    private final S3Client s3Client;
    private final FileMetadataRepository repository;
    private final BucketService bucketService;

    public ObjectServiceImpl(S3Client s3Client,
                             FileMetadataRepository repository,
                             BucketService bucketService) {
        this.s3Client = s3Client;
        this.repository = repository;
        this.bucketService = bucketService;
    }

    @Override
    public FileMetadata upload(MultipartFile file, String bucketName, String uploadedBy) {
        try {
            UploadValidator.validate(file, bucketName, uploadedBy);
            bucketService.bucketExists(bucketName);

            Instant now = Instant.now();

            String storedName = FileNameGenerator.generate(
                    Objects.requireNonNull(file.getOriginalFilename()));

            String checksum = ChecksumUtil.sha256(file.getInputStream());

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storedName)
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize())
            );

            FileMetadata metadata = new FileMetadata();

            metadata.setOriginalName(file.getOriginalFilename());
            metadata.setStoredName(storedName);
            metadata.setBucketName(bucketName);
            metadata.setFileSize(file.getSize());
            metadata.setContentType(file.getContentType());
            metadata.setUploadedBy(uploadedBy);
            metadata.setUploadedAt(now);
            metadata.setUpdatedAt(now);
            metadata.setChecksum(checksum);
            metadata.setEtag(response.eTag());
            metadata.setStatus(FileStatus.ACTIVE);
            metadata.setFileType(FileTypeUtil.determineType(file.getContentType()));

            return repository.save(metadata);
        }
        catch (IOException ex) {
            throw new FileUploadException("Failed to read uploaded file.");
        }
        catch (S3Exception ex) {
            throw new FileUploadException(ex.awsErrorDetails().errorMessage());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream download(String metadataId) {
        try {
            FileMetadata metadata = repository.findById(metadataId).orElseThrow(() -> new MetadataNotFoundException(metadataId));

            if(metadata.getStatus() != FileStatus.ACTIVE){
                throw new ValidationException("File is not available.");
            }

            GetObjectRequest request = GetObjectRequest.builder().bucket(metadata.getBucketName()).key(metadata.getStoredName()).build();

            return s3Client.getObject(request);
        }
        catch (S3Exception ex) {
            throw new FileDownloadException(
                    ex.awsErrorDetails().errorMessage());
        }
    }

    @Override
    public void delete(String metadataId) {

        FileMetadata metadata = repository.findById(metadataId).orElseThrow(() -> new MetadataNotFoundException(metadataId));

        DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(metadata.getBucketName()).key(metadata.getStoredName()).build();

        s3Client.deleteObject(request);

        repository.delete(metadata);
    }

    @Override
    public List<String> listObjects(String bucketName) {

        ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketName).build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);

        return response.contents().stream().map(S3Object::key).collect(Collectors.toList());
    }

    @Override
    public void copy(CopyRequest request) {
        CopyObjectRequest copyRequest = CopyObjectRequest.builder().
                        sourceBucket(request.getSourceBucket()).
                        sourceKey(request.getObjectKey()).
                        destinationBucket(request.getTargetBucket()).
                        destinationKey(request.getObjectKey()).build();

        s3Client.copyObject(copyRequest);
    }

    @Override
    public void move(MoveRequest request) {
        if (request.getSourceBucket().equals(request.getTargetBucket())) {
            throw new ValidationException("Source and target bucket cannot be same");
        }

        CopyRequest copyRequest = new CopyRequest();

        copyRequest.setSourceBucket(request.getSourceBucket());
        copyRequest.setTargetBucket(request.getTargetBucket());
        copyRequest.setObjectKey(request.getObjectKey());
        copy(copyRequest);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder().bucket(request.getSourceBucket()).key(request.getObjectKey()).build();
        s3Client.deleteObject(deleteRequest);

//        bug fix - metaData Update with target bucket name
        FileMetadata metaData = repository.findByStoredName(request.getObjectKey()).orElseThrow(() -> new MetadataNotFoundException(request.getObjectKey()));

        metaData.setBucketName(request.getTargetBucket());
        metaData.setUpdatedAt(Instant.now());

        repository.save(metaData);
    }

    @Override
    public void rename(RenameRequest request) {

        if (request.getOldStoredName() == null || request.getOldStoredName().isBlank()) {
            throw new ValidationException("Old file name cannot be empty.");
        }

        if (request.getNewStoredName() == null || request.getNewStoredName().isBlank()) {
            throw new ValidationException("New file name cannot be empty.");
        }

        if (request.getOldStoredName().equals(request.getNewStoredName())) {
            throw new ValidationException("Old and new names cannot be same.");
        }

        FileMetadata metadata = repository.findByStoredName(request.getOldStoredName())
                .orElseThrow(() ->
                        new MetadataNotFoundException(request.getOldStoredName()));

        if (repository.findByStoredName(request.getNewStoredName()).isPresent()) {
            throw new ValidationException("A file with the new name already exists.");
        }

        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(request.getBucketName())
                .sourceKey(request.getOldStoredName())
                .destinationBucket(request.getBucketName())
                .destinationKey(request.getNewStoredName())
                .build();

        s3Client.copyObject(copyRequest);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(request.getBucketName())
                .key(request.getOldStoredName())
                .build();

        s3Client.deleteObject(deleteRequest);

        metadata.setStoredName(request.getNewStoredName());
        metadata.setUpdatedAt(Instant.now());

        repository.save(metadata);
    }
}