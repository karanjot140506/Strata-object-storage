package com.app.exception;

public class InvalidBucketException
        extends StorageException {

    public InvalidBucketException(
            String message) {

        super(message);
    }
}