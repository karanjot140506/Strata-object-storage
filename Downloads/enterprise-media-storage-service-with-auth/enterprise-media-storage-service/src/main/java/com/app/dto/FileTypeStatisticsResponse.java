package com.app.dto;

public class FileTypeStatisticsResponse {

    private String fileType;
    private long count;
    private long totalSize;

    public FileTypeStatisticsResponse() {
    }

    public FileTypeStatisticsResponse(String fileType, long count, long totalSize) {
        this.fileType = fileType;
        this.count = count;
        this.totalSize = totalSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
}
