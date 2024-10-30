package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.dto.response.BillReportResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugtrackers.ms_user.dto.request.AppointmentRequest;
import com.bugtrackers.ms_user.dto.response.AppointmentResponse;
import com.bugtrackers.ms_user.services.AppointmentService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/appointment")
@AllArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/save")
    @Transactional
    public ResponseEntity<AppointmentResponse> saveAppointment(@RequestBody AppointmentRequest AppointmentRequest) {
        AppointmentResponse response = this.appointmentService.save(AppointmentRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find")
    public ResponseEntity<List<AppointmentResponse>> findByResourceOrEmployee(
            @RequestParam(required = false, defaultValue = "0") Integer resource,
            @RequestParam(required = false, defaultValue = "0") Integer employee) {

        List<AppointmentResponse> response = this.appointmentService.findByResourceOrEmployee(resource, employee);
        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    public ResponseEntity<List<AppointmentResponse>> findAll() {
        List<AppointmentResponse> response = this.appointmentService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bill")
    public ResponseEntity<List<BillReportResponse>> getBill() {
        List<BillReportResponse> response = this.appointmentService.getBill();
        return ResponseEntity.ok(response);
    }

}
