package com.app.repository;

import com.app.entity.FileMetadata;
import com.app.entity.FileStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends MongoRepository<FileMetadata, String> {

    /* =========================
       Existing Search Queries
       ========================= */

    List<FileMetadata> findByUploadedBy(String uploadedBy);

    List<FileMetadata> findByBucketName(String bucketName);

    List<FileMetadata> findByOriginalNameContainingIgnoreCase(String name);

    Optional<FileMetadata> findByStoredName(String storedName);

    List<FileMetadata> findByStatus(FileStatus status);

    /* =========================
       Pagination Queries
       ========================= */

    Page<FileMetadata> findByFileType(String fileType, Pageable pageable);

    Page<FileMetadata> findByOriginalNameContainingIgnoreCase(String originalName, Pageable pageable);

    Page<FileMetadata> findByBucketName(String bucketName, Pageable pageable);

    Page<FileMetadata> findByUploadedBy(String uploadedBy, Pageable pageable);

    Page<FileMetadata> findByStatus(FileStatus status, Pageable pageable);

    /* =========================
       Lifecycle Queries
       ========================= */

    Optional<FileMetadata> findByIdAndStatus(String id, FileStatus status);

    boolean existsByIdAndStatus(String id, FileStatus status);

    Page<FileMetadata> findByStatusAndBucketName(FileStatus status, String bucketName, Pageable pageable);

    Page<FileMetadata> findByStatusAndUploadedBy(FileStatus status, String uploadedBy, Pageable pageable);

    Page<FileMetadata> findByStatusAndFileType(FileStatus status, String fileType, Pageable pageable);

    /* =========================
       Statistics Queries
       ========================= */

    long countByBucketName(String bucketName);

    long countByUploadedBy(String uploadedBy);

    long countByStatus(FileStatus status);

    long countByFileType(String fileType);
}