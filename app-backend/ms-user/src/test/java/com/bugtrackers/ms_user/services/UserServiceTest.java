package com.bugtrackers.ms_user.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bugtrackers.ms_user.dto.response.ModuleResponse;
import com.bugtrackers.ms_user.models.Module;
import com.bugtrackers.ms_user.models.Page;
import com.bugtrackers.ms_user.repositories.ModuleRepository;

public class UserServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private UserService userService;
    private List<Module> mockModules;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockModules = List.of(
            new Module(1, "module", "/direction", true, LocalDateTime.now(), List.of(
                new Page(1, "page", "/path", null, true, LocalDateTime.now())
            ))
        );
    }

    @Test
    void testGetPages() {
        when(moduleRepository.findModulesByUserId(1)).thenReturn(mockModules);

        List<ModuleResponse> modules = userService.getPages(1);

        assertNotNull(modules);
    }
}
