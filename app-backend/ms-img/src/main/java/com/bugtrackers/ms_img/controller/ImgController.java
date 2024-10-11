package com.bugtrackers.ms_img.controller;

import com.bugtrackers.ms_img.services.CloudService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import java.util.UUID;

@RestController
@RequestMapping("/img")
public class ImgController {

    @Autowired
    private CloudService storageService;

    @GetMapping("")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok().body("Hello World Img Controller!");
    }

    @PostMapping("/upload-test")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String objectName = storageService.uploadImage(file);
            return storageService.getPublicUrl(objectName);
        } catch (IOException e) {
            return "Error al subir la imagen: " + e.getMessage();
        }
    }

    @PostMapping("/upload")
    public List<String> uploadMultipleImages(@RequestParam("files") MultipartFile[] files) {
        return List.of(files).stream().map(file -> {
            try {
                String uniqueFileName = UUID.randomUUID().toString();
                String objectName = storageService.uploadImage(file);
                return storageService.getPublicUrl(objectName);
            } catch (IOException e) {
                return "Error al subir: " + file.getOriginalFilename();
            }
        }).collect(Collectors.toList());
    }
    
    
}
