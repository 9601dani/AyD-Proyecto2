package com.bugtrackers.ms_user.services;

import com.bugtrackers.ms_user.dto.request.ServiceRequest;
import com.bugtrackers.ms_user.dto.response.AttributeResponse;
import com.bugtrackers.ms_user.dto.response.CreateEmployeeResponse;
import com.bugtrackers.ms_user.dto.response.EmployeeResponse;
import com.bugtrackers.ms_user.dto.response.ResourceResponse;
import com.bugtrackers.ms_user.dto.response.ServiceResponse;
import com.bugtrackers.ms_user.exceptions.ServiceNotSaveException;
import com.bugtrackers.ms_user.models.*;
import com.bugtrackers.ms_user.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ServiceService {
    private final ServiceRepository serviceRepository;
    private final EmployeeHasServiceRepository employeeHasServiceRepository;
    @Autowired
    private final ResourceHasServiceRepository resourceHasServiceRepository;
    private final ResourceHasAttributeRepository resourceHasAttributeRepository;
    private final AttributeRepository attributeRepository;

    private final EmployeeRepository employeeRepository;
    private final ResourceRepository resourceRepository;

    public List<ServiceResponse> getAllServices() {
        List<com.bugtrackers.ms_user.models.Service> services = this.serviceRepository.findAll();

        if (services.isEmpty()) {
            throw new ServiceNotSaveException("No se encontraron servicios!");
        }

        return services.stream().map(ServiceResponse::new).toList();
    }

    public com.bugtrackers.ms_user.models.Service saveService(ServiceRequest service) {
        Optional<com.bugtrackers.ms_user.models.Service> optional = this.serviceRepository.findByName(service.name());

        if (optional.isPresent()) {
            throw new ServiceNotSaveException("El servicio ya existe!");
        }

        com.bugtrackers.ms_user.models.Service newService = new com.bugtrackers.ms_user.models.Service();
        newService.setName(service.name());
        newService.setDescription(service.description());
        newService.setPrice(service.price());
        newService.setPageInformation(service.pageInformation());
        newService.setTimeAprox(service.timeAprox());
        newService.setIsAvailable(service.isAvailable());

        com.bugtrackers.ms_user.models.Service serviceCreated = this.serviceRepository.save(newService);

        List<CreateEmployeeResponse> employees = service.employees();
        List<ResourceResponse> resources = service.resources();

        for (CreateEmployeeResponse employee : employees) {
            EmployeeHasService employeeHasService = new EmployeeHasService();

            Employee employeeToSave = new Employee();
            employeeToSave.setId(employee.id());
            employeeHasService.setEmployee(employeeToSave);

            employeeHasService.setService(serviceCreated);
            this.employeeHasServiceRepository.save(employeeHasService);
        }

        for (ResourceResponse resource : resources) {
            ResourceHasService resourceHasService = new ResourceHasService();

            Resource resourceToSave = new Resource();
            resourceToSave.setId(resource.id());
            resourceHasService.setResource(resourceToSave);

            resourceHasService.setService(serviceCreated);
            this.resourceHasServiceRepository.save(resourceHasService);
        }

        return serviceCreated;
    }

    public ServiceResponse getServiceById(Integer id) {
        Optional<com.bugtrackers.ms_user.models.Service> optional = this.serviceRepository.findById(id);

        if (optional.isEmpty()) {
            throw new ServiceNotSaveException("El servicio no se encontro!");
        }

        com.bugtrackers.ms_user.models.Service service = optional.get();

        List<EmployeeHasService> employeeHasServices = this.employeeHasServiceRepository.findAll();
        List<ResourceHasService> resourceHasServices = this.resourceHasServiceRepository.findAll();

        List<CreateEmployeeResponse> employees = new ArrayList<>();
        List<ResourceResponse> resources = new ArrayList<>();

        for (EmployeeHasService employeeHasService : employeeHasServices) {
            if (employeeHasService.getService().getId().equals(service.getId())) {
                CreateEmployeeResponse employee = new CreateEmployeeResponse(
                        employeeHasService.getEmployee().getId(),
                        employeeHasService.getEmployee().getFirstName(),
                        employeeHasService.getEmployee().getLastName(),
                        employeeHasService.getEmployee().getDateOfBirth(),
                        employeeHasService.getEmployee().getUser().getEmail(),
                        employeeHasService.getEmployee().getUser().getUsername());
                employees.add(employee);
            }
        }

        List<ResourceHasAttribute> resourceHasAttributes = this.resourceHasAttributeRepository.findAll();
        List<Attribute> allAttributes = this.attributeRepository.findAll();

        for (ResourceHasService resourceHasService : resourceHasServices) {
            if (resourceHasService.getService().getId().equals(service.getId())) {
                Resource resource = resourceHasService.getResource();

                List<Integer> attributeIds = resourceHasAttributes.stream()
                        .filter(rha -> rha.getResource().getId().equals(resource.getId()))
                        .map(rha -> rha.getAttribute().getId())
                        .collect(Collectors.toList());

                List<Attribute> resourceAttributes = allAttributes.stream()
                        .filter(attribute -> attributeIds.contains(attribute.getId()))
                        .collect(Collectors.toList());

                ResourceResponse resourceResponse = new ResourceResponse(
                        resource.getId(),
                        resource.getName(),
                        resource.getImage(),
                        resourceAttributes.stream().map(AttributeResponse::new).toList());
                resources.add(resourceResponse);
            }
        }

        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getPageInformation(),
                service.getTimeAprox(),
                service.getIsAvailable(),
                employees,
                resources);
    }

    public ServiceResponse updateService(Integer id, ServiceRequest serviceRequest) {
        Optional<com.bugtrackers.ms_user.models.Service> optional = this.serviceRepository.findById(id);

        if (optional.isEmpty()) {
            throw new ServiceNotSaveException("El servicio no se encontro!");
        }

        com.bugtrackers.ms_user.models.Service serviceUpdate = optional.get();

        serviceUpdate.setName(serviceRequest.name());
        serviceUpdate.setDescription(serviceRequest.description());
        serviceUpdate.setPrice(serviceRequest.price());
        serviceUpdate.setPageInformation(serviceRequest.pageInformation());
        serviceUpdate.setTimeAprox(serviceRequest.timeAprox());
        serviceUpdate.setIsAvailable(serviceRequest.isAvailable());

        this.resourceHasServiceRepository.deleteByServiceId(serviceUpdate.getId());
        this.employeeHasServiceRepository.deleteByServiceId(serviceUpdate.getId());

        for (ResourceResponse resourceResponse : serviceRequest.resources()) {
            Optional<Resource> optionalResource = this.resourceRepository.findById(resourceResponse.id());
            if (optionalResource.isPresent()) {
                ResourceHasService resourceHasService = new ResourceHasService();
                resourceHasService.setResource(optionalResource.get());
                resourceHasService.setService(serviceUpdate);
                this.resourceHasServiceRepository.save(resourceHasService);
            } else {
                throw new ServiceNotSaveException("El recurso no se encontro: " + resourceResponse.id());
            }
        }

        for (CreateEmployeeResponse employeeResponse : serviceRequest.employees()) {
            Optional<Employee> optionalEmployee = this.employeeRepository.findById(employeeResponse.id());
            if (optionalEmployee.isPresent()) {
                EmployeeHasService employeeHasService = new EmployeeHasService();
                employeeHasService.setEmployee(optionalEmployee.get());
                employeeHasService.setService(serviceUpdate);
                this.employeeHasServiceRepository.save(employeeHasService);
            } else {
                throw new ServiceNotSaveException("El empleado no se encontro: " + employeeResponse.id());
            }
        }

        this.serviceRepository.save(serviceUpdate);

        List<EmployeeHasService> employeeHasServices = this.employeeHasServiceRepository.findAll();
        List<ResourceHasService> resourceHasServices = this.resourceHasServiceRepository.findAll();

        List<CreateEmployeeResponse> employees = new ArrayList<>();
        List<ResourceResponse> resources = new ArrayList<>();

        for (EmployeeHasService employeeHasService : employeeHasServices) {
            if (employeeHasService.getService().getId().equals(serviceUpdate.getId())) {
                CreateEmployeeResponse employee = new CreateEmployeeResponse(
                        employeeHasService.getEmployee().getId(),
                        employeeHasService.getEmployee().getFirstName(),
                        employeeHasService.getEmployee().getLastName(),
                        employeeHasService.getEmployee().getDateOfBirth(),
                        employeeHasService.getEmployee().getUser().getEmail(),
                        employeeHasService.getEmployee().getUser().getUsername());
                employees.add(employee);
            }
        }

        List<ResourceHasAttribute> resourceHasAttributes = this.resourceHasAttributeRepository.findAll();
        List<Attribute> allAttributes = this.attributeRepository.findAll();

        for (ResourceHasService resourceHasService : resourceHasServices) {
            if (resourceHasService.getService().getId().equals(serviceUpdate.getId())) {
                Resource resource = resourceHasService.getResource();

                List<Integer> attributeIds = resourceHasAttributes.stream()
                        .filter(rha -> rha.getResource().getId().equals(resource.getId()))
                        .map(rha -> rha.getAttribute().getId())
                        .collect(Collectors.toList());

                List<Attribute> resourceAttributes = allAttributes.stream()
                        .filter(attribute -> attributeIds.contains(attribute.getId()))
                        .collect(Collectors.toList());

                ResourceResponse resourceResponse = new ResourceResponse(
                        resource.getId(),
                        resource.getName(),
                        resource.getImage(),
                        resourceAttributes.stream().map(AttributeResponse::new).toList());
                resources.add(resourceResponse);
            }
        }

        return new ServiceResponse(
                serviceUpdate.getId(),
                serviceUpdate.getName(),
                serviceUpdate.getDescription(),
                serviceUpdate.getPrice(),
                serviceUpdate.getPageInformation(),
                serviceUpdate.getTimeAprox(),
                serviceUpdate.getIsAvailable(),
                employees,
                resources);
    }

    public List<ResourceResponse> getResourcesByIds(List<Integer> ids) {
        List<com.bugtrackers.ms_user.models.Service> services = this.serviceRepository.findAllByIdIn(ids);
    
        List<ResourceResponse> resourceResponse = new ArrayList<>();
    
        for (com.bugtrackers.ms_user.models.Service service : services) {
            List<Resource> resources = service.getResources();
    
            List<ResourceResponse> currentResources = resources.stream()
                    .map(resource -> new ResourceResponse(resource.getId(), resource.getName(), resource.getImage(),
                            resource.getAttributes().stream().map(AttributeResponse::new).toList()))
                    .toList();
    
            if (resourceResponse.isEmpty()) {
                resourceResponse = currentResources;
            } else {
                resourceResponse = resourceResponse.stream()
                        .filter(currentResources::contains)
                        .toList();
            }
        }
        return resourceResponse;
    }
    

    public List<EmployeeResponse> getEmployeesByIds(List<Integer> ids) {
        List<com.bugtrackers.ms_user.models.Service> services = this.serviceRepository.findAllByIdIn(ids);
    
        List<EmployeeResponse> employeeResponses = new ArrayList<>();
    
        for (com.bugtrackers.ms_user.models.Service service : services) {
            List<Employee> employees = service.getEmployees();
    
            List<EmployeeResponse> currentEmployees = employees.stream()
                    .map(EmployeeResponse::new)
                    .toList();
    
            if (employeeResponses.isEmpty()) {
                employeeResponses = currentEmployees;
            } else {
                employeeResponses = employeeResponses.stream()
                        .filter(currentEmployees::contains)
                        .toList(); 
            }
        }
    
        return employeeResponses;
    }
    
}
