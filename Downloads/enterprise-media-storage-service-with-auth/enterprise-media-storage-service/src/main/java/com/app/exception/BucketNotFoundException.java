package com.app.exception;

public class BucketNotFoundException extends StorageException {

	public BucketNotFoundException(String bucketName) {

		super("Bucket not found : " + bucketName);
	}
}