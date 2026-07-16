package com.app.service;
import com.app.dto.LifecycleResponse;
import com.app.entity.FileMetadata;
import org.springframework.data.domain.Page;

// Handles all file lifecycle operations like Archive, Restore, Soft Delete and Permanent Delete.


public interface LifecycleService {

    LifecycleResponse softDelete(String id);

    LifecycleResponse restore(String id);

    LifecycleResponse archive(String id);

    LifecycleResponse permanentDelete(String id);

    Page<FileMetadata> getActiveFiles(int page, int size);

    Page<FileMetadata> getArchivedFiles(int page, int size);

    Page<FileMetadata> getDeletedFiles(int page, int size);
}