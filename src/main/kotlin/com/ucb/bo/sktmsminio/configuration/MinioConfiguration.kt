package com.ucb.bo.sktmsminio.configuration

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class MinioConfiguration {
    @Value("\${minio.access-key}")
    lateinit var accessKey: String

    @Value("\${minio.secret-key}")
    lateinit var secretKey: String

    @Value("\${minio.url}")
    lateinit var minioUrl: String

    @Bean
    @Primary
    fun minioClient(): MinioClient {
        return MinioClient.Builder()
            .credentials(accessKey, secretKey)
            .endpoint(minioUrl)
            .build();
    }
}