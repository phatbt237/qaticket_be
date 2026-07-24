package com.qms.qms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class R2Config {

    @Bean
    public S3Client r2Client(@Value("${r2.endpoint}") String endpoint,
                              @Value("${r2.access-key}") String accessKey,
                              @Value("${r2.secret-key}") String secretKey) {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    /** Bounded pool so concurrent multi-image uploads run in parallel instead of one-by-one. */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService r2UploadExecutor(@Value("${r2.upload-thread-pool-size}") int poolSize) {
        return Executors.newFixedThreadPool(poolSize);
    }
}
