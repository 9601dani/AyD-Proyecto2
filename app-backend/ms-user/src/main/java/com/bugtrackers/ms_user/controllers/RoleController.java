package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.dto.request.RoleRequest;
import com.bugtrackers.ms_user.dto.response.PageResponse;
import com.bugtrackers.ms_user.dto.response.RoleResponse;
import com.bugtrackers.ms_user.services.RoleService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import com.bugtrackers.ms_user.dto.request.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.HashMap;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/role")
@AllArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("")
    public ResponseEntity<List<RoleResponse>> getRoles() {
        List<RoleResponse> roles = this.roleService.getRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("")
    public ResponseEntity<RoleResponse> saveRole(@RequestBody @Valid RoleRequest body) {
        RoleResponse newRole = this.roleService.saveRole(body);
        return ResponseEntity.ok(newRole);
    }

    @GetMapping("/find-pages/all")
    public ResponseEntity<List<PageResponse>> getAllPages() {
        List<PageResponse> response = this.roleService.getAllPages();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find-pages/{id}")
    public ResponseEntity<List<PageResponse>> getPagesByRoleId(@PathVariable Integer id) {
        List<PageResponse> response = this.roleService.getPagesByRoleId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @Transactional
    public ResponseEntity<HashMap<String, String>> updatePages(@PathVariable Integer id, @RequestBody List<PageRequest> pageRequests) {
        String message = this.roleService.updatePages(id, pageRequests);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
    
    
}
