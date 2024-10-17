package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.models.Service;
import com.bugtrackers.ms_user.services.ServiceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Service> getServiceById(@PathVariable Integer id) {
        Service service = this.serviceService.getServiceById(id);
        return ResponseEntity.ok(service);
    }

    @PostMapping("")
    public ResponseEntity<Service> createService(@RequestBody Service service) {
            Service serviceCreated = this.serviceService.saveService(service);
            return ResponseEntity.ok(serviceCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Integer id, @RequestBody Service service) {
        Service serviceUpdated = this.serviceService.updateService(id, service);
        return ResponseEntity.ok(serviceUpdated);
    }
}
