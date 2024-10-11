package com.bugtrackers.ms_img.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class CloudService {
    
    private final Storage storage;
    private final String bucketName;

    public CloudService(
            @Value("${gcp.credentials.location}") String credentialsPath,
            @Value("${gcp.bucket_name}") String bucketName,
            ResourceLoader resourceLoader) throws IOException {
        
        InputStream input = resourceLoader.getResource(credentialsPath).getInputStream();
        
        GoogleCredentials credentials = GoogleCredentials.fromStream(input);
        this.storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        this.bucketName = bucketName;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        String objectName = "images/" + UUID.randomUUID().toString();
        Bucket bucket = storage.get(bucketName);
        bucket.create(objectName, file.getBytes(), file.getContentType());
        return objectName;
    }

    public byte[] getImage(String objectName) {
        Blob blob = storage.get(bucketName, objectName);
        return blob.getContent();
    }
}
