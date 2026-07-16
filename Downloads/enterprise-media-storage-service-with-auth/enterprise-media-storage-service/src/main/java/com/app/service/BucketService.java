package com.app.service;


import java.util.List;

public interface BucketService {

    void createBucket(String bucketName);

    void deleteBucket(String bucketName);

    List<String> getAllBuckets();

    boolean bucketExists(String bucketName);
}