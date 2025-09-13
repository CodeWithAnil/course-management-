package com.nt.course_service_lms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Configuration class for creating AWS S3 client beans based on the active Spring profile.
 * <p>
 * Provides separate S3 clients for local and non-local (e.g. production) environments.
 * </p>
 */
@Configuration
public class S3Config {

    /**
     * AWS region used to configure the S3 client.
     * <p>
     * This value is injected from the application properties using the key {@code aws.region}.
     * </p>
     */
    @Value("${aws.secretsmanager.region}")
    private String region;

    /**
     * Creates an {@link S3Client} bean for non-local environments (e.g., development, staging, production).
     * <p>
     * Uses the default AWS credentials provider chain, which supports IAM roles, environment variables, etc.
     * </p>
     *
     * @return an {@link S3Client} instance configured for production environments
     */
    @Bean
    public S3Client s3ClientProd() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
