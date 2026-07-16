package com.app.service.impl;

import com.app.dto.LifecycleResponse;
import com.app.entity.FileMetadata;
import com.app.entity.FileStatus;
import com.app.exception.MetadataNotFoundException;
import com.app.repository.FileMetadataRepository;
import com.app.service.LifecycleService;
import com.app.service.ObjectService;
import com.app.validator.LifecycleValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class LifecycleServiceImpl implements LifecycleService {

    private final FileMetadataRepository repository;
    private final ObjectService objectService;

    public LifecycleServiceImpl(FileMetadataRepository repository, ObjectService objectService) {
        this.repository = repository;
        this.objectService = objectService;
    }

    @Override
    public LifecycleResponse softDelete(String id) {

        FileMetadata metadata = repository.findById(id)
                .orElseThrow(() -> new MetadataNotFoundException(id));

        LifecycleValidator.validateSoftDelete(metadata);

        Instant now = Instant.now();

        metadata.setStatus(FileStatus.DELETED);
        metadata.setDeletedAt(now);
        metadata.setUpdatedAt(now);

        repository.save(metadata);

        return buildResponse(
                metadata,
                "File moved to recycle bin successfully.");
    }

    @Override
    public LifecycleResponse restore(String id) {

        FileMetadata metadata = repository.findById(id)
                .orElseThrow(() -> new MetadataNotFoundException(id));

        LifecycleValidator.validateRestore(metadata);

        Instant now = Instant.now();

        metadata.setStatus(FileStatus.ACTIVE);
        metadata.setDeletedAt(null);
        metadata.setArchivedAt(null);
        metadata.setUpdatedAt(now);

        repository.save(metadata);

        return buildResponse(
                metadata,
                "File restored successfully.");
    }

    @Override
    public LifecycleResponse archive(String id) {

        FileMetadata metadata = repository.findById(id)
                .orElseThrow(() -> new MetadataNotFoundException(id));

        LifecycleValidator.validateArchive(metadata);

        Instant now = Instant.now();

        metadata.setStatus(FileStatus.ARCHIVED);
        metadata.setArchivedAt(now);
        metadata.setUpdatedAt(now);

        repository.save(metadata);

        return buildResponse(
                metadata,
                "File archived successfully.");
    }

    @Override
    public LifecycleResponse permanentDelete(String id) {

        FileMetadata metadata = repository.findById(id)
                .orElseThrow(() -> new MetadataNotFoundException(id));

        LifecycleValidator.validatePermanentDelete(metadata);
        objectService.delete(id);

        return buildResponse(metadata,
                "File permanently deleted.");
    }

    @Override
    public Page<FileMetadata> getActiveFiles(int page, int size) {

        return repository.findByStatus(
                FileStatus.ACTIVE,
                PageRequest.of(page, size));
    }

    @Override
    public Page<FileMetadata> getArchivedFiles(int page, int size) {

        return repository.findByStatus(
                FileStatus.ARCHIVED,
                PageRequest.of(page, size));
    }

    @Override
    public Page<FileMetadata> getDeletedFiles(int page, int size) {

        return repository.findByStatus(
                FileStatus.DELETED,
                PageRequest.of(page, size));
    }

    private LifecycleResponse buildResponse(FileMetadata metadata, String message) {

        return new LifecycleResponse(
                metadata.getId(),
                metadata.getOriginalName(),
                metadata.getStatus().name(),
                message,
                Instant.now()
        );
    }
}