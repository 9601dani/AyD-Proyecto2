package com.bugtrackers.ms_user.services;

import com.bugtrackers.ms_user.exceptions.ServiceNotSaveException;
import com.bugtrackers.ms_user.models.Service;
import com.bugtrackers.ms_user.repositories.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServiceService serviceService;

    private List<Service> mockServices;

    private Service mockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockServices = List.of(
                new Service(1,"Service 1", "description", new BigDecimal(1.0), "pageInformation", 1, true, LocalDateTime.now()),
                new Service(2,"Service 2", "description2", new BigDecimal(2.0), "pageInformation2", 2, false,LocalDateTime.now())
        );
        mockService = new Service(1, "Service 1", "description", new BigDecimal(1.0), "pageInformation", 1, true, LocalDateTime.now());
    }

    @Test
    void testGetAllServicesSuccess() {
        when(serviceRepository.findAll()).thenReturn(mockServices);

        List<Service> services = serviceService.getAllServices();

        assertNotNull(services);
        assertEquals(2, services.size());
        assertEquals("Service 1", services.get(0).getName());
    }

    @Test
    void testGetAllServicesNoServicesFound() {
        when(serviceRepository.findAll()).thenReturn(List.of());

        ServiceNotSaveException exception = assertThrows(ServiceNotSaveException.class, () -> {
            serviceService.getAllServices();
        });

        String expectedMessage = "No se encontraron servicios!";
        String actualMessage = exception.getMessage();


        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testSaveServiceSuccess() {
        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.empty());
        when(serviceRepository.save(mockService)).thenReturn(mockService);

        Service savedService = serviceService.saveService(mockService);

        assertNotNull(savedService);
        assertEquals(mockService.getId(), savedService.getId());
        assertEquals(mockService.getName(), savedService.getName());

        verify(serviceRepository, times(1)).findById(mockService.getId());
        verify(serviceRepository, times(1)).save(mockService);
    }

    @Test
    void testSaveServiceServiceAlreadyExists() {
        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.of(mockService));

        ServiceNotSaveException exception = assertThrows(ServiceNotSaveException.class, () -> {
            serviceService.saveService(mockService);
        });

        String expectedMessage = "El servicio ya existe!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        verify(serviceRepository, times(0)).save(mockService);
    }

    @Test
    void testGetServiceByIdSuccess() {
        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.of(mockService));

        Service service = serviceService.getServiceById(mockService.getId());

        assertNotNull(service);
        assertEquals(mockService.getId(), service.getId());
        assertEquals(mockService.getName(), service.getName());

        verify(serviceRepository, times(1)).findById(mockService.getId());
    }

    @Test
    void testGetServiceByIdServiceNotFound() {
        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.empty());

        ServiceNotSaveException exception = assertThrows(ServiceNotSaveException.class, () -> {
            serviceService.getServiceById(mockService.getId());
        });

        String expectedMessage = "El servicio no se encontro!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        verify(serviceRepository, times(1)).findById(mockService.getId());
    }

    @Test
    void testUpdateServiceSuccess() {
        Service updatedService = new Service(1, "Updated Service", "Updated description", new BigDecimal(2.0), "Updated pageInformation", 2, false, LocalDateTime.now());

        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.of(mockService));
        when(serviceRepository.save(any(Service.class))).thenReturn(updatedService);

        Service result = serviceService.updateService(mockService.getId(), updatedService);

        assertNotNull(result);
        assertEquals("Updated Service", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(new BigDecimal(2.0), result.getPrice());
        assertEquals("Updated pageInformation", result.getPageInformation());
        assertEquals(2, result.getTimeAprox());
        assertFalse(result.getIsAvailable());

        verify(serviceRepository, times(1)).findById(mockService.getId());
        verify(serviceRepository, times(1)).save(any(Service.class));
    }

    @Test
    void testUpdateServiceServiceNotFound() {
        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.empty());

        ServiceNotSaveException exception = assertThrows(ServiceNotSaveException.class, () -> {
            serviceService.updateService(mockService.getId(), mockService);
        });

        String expectedMessage = "El servicio no se encontro!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        verify(serviceRepository, times(1)).findById(mockService.getId());
        verify(serviceRepository, times(0)).save(any(Service.class));
    }

}
