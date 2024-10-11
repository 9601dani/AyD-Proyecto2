package com.bugtrackers.ms_user.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bugtrackers.ms_user.dto.response.ModuleResponse;
import com.bugtrackers.ms_user.repositories.ModuleRepository;

import lombok.AllArgsConstructor;
import com.bugtrackers.ms_user.models.Module;

@Service
@AllArgsConstructor
public class UserService {
    
    private final ModuleRepository moduleRepository;

    public List<ModuleResponse> getPages(Integer id) {
        List<Module> modules = this.moduleRepository.findModulesByUserId(id);
        System.out.println(modules);
        List<ModuleResponse> moduleResponses = modules.stream().map(ModuleResponse::new).toList();
        return moduleResponses;
    }
}
