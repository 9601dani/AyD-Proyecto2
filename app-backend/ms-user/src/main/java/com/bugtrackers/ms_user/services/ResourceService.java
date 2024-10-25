package com.bugtrackers.ms_user.services;

import com.bugtrackers.ms_user.dto.request.ResourceRequest;
import com.bugtrackers.ms_user.dto.response.AttributeResponse;
import com.bugtrackers.ms_user.dto.response.ResourceResponse;
import com.bugtrackers.ms_user.exceptions.AttributeNoSaveException;
import com.bugtrackers.ms_user.exceptions.ResourceNotFoundException;
import com.bugtrackers.ms_user.models.Attribute;
import com.bugtrackers.ms_user.models.Resource;
import com.bugtrackers.ms_user.models.ResourceHasAttribute;
import com.bugtrackers.ms_user.repositories.AttributeRepository;
import com.bugtrackers.ms_user.repositories.ResourceHasAttributeRepository;
import com.bugtrackers.ms_user.repositories.ResourceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ResourceService {
    private final AttributeRepository attributeRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceHasAttributeRepository resourceHasAttributeRepository;

    public List<AttributeResponse> getAttributes() {
        return this.attributeRepository.findAll().stream()
                .map(AttributeResponse::new).toList();
    }

    public AttributeResponse createAttribute(Attribute attribute) {
        Attribute newAttribute = this.attributeRepository.save(attribute);

        if(newAttribute == null) {
            throw new AttributeNoSaveException("No se pudo crear el atributo, intente nuevamente");
        }
        Attribute saved = this.attributeRepository.save(attribute);
        AttributeResponse response = new AttributeResponse(saved);
        return response;
    }

    public ResourceResponse createResource(ResourceRequest resourceRequest) {
        Resource resource = new Resource();
        resource.setName(resourceRequest.name());
        resource.setImage(resourceRequest.image());

        Resource newResource = this.resourceRepository.save(resource);

        List<Attribute> attributes = resourceRequest.attributes();

        for (Attribute attribute : attributes) {
            ResourceHasAttribute newAttribute = new ResourceHasAttribute();
            newAttribute.setResource(newResource);
            newAttribute.setAttribute(attribute);
            this.resourceHasAttributeRepository.save(newAttribute);
        }

        return new ResourceResponse(newResource.getId(), newResource.getName(), newResource.getImage(), attributes.stream().map(AttributeResponse::new).toList());
    }

    public List<ResourceResponse> getResources() {
        List<Resource> resources = this.resourceRepository.findAll();
        List<Attribute> attributes = this.attributeRepository.findAll();
        List<ResourceHasAttribute> resourceHasAttributes = this.resourceHasAttributeRepository.findAll();

        List<ResourceResponse> resourceResponses = new ArrayList<>();

        for (Resource resource : resources) {
            List<Integer> attributeIds = resourceHasAttributes.stream()
                    .filter(rha -> rha.getResource().getId().equals(resource.getId()))
                    .map(rha -> rha.getAttribute().getId())
                    .collect(Collectors.toList());

            List<Attribute> resourceAttributes = attributes.stream()
                    .filter(attribute -> attributeIds.contains(attribute.getId()))
                    .collect(Collectors.toList());

            ResourceResponse resourceResponse = new ResourceResponse(
                    resource.getId(),
                    resource.getName(),
                    resource.getImage(),
                    resourceAttributes.stream().map(AttributeResponse::new).toList()
            );

            resourceResponses.add(resourceResponse);
        }

        return resourceResponses;
    }

    public ResourceResponse getResourceById(Integer id){
        Optional<Resource> resourceOptional = this.resourceRepository.findById(id);

        if(resourceOptional.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró el recurso ");
        }
        List<Attribute> attributes = this.attributeRepository.findAll();
        List<ResourceHasAttribute> resourceHasAttributes = this.resourceHasAttributeRepository.findAll();

        List<Integer> attributeIds = resourceHasAttributes.stream()
                .filter(rha -> rha.getResource().getId().equals(resourceOptional.get().getId()))
                .map(rha -> rha.getAttribute().getId())
                .collect(Collectors.toList());

        List<Attribute> resourceAttributes = attributes.stream()
                .filter(attribute -> attributeIds.contains(attribute.getId()))
                .collect(Collectors.toList());

        return new ResourceResponse(
                resourceOptional.get().getId(),
                resourceOptional.get().getName(),
                resourceOptional.get().getImage(),
                resourceAttributes.stream().map(AttributeResponse::new).toList()
        );
    }

    public ResourceResponse updateResource(Integer id, ResourceRequest resourceRequest) {
        Optional<Resource> resourceOptional = this.resourceRepository.findById(id);

        if(resourceOptional.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró el recurso ");
        }

        Resource resource = resourceOptional.get();
        resource.setName(resourceRequest.name());
        resource.setImage(resourceRequest.image());

        Resource newResource = this.resourceRepository.save(resource);

        List<Attribute> attributes = resourceRequest.attributes();

        List<ResourceHasAttribute> resourceHasAttributes = this.resourceHasAttributeRepository.findAll();

        for (ResourceHasAttribute rha : resourceHasAttributes) {
            if(rha.getResource().getId().equals(newResource.getId())) {
                this.resourceHasAttributeRepository.delete(rha);
            }
        }

        for (Attribute attribute : attributes) {
            ResourceHasAttribute newAttribute = new ResourceHasAttribute();
            newAttribute.setResource(newResource);
            newAttribute.setAttribute(attribute);
            this.resourceHasAttributeRepository.save(newAttribute);
        }

        return new ResourceResponse(newResource.getId(), newResource.getName(),newResource.getImage(), attributes.stream().map(AttributeResponse::new).toList());
    }

}
