package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.dto.request.ResourceRequest;
import com.bugtrackers.ms_user.dto.response.AttributeResponse;
import com.bugtrackers.ms_user.dto.response.ResourceResponse;
import com.bugtrackers.ms_user.models.Attribute;
import com.bugtrackers.ms_user.services.ResourceService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resource")
@AllArgsConstructor
public class ResourcesController {
    private final ResourceService resourceService;


    @GetMapping("/attributes")
    public ResponseEntity<List<AttributeResponse>> getAttributes() {
        List<AttributeResponse> attributes = this.resourceService.getAttributes();
        return ResponseEntity.ok(attributes);
    }

    @PostMapping("/attributes")
    public ResponseEntity<AttributeResponse> createAttribute(@RequestBody Attribute attribute) {
        AttributeResponse newAttribute = this.resourceService.createAttribute(attribute);
        return ResponseEntity.ok(newAttribute);
    }

    /*
    * ROUTES FOR RESOURCES
    * */
    @PostMapping("")
    @Transactional
    public ResponseEntity<ResourceResponse> createResource(@RequestBody ResourceRequest resourceRequest) {
        ResourceResponse resourceResponse = this.resourceService.createResource(resourceRequest);
        return ResponseEntity.ok(resourceResponse);
    }

    @GetMapping("")
    @Transactional
    public ResponseEntity<List<ResourceResponse>> getResources() {
        List<ResourceResponse> resources = this.resourceService.getResources();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<ResourceResponse> getResourceById(@PathVariable Integer id) {
        ResourceResponse resource = this.resourceService.getResourceById(id);
        return ResponseEntity.ok(resource);
    }


    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ResourceResponse> updateResource(@PathVariable Integer id, @RequestBody ResourceRequest resourceRequest) {
        ResourceResponse resource = this.resourceService.updateResource(id, resourceRequest);
        return ResponseEntity.ok(resource);
    }



}
