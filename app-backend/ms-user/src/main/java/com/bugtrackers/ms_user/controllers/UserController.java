package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.dto.request.RequestString;
import com.bugtrackers.ms_user.dto.request.UserAllRequest;
import com.bugtrackers.ms_user.dto.response.ModuleResponse;
import com.bugtrackers.ms_user.dto.response.UserAllResponse;
import com.bugtrackers.ms_user.services.UserService;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    
    private final UserService userService;

    @GetMapping("/pages/{id}")
    public ResponseEntity<List<ModuleResponse>> getPages(@PathVariable Integer id) {
        List<ModuleResponse> moduleResponse = this.userService.getPages(id);
        return ResponseEntity.ok(moduleResponse);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserAllResponse> getById(@PathVariable Integer id) {
        UserAllResponse userAllResponse = this.userService.getById(id);
        return ResponseEntity.ok(userAllResponse);
    }
    
    @PutMapping("/profile/{id}")
    public ResponseEntity<UserAllResponse> updateProfile(@PathVariable Integer id, @RequestBody UserAllRequest userAllRequest) {
        UserAllResponse userAllResponse = this.userService.updateProfile(id, userAllRequest);
        return ResponseEntity.ok(userAllResponse);
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<Map<String, String>> getInfo(@PathVariable Integer id) {
        String pathImage = this.userService.getInfo(id);
        Map<String, String> map = new HashMap<>();
        map.put("path", pathImage);
        return ResponseEntity.ok(map);
    }



    @PutMapping("/profile/img/{id}")
    public ResponseEntity<Map<String, String>> updateImageProfile(@PathVariable Integer id, @RequestBody RequestString imageUpdateRequest) {
        String pathImage = imageUpdateRequest.getMessage();
        this.userService.updateImageProfile(id, pathImage);
        Map<String, String> map = new HashMap<>();
        map.put("path", pathImage);
        return ResponseEntity.ok(map);
    }


}
