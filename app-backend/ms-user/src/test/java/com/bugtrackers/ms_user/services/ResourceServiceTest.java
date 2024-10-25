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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private AttributeRepository attributeRepository;

    @Mock
    private ResourceHasAttributeRepository resourceHasAttributeRepository;

    @InjectMocks
    private ResourceService resourceService;

    private ResourceRequest resourceRequest;
    private Resource newResource;

    private Resource resource1;
    private Resource resource2;
    private Attribute attribute;
    private Attribute attribute1;
    private Attribute attribute2;

    private ResourceHasAttribute resourceHasAttribute1;
    private ResourceHasAttribute resourceHasAttribute2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resource1 = new Resource();
        resource1.setId(1);
        resource1.setName("Resource1");
        resource1.setImage("img1");

        resource2 = new Resource();
        resource2.setId(2);
        resource2.setName("Resource2");
        resource2.setImage("img2");

        attribute = new Attribute(1, "Attrib" +
                "ute1", "Description1", List.of());
        attribute1 = new Attribute(1, "Attribute1", "Description1", List.of());
        attribute2 = new Attribute(2, "Attribute2", "Description2", List.of());

        List<Attribute> attributes = Arrays.asList(attribute1, attribute2);
        resourceRequest = new ResourceRequest("NewResource","img1", attributes);

        newResource = new Resource();
        newResource.setId(1);
        newResource.setName("NewResource");
        newResource.setImage("img1");

        resourceHasAttribute1 = new ResourceHasAttribute();
        resourceHasAttribute1.setResource(resource1);
        resourceHasAttribute1.setAttribute(attribute1);

        resourceHasAttribute2 = new ResourceHasAttribute();
        resourceHasAttribute2.setResource(resource2);
        resourceHasAttribute2.setAttribute(attribute2);
    }

    @Test
    void testGetAttributes() {

        List<Attribute> attributes = Arrays.asList(attribute1, attribute2);
        when(attributeRepository.findAll()).thenReturn(attributes);

        List<AttributeResponse> result = resourceService.getAttributes();

        assertEquals(2, result.size());
        assertEquals(attribute1.getId(), result.get(0).id());
        assertEquals(attribute1.getName(), result.get(0).name());
        assertEquals(attribute1.getDescription(), result.get(0).description());

        assertEquals(attribute2.getId(), result.get(1).id());
        assertEquals(attribute2.getName(), result.get(1).name());
        assertEquals(attribute2.getDescription(), result.get(1).description());

        verify(attributeRepository, times(1)).findAll();
    }

    @Test
    void testCreateAttributeSuccess() {
        when(attributeRepository.save(attribute)).thenReturn(attribute);

        AttributeResponse result = resourceService.createAttribute(attribute);

        assertEquals(new AttributeResponse(attribute), result);
        verify(attributeRepository, times(2)).save(attribute);
    }

    @Test
    void testCreateAttributeFailure() {
        when(attributeRepository.save(attribute)).thenReturn(null);

        assertThrows(AttributeNoSaveException.class, () -> {
            resourceService.createAttribute(attribute);
        });

        verify(attributeRepository, times(1)).save(attribute);
    }

    @Test
    void testCreateResourceSuccess() {
        when(resourceRepository.save(any(Resource.class))).thenReturn(newResource);

        ResourceResponse result = resourceService.createResource(resourceRequest);

        System.out.println("Resultado del recurso creado: " + result);

        assertNotNull(result, "El resultado no debe ser nulo");

        assertEquals(newResource.getId(), result.id(), "El ID del recurso creado no coincide");
        assertEquals(newResource.getName(), result.name(), "El nombre del recurso creado no coincide");
        assertEquals(newResource.getImage(), result.image(), "La imagen del recurso creado no coincide");

        List<AttributeResponse> expectedAttributes = resourceRequest.attributes().stream()
                .map(AttributeResponse::new)
                .toList();
        assertEquals(expectedAttributes, result.attributes(), "Los atributos no coinciden");

        verify(resourceRepository, times(1)).save(any(Resource.class));

        verify(resourceHasAttributeRepository, times(2)).save(any(ResourceHasAttribute.class));
    }



    @Test
    void testGetResourcesSuccess() {
        List<Resource> resources = Arrays.asList(resource1, resource2);
        List<Attribute> attributes = Arrays.asList(attribute1, attribute2);
        List<ResourceHasAttribute> resourceHasAttributes = Arrays.asList(resourceHasAttribute1, resourceHasAttribute2);

        when(resourceRepository.findAll()).thenReturn(resources);
        when(attributeRepository.findAll()).thenReturn(attributes);
        when(resourceHasAttributeRepository.findAll()).thenReturn(resourceHasAttributes);

        List<ResourceResponse> result = resourceService.getResources();

        assertEquals(2, result.size());

        ResourceResponse resourceResponse1 = result.get(0);
        assertEquals(resource1.getId(), resourceResponse1.id());
        assertEquals(resource1.getName(), resourceResponse1.name());
        assertEquals(1, resourceResponse1.attributes().size());
        assertEquals(attribute1.getId(), resourceResponse1.attributes().get(0).id());

        ResourceResponse resourceResponse2 = result.get(1);
        assertEquals(resource2.getId(), resourceResponse2.id());
        assertEquals(resource2.getName(), resourceResponse2.name());
        assertEquals(1, resourceResponse2.attributes().size());
        assertEquals(attribute2.getId(), resourceResponse2.attributes().get(0).id());

        verify(resourceRepository, times(1)).findAll();
        verify(attributeRepository, times(1)).findAll();
        verify(resourceHasAttributeRepository, times(1)).findAll();
    }

    @Test
    void testGetResourceByIdSuccess() {
        when(resourceRepository.findById(1)).thenReturn(Optional.of(resource1));
        when(attributeRepository.findAll()).thenReturn(Arrays.asList(attribute1, attribute2));
        when(resourceHasAttributeRepository.findAll()).thenReturn(Arrays.asList(resourceHasAttribute1, resourceHasAttribute2));

        ResourceResponse response = resourceService.getResourceById(1);

        assertNotNull(response);
        assertEquals(resource1.getId(), response.id());
        assertEquals(resource1.getName(), response.name());
        assertEquals(1, response.attributes().size());
        assertEquals(attribute1.getId(), response.attributes().get(0).id());

        verify(resourceRepository, times(1)).findById(1);
        verify(attributeRepository, times(1)).findAll();
        verify(resourceHasAttributeRepository, times(1)).findAll();
    }

    @Test
    void testGetResourceByIdNotFound() {
        when(resourceRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resourceService.getResourceById(3));

        verify(resourceRepository, times(1)).findById(3);
        verify(attributeRepository, never()).findAll();
        verify(resourceHasAttributeRepository, never()).findAll();
    }

    @Test
    void testUpdateResourceSuccess() {
        when(resourceRepository.findById(1)).thenReturn(Optional.of(resource1));
        when(resourceRepository.save(any(Resource.class))).thenReturn(newResource);
        when(resourceHasAttributeRepository.findAll()).thenReturn(Arrays.asList(resourceHasAttribute1, resourceHasAttribute2));

        ResourceResponse response = resourceService.updateResource(1, resourceRequest);

        assertNotNull(response);
        assertEquals(newResource.getId(), response.id());
        assertEquals(newResource.getName(), response.name());
        assertEquals(newResource.getImage(), response.image());
        assertEquals(2, response.attributes().size());
        assertEquals(attribute1.getId(), response.attributes().get(0).id());
        assertEquals(attribute2.getId(), response.attributes().get(1).id());

        verify(resourceRepository, times(1)).findById(1);
        verify(resourceRepository, times(1)).save(any(Resource.class));
        verify(resourceHasAttributeRepository, times(1)).findAll();
        verify(resourceHasAttributeRepository, times(1)).delete(resourceHasAttribute1);

        verify(resourceHasAttributeRepository, times(2)).save(any(ResourceHasAttribute.class));
    }


    @Test
    void testUpdateResourceNotFound() {
        when(resourceRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resourceService.updateResource(3, resourceRequest));

        verify(resourceRepository, times(1)).findById(3);
        verify(resourceRepository, never()).save(any(Resource.class));
        verify(resourceHasAttributeRepository, never()).findAll();
        verify(resourceHasAttributeRepository, never()).delete(any(ResourceHasAttribute.class));
    }
}
