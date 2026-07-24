package com.qms.qms.storage;

import com.qms.qms.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
public class R2StorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif");

    private final S3Client r2Client;
    private final ExecutorService uploadExecutor;
    private final String bucket;
    private final String publicBaseUrl;

    public R2StorageService(S3Client r2Client,
                             ExecutorService r2UploadExecutor,
                             @Value("${r2.bucket}") String bucket,
                             @Value("${r2.public-base-url}") String publicBaseUrl) {
        this.r2Client = r2Client;
        this.uploadExecutor = r2UploadExecutor;
        this.bucket = bucket;
        this.publicBaseUrl = publicBaseUrl;
    }

    /** Uploads every file concurrently on the shared pool; returns URLs in the same order as the input. */
    public List<String> uploadAll(List<MultipartFile> files) {
        List<CompletableFuture<String>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> uploadOne(file), uploadExecutor))
                .toList();

        try {
            return futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
        } catch (CompletionException e) {
            if (e.getCause() instanceof RuntimeException re) {
                throw re;
            }
            throw new StorageException("Failed to upload images", e.getCause());
        }
    }

    public String uploadOne(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported image type: " + contentType);
        }

        String key = "qa-ticket/" + UUID.randomUUID() + extensionOf(file.getOriginalFilename());
        try {
            r2Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(contentType)
                            .contentLength(file.getSize())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new StorageException("Failed to read uploaded file: " + file.getOriginalFilename(), e);
        } catch (RuntimeException e) {
            throw new StorageException("Failed to upload file to R2: " + file.getOriginalFilename(), e);
        }

        return publicBaseUrl + "/" + key;
    }

    private String extensionOf(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        int dot = originalFilename.lastIndexOf('.');
        return dot >= 0 ? originalFilename.substring(dot) : "";
    }
}
