package com.bugtrackers.ms_user.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bugtrackers.ms_user.dto.request.AppointmentRequest;
import com.bugtrackers.ms_user.dto.response.AppointmentResponse;
import com.bugtrackers.ms_user.exceptions.EmployeeNotFoundException;
import com.bugtrackers.ms_user.exceptions.ResourceNotFoundException;
import com.bugtrackers.ms_user.exceptions.UserNotFoundException;
import com.bugtrackers.ms_user.models.Appointment;
import com.bugtrackers.ms_user.models.Employee;
import com.bugtrackers.ms_user.models.Resource;
import com.bugtrackers.ms_user.models.Service;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.repositories.AppointmentRepository;
import com.bugtrackers.ms_user.repositories.AppointmentServiceRepository;
import com.bugtrackers.ms_user.repositories.EmployeeRepository;
import com.bugtrackers.ms_user.repositories.ResourceRepository;
import com.bugtrackers.ms_user.repositories.ServiceRepository;
import com.bugtrackers.ms_user.repositories.UserRepository;

public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AppointmentServiceRepository appointmentServiceRepository;

    @InjectMocks
    private AppointmentService appointmentService;
    private List<Service> mockServices;

    private List<Appointment> mAppointments;
    private List<Resource> mResources;
    private List<Employee> mEmployees;
    private User mockUser;
    private Resource mockResource;
    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.mockUser = new User(1, "email", "username", "password", "token", true, true, true, LocalDateTime.now());
        this.mockResource = new Resource(1, "name", "image", List.of(), List.of());
        this.mockEmployee = new Employee(1, "name", "lastname", LocalDate.now(), mockUser, List.of());
        this.mAppointments = List.of(
            new Appointment(1, mockUser, mockResource, BigDecimal.ZERO, "PENDING", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), mockEmployee, List.of()),
            new Appointment(2, mockUser, mockResource, BigDecimal.ZERO, "PENDING", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), mockEmployee, List.of()),
            new Appointment(3, mockUser, mockResource, BigDecimal.ZERO, "PENDING", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), mockEmployee, List.of()),
            new Appointment(4, mockUser, mockResource, BigDecimal.ZERO, "PENDING", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), mockEmployee, List.of())
        );

        mResources = List.of(
                new Resource(1, "name", "image", List.of(), List.of()),
                new Resource(2, "name", "image", List.of(), List.of()),
                new Resource(3, "name", "image", List.of(), List.of())
                
        );

        mEmployees = List.of(
                new Employee(1, "name", "lastname", LocalDate.now(), mockUser, List.of()),
                new Employee(2, "name", "lastname", LocalDate.now(), mockUser, List.of()),
                new Employee(3, "name", "lastname", LocalDate.now(), mockUser, List.of()),
                new Employee(4, "name", "lastname", LocalDate.now(), mockUser, List.of())
        );

        mockServices = List.of(
                new Service(1, "Service 1", "description", new BigDecimal(1.0), "pageInformation", 1, true, LocalDateTime.now(), mResources, mEmployees),
                new Service(2, "Service 2", "description2", new BigDecimal(2.0), "pageInformation2", 2, false, LocalDateTime.now(), mResources, mEmployees)
        );
    }

    @Test
    void testFindByResourceOrEmployee() {
        when(this.appointmentRepository.findByResourceIdOrEmployeeId(1, 1)).thenReturn(mAppointments);

        List<AppointmentResponse> expected = this.mAppointments.stream().map(AppointmentResponse::new).toList();
        List<AppointmentResponse> actual = this.appointmentService.findByResourceOrEmployee(1, 1);

        assertEquals(expected, actual);
    }

    @Test
    void shouldSave() {
        AppointmentRequest request = new AppointmentRequest(1, 1, "2024-02-27T18:14:01.184", "2024-02-27T19:14:01.184", 1, List.of(1, 2));

        Appointment saved = new Appointment(1, mockUser, mockResource, BigDecimal.ZERO, "PENDING", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), mockEmployee, List.of());
        List<Integer> ids = List.of(1,2);
        when(this.userRepository.findById(1)).thenReturn(Optional.of(this.mockUser));
        when(this.resourceRepository.findById(1)).thenReturn(Optional.of(this.mockResource));
        when(this.employeeRepository.findById(1)).thenReturn(Optional.of(this.mockEmployee));
        when(this.serviceRepository.findAllByIdIn(ids)).thenReturn(mockServices);
        when(this.appointmentRepository.save(any(Appointment.class))).thenReturn(saved);

        AppointmentResponse expected = new AppointmentResponse(saved);

        AppointmentResponse actual = this.appointmentService.save(request);
        assertEquals(expected, actual);
    }

    @Test
    void shouldSaveWithoutResourceAndEmployee() {
        AppointmentRequest request = new AppointmentRequest(1, null, "2024-02-27T18:14:01.184", "2024-02-27T19:14:01.184", null, List.of(1, 2));

        Appointment saved = new Appointment(1, mockUser, null, BigDecimal.ZERO, "PENDING", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), null, List.of());
        List<Integer> ids = List.of(1,2);
        when(this.userRepository.findById(1)).thenReturn(Optional.of(this.mockUser));
        when(this.serviceRepository.findAllByIdIn(ids)).thenReturn(mockServices);
        when(this.appointmentRepository.save(any(Appointment.class))).thenReturn(saved);

        AppointmentResponse expected = new AppointmentResponse(saved);

        AppointmentResponse actual = this.appointmentService.save(request);
        assertEquals(expected, actual);
    }

    @Test
    void shouldNotFindUser() {
        AppointmentRequest request = new AppointmentRequest(1, 1, "2024-02-27T18:14:01.184", "2024-02-27T19:14:01.184", 1, List.of(1, 2));
        when(this.userRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            this.appointmentService.save(request);
        });

        String expectedMessage = "Usuario no encontrado.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldNotFindResource() {
        AppointmentRequest request = new AppointmentRequest(1, 1, "2024-02-27T18:14:01.184", "2024-02-27T19:14:01.184", 1, List.of(1, 2));
        when(this.userRepository.findById(1)).thenReturn(Optional.of(this.mockUser));
        when(this.resourceRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            this.appointmentService.save(request);
        });

        String expectedMessage = "Recurso no encontrado.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotFindEmployee() {
        AppointmentRequest request = new AppointmentRequest(1, 1, "2024-02-27T18:14:01.184", "2024-02-27T19:14:01.184", 1, List.of(1, 2));
        when(this.userRepository.findById(1)).thenReturn(Optional.of(this.mockUser));
        when(this.resourceRepository.findById(1)).thenReturn(Optional.of(this.mockResource));
        when(this.employeeRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            this.appointmentService.save(request);
        });

        String expectedMessage = "Empleado no encontrado.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}
