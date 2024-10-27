package com.bugtrackers.ms_user.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bugtrackers.ms_user.dto.request.AppointmentRequest;
import com.bugtrackers.ms_user.dto.response.AppointmentResponse;
import com.bugtrackers.ms_user.exceptions.EmployeeNotFoundException;
import com.bugtrackers.ms_user.exceptions.ResourceNotFoundException;
import com.bugtrackers.ms_user.exceptions.UserNotFoundException;
import com.bugtrackers.ms_user.models.Appointment;
import com.bugtrackers.ms_user.models.AppointmentHasService;
import com.bugtrackers.ms_user.models.Employee;
import com.bugtrackers.ms_user.models.Resource;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.repositories.AppointmentRepository;
import com.bugtrackers.ms_user.repositories.AppointmentServiceRepository;
import com.bugtrackers.ms_user.repositories.EmployeeRepository;
import com.bugtrackers.ms_user.repositories.ResourceRepository;
import com.bugtrackers.ms_user.repositories.ServiceRepository;
import com.bugtrackers.ms_user.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final EmployeeRepository employeeRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;

    public AppointmentResponse save(AppointmentRequest appointmentRequest) {

        Appointment appointment = new Appointment();

        Optional<User> uOptional = this.userRepository.findById(appointmentRequest.userId());

        if(uOptional.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        appointment.setUser(uOptional.get());

        if(appointmentRequest.resourceId() != null) {
            Optional<Resource> rOptional = this.resourceRepository.findById(appointmentRequest.resourceId());

            if(rOptional.isEmpty()) {
                throw new ResourceNotFoundException("Recurso no encontrado.");
            }

            appointment.setResource(rOptional.get());
        }

        if(appointmentRequest.employeeId() != null) {
            Optional<Employee> eOptional = this.employeeRepository.findById(appointmentRequest.employeeId());

            if(eOptional.isEmpty()) {
                throw new EmployeeNotFoundException("Empleado no encontrado.");
            }

            appointment.setEmployee(eOptional.get());
        }

        appointment.setStartTime(LocalDateTime.parse(appointmentRequest.startTime()));
        appointment.setEndTime(LocalDateTime.parse(appointmentRequest.endTime()));
        
        BigDecimal total = BigDecimal.ZERO;
        
        List<AppointmentHasService> appoinmentServices = new ArrayList<>();
        List<com.bugtrackers.ms_user.models.Service> services = this.serviceRepository.findAllByIdIn(appointmentRequest.servicesId());
        
        total = services.stream().map(com.bugtrackers.ms_user.models.Service::getPrice)
        .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        appointment.setTotal(total);
        
        Appointment saved = this.appointmentRepository.save(appointment);

        services.forEach(service -> {
            AppointmentHasService aHasService = new AppointmentHasService();
            aHasService.setAppointment(saved);
            aHasService.setService(service);
            aHasService.setPrice(service.getPrice());
            aHasService.setTimeAprox(service.getTimeAprox());
            appoinmentServices.add(aHasService);
            
        });
        
        List<AppointmentHasService> saveAll = this.appointmentServiceRepository.saveAll(appoinmentServices);
        saved.setAppointmentHasServices(saveAll);

        return new AppointmentResponse(saved);
    }

    public List<AppointmentResponse> findByResourceOrEmployee(Integer resourceId, Integer employeeId) {
        List<Appointment> appointments = this.appointmentRepository.findByResourceIdOrEmployeeId(resourceId, employeeId);
        return appointments.stream().map(AppointmentResponse::new).toList();
    }
    
}
