package com.bugtrackers.ms_img.services;

import com.bugtrackers.ms_img.exceptions.FileNotCreatedException;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Bucket;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CloudService {
    
    private final Storage storage;
    private final String bucketName;

    public String uploadImage(MultipartFile file) {
        try {

            String objectName = "images/" + UUID.randomUUID().toString();
            Bucket bucket = storage.get(bucketName);
            bucket.create(objectName, file.getBytes(), file.getContentType());
            return objectName;
        } catch(IOException exception) {
            throw new FileNotCreatedException("No se pudo guardar la imagen");
        }
    }

    public String uploadProfileImage(MultipartFile file, String nameOldImage) {
        
        return "";
    
    }
}
