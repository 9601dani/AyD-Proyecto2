package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.dto.request.ServiceRequest;
import com.bugtrackers.ms_user.dto.response.ServiceResponse;
import com.bugtrackers.ms_user.models.Service;
import com.bugtrackers.ms_user.services.ServiceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@AllArgsConstructor
public class ServicesController {
    private final ServiceService serviceService;

    @GetMapping("")
    public ResponseEntity<List<Service>> getServices() {
        List<Service> services = this.serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getServiceById(@PathVariable Integer id) {
        ServiceResponse service = this.serviceService.getServiceById(id);
        return ResponseEntity.ok(service);
    }

    @PostMapping("")
    @Transactional
    public ResponseEntity<Service> createService(@RequestBody ServiceRequest service) {
            Service serviceCreated = this.serviceService.saveService(service);
            return ResponseEntity.ok(serviceCreated);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ServiceResponse> updateService(@PathVariable Integer id, @RequestBody ServiceRequest service) {
        ServiceResponse serviceUpdated = this.serviceService.updateService(id, service);
        return ResponseEntity.ok(serviceUpdated);
    }
}
