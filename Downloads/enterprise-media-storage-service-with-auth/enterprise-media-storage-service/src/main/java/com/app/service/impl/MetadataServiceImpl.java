package com.app.service.impl;

import com.app.entity.FileMetadata;
import com.app.entity.FileStatus;
import com.app.exception.MetadataNotFoundException;
import com.app.repository.FileMetadataRepository;
import com.app.service.MetadataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class MetadataServiceImpl implements MetadataService {

    private final FileMetadataRepository repository;

    public MetadataServiceImpl(FileMetadataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<FileMetadata> getAllFiles(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return repository.findAll(pageable);
    }

    @Override
    public FileMetadata getById(String id) {
        return repository.findById(id).orElseThrow(() -> new MetadataNotFoundException(id));
    }

    @Override
    public Page<FileMetadata> searchByFileName(String fileName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByOriginalNameContainingIgnoreCase(fileName, pageable);
    }

    @Override
    public Page<FileMetadata> searchByBucket(String bucketName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByBucketName(bucketName, pageable);
    }

    @Override
    public Page<FileMetadata> searchByUser(String uploadedBy, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByUploadedBy(uploadedBy, pageable);
    }

    @Override
    public Page<FileMetadata> searchByStatus(FileStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByStatus(status, pageable);
    }

    @Override
    public Page<FileMetadata> searchByFileType(String fileType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByFileType(fileType, pageable);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public long countByBucket(String bucketName) {
        return repository.countByBucketName(bucketName);
    }
}