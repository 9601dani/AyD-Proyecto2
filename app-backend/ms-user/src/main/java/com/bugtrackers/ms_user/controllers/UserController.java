package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.dto.request.RequestString;
import com.bugtrackers.ms_user.dto.request.UserAllRequest;
import com.bugtrackers.ms_user.dto.response.AppointmentResponse;
import com.bugtrackers.ms_user.dto.response.ModuleResponse;
import com.bugtrackers.ms_user.dto.response.PopularityResponse;
import com.bugtrackers.ms_user.dto.response.UserAllResponse;
import com.bugtrackers.ms_user.services.UserService;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PutMapping("/set-2fa/{id}")
    public ResponseEntity<HashMap<String, String>> set2fa(@PathVariable Integer id) {
        String message = this.userService.set2fa(id);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(@PathVariable Integer id) {
        List<AppointmentResponse> appointmentResponse = this.userService.getMyAppointments(id);
        return ResponseEntity.ok(appointmentResponse);
    }

    @GetMapping("/report/popularity")
    public ResponseEntity<List<PopularityResponse>> getPopularity() {
        List<PopularityResponse> popularityResponse = this.userService.getPopularity();
        return ResponseEntity.ok(popularityResponse);
    }

    @GetMapping("/report/users")
    public ResponseEntity<List<PopularityResponse>> getUsersByRole() {
        List<PopularityResponse> popularityResponse = this.userService.getUserByRole();
        return ResponseEntity.ok(popularityResponse);
    }

    @GetMapping("/report/resources")
    public ResponseEntity<List<PopularityResponse>> getPopularityResources() {
        List<PopularityResponse> popularityResponse = this.userService.getPopularityResources();
        return ResponseEntity.ok(popularityResponse);
    }



}
