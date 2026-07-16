package com.app.service.impl;

import com.app.dto.FileTypeStatisticsResponse;
import com.app.dto.StorageStatisticsResponse;
import com.app.entity.FileMetadata;
import com.app.entity.FileStatus;
import com.app.repository.FileMetadataRepository;
import com.app.service.BucketService;
import com.app.service.StorageStatisticsService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StorageStatisticsServiceImpl implements StorageStatisticsService {

    private final FileMetadataRepository fileMetadataRepository;
    private final BucketService bucketService;

    public StorageStatisticsServiceImpl(FileMetadataRepository fileMetadataRepository, BucketService bucketService) {
        this.fileMetadataRepository = fileMetadataRepository;
        this.bucketService = bucketService;
    }

    @Override
    public StorageStatisticsResponse getStatistics() {

        List<FileMetadata> allFiles = fileMetadataRepository.findAll();

        StorageStatisticsResponse response = new StorageStatisticsResponse();

        response.setTotalFiles(allFiles.size());
        response.setActiveFiles(fileMetadataRepository.countByStatus(FileStatus.ACTIVE));
        response.setArchivedFiles(fileMetadataRepository.countByStatus(FileStatus.ARCHIVED));
        response.setDeletedFiles(fileMetadataRepository.countByStatus(FileStatus.DELETED));

        long totalBytes = allFiles.stream()
                .filter(f -> f.getFileSize() != null)
                .mapToLong(FileMetadata::getFileSize)
                .sum();
        response.setTotalStorageBytes(totalBytes);

        response.setTotalBuckets(bucketService.getAllBuckets().size());

        Map<String, Long> filesPerBucket = allFiles.stream()
                .filter(f -> f.getBucketName() != null)
                .collect(Collectors.groupingBy(FileMetadata::getBucketName, Collectors.counting()));
        response.setFilesPerBucket(filesPerBucket);

        Map<String, List<FileMetadata>> byType = allFiles.stream()
                .filter(f -> f.getFileType() != null)
                .collect(Collectors.groupingBy(FileMetadata::getFileType));

        List<FileTypeStatisticsResponse> breakdown = byType.entrySet().stream()
                .map(entry -> {
                    long count = entry.getValue().size();
                    long size = entry.getValue().stream()
                            .filter(f -> f.getFileSize() != null)
                            .mapToLong(FileMetadata::getFileSize)
                            .sum();
                    return new FileTypeStatisticsResponse(entry.getKey(), count, size);
                })
                .sorted(Comparator.comparingLong(FileTypeStatisticsResponse::getCount).reversed())
                .collect(Collectors.toList());

        response.setFileTypeBreakdown(breakdown);

        return response;
    }
}
