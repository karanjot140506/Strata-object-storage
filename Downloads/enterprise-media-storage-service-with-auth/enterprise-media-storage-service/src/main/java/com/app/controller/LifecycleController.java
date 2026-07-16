package com.app.controller;

import com.app.dto.LifecycleResponse;
import com.app.entity.FileMetadata;
import com.app.service.LifecycleService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lifecycle")
public class LifecycleController {

    private final LifecycleService lifecycleService;

    public LifecycleController(LifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    @PutMapping("/archive/{id}")
    public LifecycleResponse archive(@PathVariable String id) {
        return lifecycleService.archive(id);
    }

    @PutMapping("/restore/{id}")
    public LifecycleResponse restore(@PathVariable String id) {
        return lifecycleService.restore(id);
    }

    @PutMapping("/soft-delete/{id}")
    public LifecycleResponse softDelete(@PathVariable String id) {
        return lifecycleService.softDelete(id);
    }

    @DeleteMapping("/permanent-delete/{id}")
    public LifecycleResponse permanentDelete(@PathVariable String id) {
        return lifecycleService.permanentDelete(id);
    }

    @GetMapping("/active")
    public Page<FileMetadata> active(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return lifecycleService.getActiveFiles(page, size);
    }

    @GetMapping("/archived")
    public Page<FileMetadata> archived(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return lifecycleService.getArchivedFiles(page, size);
    }

    @GetMapping("/deleted")
    public Page<FileMetadata> deleted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return lifecycleService.getDeletedFiles(page, size);
    }
}