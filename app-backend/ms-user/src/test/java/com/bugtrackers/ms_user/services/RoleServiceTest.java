package com.bugtrackers.ms_user.services;

import com.bugtrackers.ms_user.dto.request.PageRequest;
import com.bugtrackers.ms_user.dto.request.RoleRequest;
import com.bugtrackers.ms_user.dto.response.PageResponse;
import com.bugtrackers.ms_user.dto.response.RoleResponse;
import com.bugtrackers.ms_user.exceptions.UserNotFoundException;
import com.bugtrackers.ms_user.models.Page;
import com.bugtrackers.ms_user.models.Role;
import com.bugtrackers.ms_user.repositories.PageRepository;
import com.bugtrackers.ms_user.repositories.RolePageRepository;
import com.bugtrackers.ms_user.repositories.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PageRepository pageRepository;

    @Mock
    private RolePageRepository rolePageRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role1;
    private Role role2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        role1 = new Role();
        role1.setId(1);
        role1.setName("ROLE_ADMIN");

        role2 = new Role();
        role2.setId(2);
        role2.setName("ROLE_USER");
    }

    @Test
    void testGetRoles() {
        List<Role> roles = Arrays.asList(role1, role2);
        when(roleRepository.findAll()).thenReturn(roles);

        List<RoleResponse> result = roleService.getRoles();

        assertEquals(2, result.size());
        assertEquals("ROLE_ADMIN", result.get(0).name());
        assertEquals("ROLE_USER", result.get(1).name());
    }

    @Test
    void testSaveRole_Success() {
        when(roleRepository.findByName(role1.getName())).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(role1);

        RoleRequest request = new RoleRequest(role1.getName(), role1.getDescription());

        RoleResponse result = roleService.saveRole(request);

        assertNotNull(result);
        assertEquals("ROLE_ADMIN", result.name());
        verify(roleRepository, times(1)).findByName(role1.getName());
        // verify(roleRepository, times(1)).save(role1);
    }

    @Test
    void testSaveRole_RoleAlreadyExists() {
        RoleRequest request = new RoleRequest(role1.getName(), role1.getDescription());

        when(roleRepository.findByName(role1.getName())).thenReturn(Optional.of(role1));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> roleService.saveRole(request));
        assertEquals("El rol ya existe!", exception.getMessage());
        verify(roleRepository, times(1)).findByName(role1.getName());
        verify(roleRepository, times(0)).save(any(Role.class));
    }

    @Test
    void shouldGetAllPages() {
        com.bugtrackers.ms_user.models.Module mock = new com.bugtrackers.ms_user.models.Module(2, "module",
                "/direction", true, LocalDateTime.now(), List.of(
                        new Page(1, "page", "/path", null, true, LocalDateTime.now(), List.of())));
        Role role = new Role(1, "role", "description", LocalDateTime.now(), null, List.of());
        Page page = new Page(1, "name", "path", mock, true, LocalDateTime.now(), List.of(role));

        when(this.pageRepository.findAllByIsAvailable(true)).thenReturn(List.of(page));

        List<PageResponse> expected = List.of(new PageResponse(page));

        List<PageResponse> result = this.roleService.getAllPages();
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void shouldGetPagesByRoleId() {
        com.bugtrackers.ms_user.models.Module mock = new com.bugtrackers.ms_user.models.Module(2, "module",
                "/direction", true, LocalDateTime.now(), List.of(
                        new Page(1, "page", "/path", null, true, LocalDateTime.now(), List.of())));
        Page page = new Page(1, "name", "path", mock, true, LocalDateTime.now(), List.of());
        Role role = new Role(1, "role", "description", LocalDateTime.now(), null, List.of(page));

        when(this.roleRepository.findById(1)).thenReturn(Optional.of(role));

        List<PageResponse> expected = List.of(new PageResponse(page));
        List<PageResponse> actual = this.roleService.getPagesByRoleId(1);

        assertNotNull(actual);
        assertEquals(expected, actual);

    }

    @Test
    void shouldNotGetPagesByRoleNotFound() {

        when(this.roleRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            this.roleService.getPagesByRoleId(1);
        });

        String expected = "No se encontró el role.";
        String actual = exception.getMessage();
        assertEquals(expected, actual);

    }

    @Test
    void shouldUpdatePages() {
        com.bugtrackers.ms_user.models.Module mock = new com.bugtrackers.ms_user.models.Module(2, "module",
                "/direction", true, LocalDateTime.now(), List.of(
                        new Page(1, "page", "/path", null, true, LocalDateTime.now(), List.of())));
        Page page = new Page(1, "name", "path", mock, true, LocalDateTime.now(), List.of());
        Role role = new Role(1, "role", "description", LocalDateTime.now(), null, List.of(page));

        List<PageRequest> request = List.of(
            new PageRequest(1, "name", "path", true),
            new PageRequest(2, "name", "path", true),
            new PageRequest(3, "name", "path", true),
            new PageRequest(4, "name", "path", true)
        );

        when(this.roleRepository.findById(1)).thenReturn(Optional.of(role));

        String expectedMessage = "Privilegios actualizados.";
        String actualMessage = this.roleService.updatePages(1, request);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotUpdatePages() {

        List<PageRequest> request = List.of(
            new PageRequest(1, "name", "path", true),
            new PageRequest(2, "name", "path", true),
            new PageRequest(3, "name", "path", true),
            new PageRequest(4, "name", "path", true)
        );

        when(this.roleRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            this.roleService.updatePages(1, request);
        });

        String expectedMessage = "Role no encontrado.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}
