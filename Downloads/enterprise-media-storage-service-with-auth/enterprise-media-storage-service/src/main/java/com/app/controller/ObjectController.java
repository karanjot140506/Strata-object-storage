package com.app.controller;

import com.app.dto.CopyRequest;
import com.app.dto.MoveRequest;
import com.app.dto.RenameRequest;
import com.app.entity.FileMetadata;
import com.app.exception.MetadataNotFoundException;
import com.app.repository.FileMetadataRepository;
import com.app.service.ObjectService;
import com.app.validator.UploadValidator;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/objects")
public class ObjectController {

    private final ObjectService objectService;
    private final FileMetadataRepository repository;

    public ObjectController(ObjectService objectService, FileMetadataRepository repository) {
        this.objectService = objectService;
        this.repository = repository;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileMetadata> upload(@RequestParam MultipartFile file, @RequestParam String bucketName, @RequestParam String uploadedBy) {
        UploadValidator.validate(file, bucketName, uploadedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(objectService.upload(file, bucketName, uploadedBy));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String id) {

        FileMetadata metadata = repository.findById(id).orElseThrow(() -> new MetadataNotFoundException(id));

        InputStream stream = objectService.download(id);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getOriginalName() + "\"").contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(stream));
    }

    @GetMapping
    public List<String> listObjects(@RequestParam String bucketName) {
        return objectService.listObjects(bucketName);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        objectService.delete(id);
        return ResponseEntity.ok("Deleted Successfully");
    }

    @PostMapping("/copy")
    public ResponseEntity<String> copy(@Valid @RequestBody CopyRequest request) {
        objectService.copy(request);
        return ResponseEntity.ok("Copied Successfully");
    }

    @PostMapping("/move")
    public ResponseEntity<String> move(@Valid @RequestBody MoveRequest request) {
        objectService.move(request);
        return ResponseEntity.ok("Moved Successfully");
    }

    @PostMapping("/rename")
    public ResponseEntity<String> rename(@Valid @RequestBody RenameRequest request) {
        objectService.rename(request);
        return ResponseEntity.ok("Renamed Successfully");
    }
}