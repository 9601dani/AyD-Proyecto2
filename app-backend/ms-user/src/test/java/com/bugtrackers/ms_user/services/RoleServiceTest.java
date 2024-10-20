package com.bugtrackers.ms_user.services;

import com.bugtrackers.ms_user.models.Role;
import com.bugtrackers.ms_user.repositories.RoleRepository;
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

public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

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

        List<Role> result = roleService.getRoles();

        assertEquals(2, result.size());
        assertEquals("ROLE_ADMIN", result.get(0).getName());
        assertEquals("ROLE_USER", result.get(1).getName());
    }

    @Test
    void testSaveRole_Success() {
        when(roleRepository.findById(1)).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(role1);

        Role result = roleService.saveRole(role1);

        assertNotNull(result);
        assertEquals("ROLE_ADMIN", result.getName());
        verify(roleRepository, times(1)).findById(1);
        verify(roleRepository, times(1)).save(role1);
    }

    @Test
    void testSaveRole_RoleAlreadyExists() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role1));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> roleService.saveRole(role1));
        assertEquals("El rol ya existe!", exception.getMessage());
        verify(roleRepository, times(1)).findById(1);
        verify(roleRepository, times(0)).save(any(Role.class));
    }
}
