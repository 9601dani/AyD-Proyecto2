package com.bugtrackers.ms_user.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.bugtrackers.ms_user.models.Appointment;
import com.bugtrackers.ms_user.models.AppointmentHasService;
import com.bugtrackers.ms_user.models.Service;

public record AppointmentResponse(
        Integer id,
        String username,
        String employeeFirstName,
        String resourceName,
        BigDecimal total,
        String state,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<String> serviceNames) {

    public AppointmentResponse(Appointment appointment) {
        this(
                appointment.getId(),
                appointment.getUser().getUsername(),
                appointment.getEmployee() != null ? appointment.getEmployee().getFirstName() : "Sin empleado",
                appointment.getResource() != null ? appointment.getResource().getName() : "Sin recurso",
                appointment.getTotal(),
                appointment.getState(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getAppointmentHasServices().stream()
                        .map(AppointmentHasService::getService)
                        .map(Service::getName)
                        .toList());
    }

}
