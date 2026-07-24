package com.qms.qms.controller;

import com.qms.qms.storage.R2StorageService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private final R2StorageService r2StorageService;

    public UploadController(R2StorageService r2StorageService) {
        this.r2StorageService = r2StorageService;
    }

    /**
     * Uploads one or more images to R2 in parallel and returns their public URLs
     * (same order as the submitted files) to be embedded in a QA ticket's defect images.
     */
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<String> uploadImages(@RequestParam("files") @NotEmpty List<MultipartFile> files) {
        return r2StorageService.uploadAll(files);
    }
}
