package com.app.dto;

import java.util.List;
import java.util.Map;

public class StorageStatisticsResponse {

    private long totalFiles;
    private long activeFiles;
    private long archivedFiles;
    private long deletedFiles;
    private long totalStorageBytes;
    private long totalBuckets;
    private Map<String, Long> filesPerBucket;
    private List<FileTypeStatisticsResponse> fileTypeBreakdown;

    public StorageStatisticsResponse() {
    }

    public long getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(long totalFiles) {
        this.totalFiles = totalFiles;
    }

    public long getActiveFiles() {
        return activeFiles;
    }

    public void setActiveFiles(long activeFiles) {
        this.activeFiles = activeFiles;
    }

    public long getArchivedFiles() {
        return archivedFiles;
    }

    public void setArchivedFiles(long archivedFiles) {
        this.archivedFiles = archivedFiles;
    }

    public long getDeletedFiles() {
        return deletedFiles;
    }

    public void setDeletedFiles(long deletedFiles) {
        this.deletedFiles = deletedFiles;
    }

    public long getTotalStorageBytes() {
        return totalStorageBytes;
    }

    public void setTotalStorageBytes(long totalStorageBytes) {
        this.totalStorageBytes = totalStorageBytes;
    }

    public long getTotalBuckets() {
        return totalBuckets;
    }

    public void setTotalBuckets(long totalBuckets) {
        this.totalBuckets = totalBuckets;
    }

    public Map<String, Long> getFilesPerBucket() {
        return filesPerBucket;
    }

    public void setFilesPerBucket(Map<String, Long> filesPerBucket) {
        this.filesPerBucket = filesPerBucket;
    }

    public List<FileTypeStatisticsResponse> getFileTypeBreakdown() {
        return fileTypeBreakdown;
    }

    public void setFileTypeBreakdown(List<FileTypeStatisticsResponse> fileTypeBreakdown) {
        this.fileTypeBreakdown = fileTypeBreakdown;
    }
}
