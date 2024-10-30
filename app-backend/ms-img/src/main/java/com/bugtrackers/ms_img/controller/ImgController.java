package com.bugtrackers.ms_img.controller;

import com.bugtrackers.ms_img.dto.response.ResponseString;
import com.bugtrackers.ms_img.services.CloudService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PutMapping("/upload/profile")
    public ResponseEntity<ResponseString> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nameOldImage") String nameOldImage) {
        String new_image_path = cloudService.uploadProfileImage(file, nameOldImage);
        return ResponseEntity.ok(new ResponseString(new_image_path));
    }

    @PostMapping("/upload/resource")
    public ResponseEntity<ResponseString> uploadResourceImage(@RequestParam("file") MultipartFile file) {
        String objectName = cloudService.uploadResourceImage(file);
        return ResponseEntity.ok(new ResponseString(objectName));
    }

    @PutMapping("/upload/resource")
    public ResponseEntity<ResponseString> updateResourceImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nameOldImage") String nameOldImage) {
        String new_image_path = cloudService.updateResourceImage(file, nameOldImage);
        return ResponseEntity.ok(new ResponseString(new_image_path));
    }
}
