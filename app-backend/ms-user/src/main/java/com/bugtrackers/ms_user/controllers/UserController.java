package com.bugtrackers.ms_user.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugtrackers.ms_user.dto.response.ModuleResponse;
import com.bugtrackers.ms_user.services.UserService;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    
}
