package com.bugtrackers.ms_user.services;

import com.bugtrackers.ms_user.dto.request.ServiceRequest;
import com.bugtrackers.ms_user.dto.response.CreateEmployeeResponse;
import com.bugtrackers.ms_user.dto.response.ResourceResponse;
import com.bugtrackers.ms_user.dto.response.ServiceResponse;
import com.bugtrackers.ms_user.exceptions.ServiceNotSaveException;
import com.bugtrackers.ms_user.models.*;
import com.bugtrackers.ms_user.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private EmployeeHasServiceRepository employeeHasServiceRepository;

    @Mock
    private ResourceHasServiceRepository resourceHasServiceRepository;

    @Mock
    private ResourceHasAttributeRepository resourceHasAttributeRepository;

    @Mock
    private AttributeRepository attributeRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ServiceService serviceService;

    private List<Service> mockServices;
    private Service mockService;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockServices = List.of(
                new Service(1, "Service 1", "description", new BigDecimal(1.0), "pageInformation", 1, true, LocalDateTime.now(), List.of(), List.of()),
                new Service(2, "Service 2", "description2", new BigDecimal(2.0), "pageInformation2", 2, false, LocalDateTime.now(), List.of(), List.of())
        );
        mockService = new Service(1, "Service 1", "description", new BigDecimal(1.0), "pageInformation", 1, true, LocalDateTime.now(), List.of(), List.of());
    }

    @Test
    void testGetServiceByIdSuccess() {
        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.of(mockService));

        List<EmployeeHasService> mockEmployeeHasServices = List.of(
                new EmployeeHasService(1, new Employee(1, "Juan", "Perez", LocalDate.of(1990, 5, 15), new User(), List.of()), mockService),
                new EmployeeHasService(2, new Employee(2, "Ana", "Gomez", LocalDate.of(1985, 11, 20), new User(), List.of()), mockService)
        );
        when(employeeHasServiceRepository.findAll()).thenReturn(mockEmployeeHasServices);

        List<ResourceHasService> mockResourceHasServices = List.of(
                new ResourceHasService(1, new Resource(1, "Recurso 1","img1", List.of(), List.of()), mockService),
                new ResourceHasService(2, new Resource(2, "Recurso 2","img1", List.of(), List.of()), mockService)
        );
        when(resourceHasServiceRepository.findAll()).thenReturn(mockResourceHasServices);

        List<ResourceHasAttribute> mockResourceHasAttributes = List.of(
                new ResourceHasAttribute(1, new Resource(1, "Recurso 1","img1", List.of(), List.of()), new Attribute(1, "Attr 1", "description 1", List.of())),
                new ResourceHasAttribute(2, new Resource(2, "Recurso 2","img1", List.of(), List.of()), new Attribute(2, "Attr 2", "description 2", List.of()))
        );
        when(resourceHasAttributeRepository.findAll()).thenReturn(mockResourceHasAttributes);

        List<Attribute> mockAttributes = List.of(
                new Attribute(1, "Attr 1", "description 1", List.of()),
                new Attribute(2, "Attr 2", "description 2", List.of())
        );
        when(attributeRepository.findAll()).thenReturn(mockAttributes);

        ServiceResponse serviceResponse = serviceService.getServiceById(mockService.getId());

        assertNotNull(serviceResponse);
        assertEquals(mockService.getId(), serviceResponse.id());
        assertEquals(mockService.getName(), serviceResponse.name());
        assertEquals(2, serviceResponse.employees().size());
        assertEquals(2, serviceResponse.resources().size());

        verify(serviceRepository, times(1)).findById(mockService.getId());
        verify(employeeHasServiceRepository, times(1)).findAll();
        verify(resourceHasServiceRepository, times(1)).findAll();
        verify(resourceHasAttributeRepository, times(1)).findAll();
        verify(attributeRepository, times(1)).findAll();
    }

    @Test
    void testGetServiceByIdNotFound() {
        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.empty());

        ServiceNotSaveException exception = assertThrows(ServiceNotSaveException.class, () -> {
            serviceService.getServiceById(mockService.getId());
        });

        String expectedMessage = "El servicio no se encontro!";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testGetAllServicesSuccess() {
        when(serviceRepository.findAll()).thenReturn(mockServices);

        List<ServiceResponse> services = serviceService.getAllServices();

        assertNotNull(services);
        assertEquals(2, services.size());
        assertEquals("Service 1", services.get(0).name());
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
    void testUpdateServiceServiceNotFound() {
        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.empty());

        ServiceNotSaveException exception = assertThrows(ServiceNotSaveException.class, () -> {
            serviceService.updateService(mockService.getId(), new ServiceRequest(
                    mockService.getName(), mockService.getDescription(), mockService.getPrice(),
                    mockService.getPageInformation(), mockService.getTimeAprox(), mockService.getIsAvailable(),
                    List.of(), List.of()
            ));
        });

        String expectedMessage = "El servicio no se encontro!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        verify(serviceRepository, times(1)).findById(mockService.getId());
        verify(serviceRepository, times(0)).save(any(Service.class));
    }

    @Test
    void testUpdateServiceSuccess() {
        List<CreateEmployeeResponse> mockEmployees = List.of(
                new CreateEmployeeResponse(1, "Juan", "Perez", LocalDate.of(1990, 5, 15), "juan.perez@example.com", "juanperez"),
                new CreateEmployeeResponse(2, "Ana", "Gomez", LocalDate.of(1985, 11, 20), "ana.gomez@example.com", "anagomez")
        );

        List<ResourceResponse> mockResources = List.of(
                new ResourceResponse(1, "Resource 1","img1", List.of()),
                new ResourceResponse(2, "Resource 2","img1", List.of())
        );

        ServiceRequest serviceRequest = new ServiceRequest(
                "Updated Service",
                "Updated description",
                new BigDecimal(2.0),
                "Updated pageInformation",
                2,
                false,
                mockEmployees,
                mockResources
        );

        Service updatedService = new Service(
                mockService.getId(),
                "Updated Service",
                "Updated description",
                new BigDecimal(2.0),
                "Updated pageInformation",
                2,
                false,
                LocalDateTime.now(),
                List.of(),
                List.of()
        );

        when(serviceRepository.findById(eq(mockService.getId()))).thenReturn(Optional.of(mockService));
        when(serviceRepository.save(any(Service.class))).thenReturn(updatedService);

        doNothing().when(resourceHasServiceRepository).deleteByServiceId(mockService.getId());
        doNothing().when(employeeHasServiceRepository).deleteByServiceId(mockService.getId());

        when(resourceRepository.findById(1)).thenReturn(Optional.of(new Resource(1, "Resource 1","img1", List.of(), List.of())));
        when(resourceRepository.findById(2)).thenReturn(Optional.of(new Resource(2, "Resource 2","img1", List.of(), List.of())));

        when(employeeRepository.findById(1)).thenReturn(Optional.of(new Employee(1, "Juan", "Perez", LocalDate.of(1990, 5, 15), new User(), List.of())));
        when(employeeRepository.findById(2)).thenReturn(Optional.of(new Employee(2, "Ana", "Gomez", LocalDate.of(1985, 11, 20), new User(), List.of())));

        List<EmployeeHasService> mockEmployeeHasServices = List.of(
                new EmployeeHasService(1, new Employee(1, "Juan", "Perez", LocalDate.of(1990, 5, 15), new User(), List.of()), mockService),
                new EmployeeHasService(2, new Employee(2, "Ana", "Gomez", LocalDate.of(1985, 11, 20), new User(), List.of()), mockService)
        );
        when(employeeHasServiceRepository.findAll()).thenReturn(mockEmployeeHasServices);

        List<ResourceHasService> mockResourceHasServices = List.of(
                new ResourceHasService(1, new Resource(1, "Resource 1","img1", List.of(), List.of()), mockService),
                new ResourceHasService(2, new Resource(2, "Resource 2","img1", List.of(), List.of()), mockService)
        );
        when(resourceHasServiceRepository.findAll()).thenReturn(mockResourceHasServices);

        List<ResourceHasAttribute> mockResourceHasAttributes = List.of(
                new ResourceHasAttribute(1, new Resource(1, "Resource 1","img1", List.of(), List.of()), new Attribute(1, "Attr 1", "description 1", List.of())),
                new ResourceHasAttribute(2, new Resource(2, "Resource 2","img1", List.of(), List.of()), new Attribute(2, "Attr 2", "description 2", List.of()))
        );
        when(resourceHasAttributeRepository.findAll()).thenReturn(mockResourceHasAttributes);

        List<Attribute> mockAttributes = List.of(
                new Attribute(1, "Attr 1", "description 1", List.of()),
                new Attribute(2, "Attr 2", "description 2", List.of())
        );
        when(attributeRepository.findAll()).thenReturn(mockAttributes);

        ServiceResponse result = serviceService.updateService(mockService.getId(), serviceRequest);

        assertNotNull(result);
        assertEquals("Updated Service", result.name());
        assertEquals("Updated description", result.description());
        assertEquals(new BigDecimal(2.0), result.price());
        assertEquals("Updated pageInformation", result.pageInformation());
        assertEquals(2, result.timeAprox());
        assertFalse(result.isAvailable());
        assertEquals(2, result.employees().size());
        assertEquals(2, result.resources().size());

        verify(serviceRepository, times(1)).findById(eq(mockService.getId()));
        verify(serviceRepository, times(1)).save(any(Service.class));
        verify(resourceHasServiceRepository, times(1)).deleteByServiceId(mockService.getId());
        verify(employeeHasServiceRepository, times(1)).deleteByServiceId(mockService.getId());
        verify(employeeHasServiceRepository, times(2)).save(any(EmployeeHasService.class));
        verify(resourceHasServiceRepository, times(2)).save(any(ResourceHasService.class));
    }


    @Test
    void testSaveServiceAlreadyExists() {
        when(serviceRepository.findByName(mockService.getName())).thenReturn(Optional.of(mockService));

        ServiceNotSaveException exception = assertThrows(ServiceNotSaveException.class, () -> {
            serviceService.saveService(new ServiceRequest(
                    mockService.getName(), mockService.getDescription(), mockService.getPrice(),
                    mockService.getPageInformation(), mockService.getTimeAprox(), mockService.getIsAvailable(),
                    List.of(), List.of()
            ));
        });

        String expectedMessage = "El servicio ya existe!";
        assertEquals(expectedMessage, exception.getMessage());

        verify(serviceRepository, times(1)).findByName(mockService.getName());
        verify(serviceRepository, times(0)).save(any(Service.class));
    }

    @Test
    void testUpdateServiceResourceNotFound() {
        ServiceRequest serviceRequest = new ServiceRequest(
                "Updated Service",
                "Updated description",
                new BigDecimal(2.0),
                "Updated pageInformation",
                2,
                false,
                List.of(),
                List.of(new ResourceResponse(1, "Resource 1","img1", List.of()))
        );

        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.of(mockService));

        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(new Employee(1, "Juan", "Perez", LocalDate.of(1990, 5, 15), new User(), List.of())));

        when(resourceRepository.findById(1)).thenReturn(Optional.empty());

        ServiceNotSaveException exception = assertThrows(ServiceNotSaveException.class, () -> {
            serviceService.updateService(mockService.getId(), serviceRequest);
        });

        String expectedMessage = "El recurso no se encontro: 1";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        verify(resourceRepository, times(1)).findById(1);
        verify(serviceRepository, times(0)).save(any(Service.class));
    }

    @Test
    void testUpdateServiceEmployeeNotFound() {
        ServiceRequest serviceRequest = new ServiceRequest(
                "Updated Service",
                "Updated description",
                new BigDecimal(2.0),
                "Updated pageInformation",
                2,
                false,
                List.of(new CreateEmployeeResponse(1, "Juan", "Perez", LocalDate.of(1990, 5, 15), "juan.perez@example.com", "juanperez")),
                List.of()
        );

        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.of(mockService));

        when(resourceRepository.findById(anyInt())).thenReturn(Optional.of(new Resource(1, "Resource 1","img1", List.of(), List.of())));

        when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        ServiceNotSaveException exception = assertThrows(ServiceNotSaveException.class, () -> {
            serviceService.updateService(mockService.getId(), serviceRequest);
        });

        String expectedMessage = "El empleado no se encontro: 1";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        verify(employeeRepository, times(1)).findById(1);
        verify(serviceRepository, times(0)).save(any(Service.class));
    }

    @Test
    void testSaveServiceSuccess() {
        ServiceRequest serviceRequest = new ServiceRequest(
                "New Service",
                "Service description",
                new BigDecimal(100.0),
                "Service Page Info",
                60,
                true,
                List.of(new CreateEmployeeResponse(1, "Juan", "Perez", LocalDate.of(1990, 5, 15), "juan.perez@example.com", "juanperez")),
                List.of(new ResourceResponse(1, "Resource 1","img1", List.of()))
        );

        when(serviceRepository.findByName(serviceRequest.name())).thenReturn(Optional.empty());

        Service newService = new Service(1, "New Service", "Service description", new BigDecimal(100.0), "Service Page Info", 60, true, LocalDateTime.now(), List.of(), List.of());
        when(serviceRepository.save(any(Service.class))).thenReturn(newService);

        when(employeeHasServiceRepository.save(any(EmployeeHasService.class))).thenReturn(new EmployeeHasService());
        when(resourceHasServiceRepository.save(any(ResourceHasService.class))).thenReturn(new ResourceHasService());

        Service result = serviceService.saveService(serviceRequest);

        assertNotNull(result);
        assertEquals("New Service", result.getName());
        assertEquals("Service description", result.getDescription());
        assertEquals(new BigDecimal(100.0), result.getPrice());

        verify(serviceRepository, times(1)).save(any(Service.class));
        verify(employeeHasServiceRepository, times(1)).save(any(EmployeeHasService.class));
        verify(resourceHasServiceRepository, times(1)).save(any(ResourceHasService.class));
    }

    @Test
    void testSaveServiceServiceAlreadyExists() {
        ServiceRequest serviceRequest = new ServiceRequest(
                "Existing Service",
                "Service description",
                new BigDecimal(100.0),
                "Service Page Info",
                60,
                true,
                List.of(),
                List.of()
        );

        when(serviceRepository.findByName(serviceRequest.name())).thenReturn(Optional.of(mockService));

        ServiceNotSaveException exception = assertThrows(ServiceNotSaveException.class, () -> {
            serviceService.saveService(serviceRequest);
        });

        String expectedMessage = "El servicio ya existe!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        verify(serviceRepository, times(0)).save(any(Service.class));
        verify(employeeHasServiceRepository, times(0)).save(any(EmployeeHasService.class));
        verify(resourceHasServiceRepository, times(0)).save(any(ResourceHasService.class));
    }

    @Test
    void testSaveServiceWithEmployeesAndResources() {
        ServiceRequest serviceRequest = new ServiceRequest(
                "New Service",
                "Service description",
                new BigDecimal(100.0),
                "Service Page Info",
                60,
                true,
                List.of(new CreateEmployeeResponse(1, "Juan", "Perez", LocalDate.of(1990, 5, 15), "juan.perez@example.com", "juanperez")),
                List.of(new ResourceResponse(1, "Resource 1","img1", List.of()))
        );

        when(serviceRepository.findByName(serviceRequest.name())).thenReturn(Optional.empty());

        Service newService = new Service(1, "New Service", "Service description", new BigDecimal(100.0), "Service Page Info", 60, true, LocalDateTime.now(), List.of(), List.of());
        when(serviceRepository.save(any(Service.class))).thenReturn(newService);

        when(employeeHasServiceRepository.save(any(EmployeeHasService.class))).thenReturn(new EmployeeHasService());
        when(resourceHasServiceRepository.save(any(ResourceHasService.class))).thenReturn(new ResourceHasService());

        Service result = serviceService.saveService(serviceRequest);

        assertNotNull(result);
        assertEquals("New Service", result.getName());

        verify(serviceRepository, times(1)).save(any(Service.class));
        verify(employeeHasServiceRepository, times(1)).save(any(EmployeeHasService.class));
        verify(resourceHasServiceRepository, times(1)).save(any(ResourceHasService.class));
    }

    @Test
    void testGetServiceByIdWithEmployeesFiltered() {
        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.of(mockService));

        List<EmployeeHasService> mockEmployeeHasServices = List.of(
                new EmployeeHasService(1, new Employee(1, "Juan", "Perez", LocalDate.of(1990, 5, 15), new User(), List.of()), mockService),
                new EmployeeHasService(2, new Employee(2, "Ana", "Gomez", LocalDate.of(1985, 11, 20), new User(), List.of()), new Service(99, "Other Service", "Other Desc", new BigDecimal(100), "Other Info", 60, true, LocalDateTime.now(), List.of(), List.of()))
        );
        when(employeeHasServiceRepository.findAll()).thenReturn(mockEmployeeHasServices);

        List<ResourceHasService> mockResourceHasServices = List.of();
        when(resourceHasServiceRepository.findAll()).thenReturn(mockResourceHasServices);

        ServiceResponse serviceResponse = serviceService.getServiceById(mockService.getId());

        assertNotNull(serviceResponse);
        assertEquals(mockService.getId(), serviceResponse.id());
        assertEquals(1, serviceResponse.employees().size());

        CreateEmployeeResponse employee = serviceResponse.employees().get(0);
        assertEquals(1, employee.id());

        verify(serviceRepository, times(1)).findById(mockService.getId());
        verify(employeeHasServiceRepository, times(1)).findAll();
        verify(resourceHasServiceRepository, times(1)).findAll();
    }


    @Test
    void testGetServiceByIdWithResourcesFiltered() {
        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.of(mockService));

        when(employeeHasServiceRepository.findAll()).thenReturn(List.of());

        List<ResourceHasService> mockResourceHasServices = List.of(
                new ResourceHasService(1, new Resource(1, "Recurso 1","img1", List.of(), List.of()), mockService),
                new ResourceHasService(2, new Resource(2, "Recurso 2","img1", List.of(), List.of()), new Service(99, "Other Service", "Other Desc", new BigDecimal(100), "Other Info", 60, true, LocalDateTime.now(), List.of(), List.of()))
        );
        when(resourceHasServiceRepository.findAll()).thenReturn(mockResourceHasServices);

        List<ResourceHasAttribute> mockResourceHasAttributes = List.of();
        when(resourceHasAttributeRepository.findAll()).thenReturn(mockResourceHasAttributes);

        List<Attribute> mockAttributes = List.of();
        when(attributeRepository.findAll()).thenReturn(mockAttributes);

        ServiceResponse serviceResponse = serviceService.getServiceById(mockService.getId());

        assertNotNull(serviceResponse);
        assertEquals(1, serviceResponse.resources().size());

        ResourceResponse resource = serviceResponse.resources().get(0);
        assertEquals(1, resource.id());

        verify(serviceRepository, times(1)).findById(mockService.getId());
        verify(resourceHasServiceRepository, times(1)).findAll();
        verify(resourceHasAttributeRepository, times(1)).findAll();
        verify(attributeRepository, times(1)).findAll();
    }

    @Test
    void testUpdateServiceWithFilteredEmployees() {
        ServiceRequest serviceRequest = new ServiceRequest(
                "Updated Service", "Updated description", new BigDecimal(2.0), "Updated pageInformation", 2, true, List.of(), List.of()
        );


        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.of(mockService));
        when(serviceRepository.save(any(Service.class))).thenReturn(mockService);


        List<EmployeeHasService> mockEmployeeHasServices = List.of(
                new EmployeeHasService(1, new Employee(1, "Juan", "Perez", LocalDate.of(1990, 5, 15), new User(), List.of()), mockService),
                new EmployeeHasService(2, new Employee(2, "Pedro", "Lopez", LocalDate.of(1990, 5, 15), new User(), List.of()), new Service(99, "Other Service", "Other Desc", new BigDecimal(100), "Other Info", 60, true, LocalDateTime.now(), List.of(), List.of()))
        );
        when(employeeHasServiceRepository.findAll()).thenReturn(mockEmployeeHasServices);

        when(resourceHasServiceRepository.findAll()).thenReturn(List.of());

        ServiceResponse result = serviceService.updateService(mockService.getId(), serviceRequest);

        assertEquals(1, result.employees().size());
        assertEquals(1, result.employees().get(0).id());
        verify(serviceRepository, times(1)).save(any(Service.class));
    }

    @Test
    void testUpdateServiceWithFilteredResources() {
        ServiceRequest serviceRequest = new ServiceRequest(
                "Updated Service", "Updated description", new BigDecimal(2.0), "Updated pageInformation", 2, true, List.of(), List.of()
        );

        when(serviceRepository.findById(mockService.getId())).thenReturn(Optional.of(mockService));
        when(serviceRepository.save(any(Service.class))).thenReturn(mockService);

        List<ResourceHasService> mockResourceHasServices = List.of(
                new ResourceHasService(1, new Resource(1, "Recurso 1","img1", List.of(), List.of()), mockService),
                new ResourceHasService(2, new Resource(2, "Recurso 2","img1", List.of(), List.of()), new Service(99, "Other Service", "Other Desc", new BigDecimal(100), "Other Info", 60, true, LocalDateTime.now(), List.of(), List.of()))
        );
        when(resourceHasServiceRepository.findAll()).thenReturn(mockResourceHasServices);

        when(employeeHasServiceRepository.findAll()).thenReturn(List.of());

        when(resourceHasAttributeRepository.findAll()).thenReturn(List.of());
        when(attributeRepository.findAll()).thenReturn(List.of());

        ServiceResponse result = serviceService.updateService(mockService.getId(), serviceRequest);

        assertEquals(1, result.resources().size());
        assertEquals(1, result.resources().get(0).id());
        verify(serviceRepository, times(1)).save(any(Service.class));
    }


}
