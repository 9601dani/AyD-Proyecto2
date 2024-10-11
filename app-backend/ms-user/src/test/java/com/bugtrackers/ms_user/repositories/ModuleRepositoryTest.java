package com.bugtrackers.ms_user.repositories;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.bugtrackers.ms_user.models.Module;
import com.bugtrackers.ms_user.models.Page;

public class ModuleRepositoryTest {

    @Mock
    private ModuleRepository moduleRepository;
    private List<Module> mockModules;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockModules = List.of(
            new Module(1, "module 1", "/direction", true, LocalDateTime.now(), List.of(
                new Page(1, "page", "/path", null, true, LocalDateTime.now())
            )));
    }

    @Test
    void testFindModulesByUserId() {
        when(moduleRepository.findModulesByUserId(1)).thenReturn(mockModules);

        List<Module> modules = moduleRepository.findModulesByUserId(1);

        assertNotNull(modules);
    }
}
