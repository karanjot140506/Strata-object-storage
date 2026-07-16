package com.app.controller;

import com.app.entity.FileMetadata;
import com.app.entity.FileStatus;
import com.app.service.MetadataService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metadata")
public class MetadataController {

    private final MetadataService service;

    public MetadataController(MetadataService service) {
        this.service = service;
    }

    @GetMapping
    public Page<FileMetadata> getAll(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "uploadedAt") String sortBy,
                                     @RequestParam(defaultValue = "desc") String direction) {

        return service.getAllFiles(page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public FileMetadata getById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/search")
    public Page<FileMetadata> searchByName(@RequestParam String name,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {

        return service.searchByFileName(name, page, size);
    }

    @GetMapping("/bucket/{bucketName}")
    public Page<FileMetadata> searchByBucket(@PathVariable String bucketName,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {

        return service.searchByBucket(bucketName, page, size);
    }

    @GetMapping("/user/{uploadedBy}")
    public Page<FileMetadata> searchByUser(@PathVariable String uploadedBy,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {

        return service.searchByUser(uploadedBy, page, size);
    }

    @GetMapping("/status/{status}")
    public Page<FileMetadata> searchByStatus(@PathVariable FileStatus status,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {

        return service.searchByStatus(status, page, size);
    }

    @GetMapping("/type/{fileType}")
    public Page<FileMetadata> searchByFileType(@PathVariable String fileType,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {

        return service.searchByFileType(fileType, page, size);
    }

    @GetMapping("/count")
    public long count() {
        return service.count();
    }

    @GetMapping("/bucket/{bucketName}/count")
    public long countByBucket(@PathVariable String bucketName) {
        return service.countByBucket(bucketName);
    }
}