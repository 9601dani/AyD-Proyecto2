package com.bugtrackers.ms_user.services;


import com.bugtrackers.ms_user.exceptions.ServiceNotSaveException;
import com.bugtrackers.ms_user.repositories.ServiceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ServiceService {
    private final ServiceRepository serviceRepository;

    public List<com.bugtrackers.ms_user.models.Service> getAllServices() {
        List<com.bugtrackers.ms_user.models.Service> services = this.serviceRepository.findAll();

        if (services.isEmpty()) {
            throw new ServiceNotSaveException("No se encontraron servicios!");
        }

        return services;
    }

    public com.bugtrackers.ms_user.models.Service saveService(com.bugtrackers.ms_user.models.Service service) {
        Optional<com.bugtrackers.ms_user.models.Service> optional = this.serviceRepository.findById(service.getId());

        if(optional.isPresent()) {
            throw new ServiceNotSaveException("El servicio ya existe!");
        }

        return this.serviceRepository.save(service);
    }

    public com.bugtrackers.ms_user.models.Service getServiceById(Integer id) {
        Optional<com.bugtrackers.ms_user.models.Service> optional = this.serviceRepository.findById(id);

        if(optional.isEmpty()) {
            throw new ServiceNotSaveException("El servicio no se encontro!");
        }

        com.bugtrackers.ms_user.models.Service service = optional.get();
        return service;
    }

    public com.bugtrackers.ms_user.models.Service updateService(Integer id, com.bugtrackers.ms_user.models.Service service) {
        Optional<com.bugtrackers.ms_user.models.Service> optional = this.serviceRepository.findById(id);

        if(optional.isEmpty()) {
            throw new ServiceNotSaveException("El servicio no se encontro!");
        }

        com.bugtrackers.ms_user.models.Service serviceUpdate = optional.get();
        serviceUpdate.setName(service.getName());
        serviceUpdate.setDescription(service.getDescription());
        serviceUpdate.setPrice(service.getPrice());
        serviceUpdate.setPageInformation(service.getPageInformation());
        serviceUpdate.setTimeAprox(service.getTimeAprox());
        serviceUpdate.setIsAvailable(service.getIsAvailable());

        return this.serviceRepository.save(serviceUpdate);
    }


}
