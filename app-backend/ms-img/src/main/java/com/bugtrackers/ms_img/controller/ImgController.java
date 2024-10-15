package com.bugtrackers.ms_img.controller;

import com.bugtrackers.ms_img.services.CloudService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/img")
@AllArgsConstructor
public class ImgController {

    private final CloudService cloudService;

    @GetMapping("")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok().body("Hello World Img Controller!");
    }

    @PostMapping("/upload-test")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String objectName = cloudService.uploadImage(file);
        return ResponseEntity.ok(objectName);
    }

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadMultipleImages(@RequestParam("files") MultipartFile[] files) {
        List<String> response = List.of(files).stream().map(file -> {
            String objectName = cloudService.uploadImage(file);
            return objectName;
        }).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("upload/profile")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file, @RequestParam("nameOldImage") String nameOldImage) {
        String objectName = cloudService.uploadImage(file);
        return ResponseEntity.ok(objectName);
    }


    
}
