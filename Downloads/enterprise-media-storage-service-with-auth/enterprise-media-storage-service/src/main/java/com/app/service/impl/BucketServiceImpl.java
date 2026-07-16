package com.app.service.impl;

import com.app.exception.BucketAlreadyExistsException;
import com.app.exception.BucketNotFoundException;
import com.app.exception.InvalidFileException;
import com.app.service.BucketService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BucketServiceImpl implements BucketService {

    private final S3Client s3Client;

    public BucketServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void createBucket(String bucketName) {
        if (bucketExists(bucketName)) {
            throw new BucketAlreadyExistsException(bucketName);
        }

        CreateBucketRequest request = CreateBucketRequest.builder().bucket(bucketName).build();

        s3Client.createBucket(request);
    }

    @Override
    public void deleteBucket(String bucketName) {

        if (bucketName == null || bucketName.trim().isEmpty()) {

            throw new InvalidFileException("Bucket name cannot be empty");
        }

        if (!bucketExists(bucketName)) {

            throw new BucketNotFoundException(bucketName);
        }

        DeleteBucketRequest request = DeleteBucketRequest.builder().bucket(bucketName).build();

        s3Client.deleteBucket(request);
    }

    @Override
    public List<String> getAllBuckets() {
        ListBucketsResponse response = s3Client.listBuckets();

        return response.buckets().stream().map(Bucket::name).collect(Collectors.toList());
    }

    @Override
    public boolean bucketExists(String bucketName) {
        try {
            HeadBucketRequest request = HeadBucketRequest.builder().bucket(bucketName).build();
            s3Client.headBucket(request);
            return true;

        } catch (Exception ex) {
            return false;
        }
    }
}