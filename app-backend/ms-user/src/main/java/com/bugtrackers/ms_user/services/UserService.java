package com.bugtrackers.ms_user.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bugtrackers.ms_user.dto.request.UserAllRequest;
import com.bugtrackers.ms_user.dto.response.ModuleResponse;
import com.bugtrackers.ms_user.dto.response.UserAllResponse;
import com.bugtrackers.ms_user.exceptions.UserNotFoundException;
import com.bugtrackers.ms_user.repositories.ModuleRepository;
import com.bugtrackers.ms_user.repositories.UserInformationRepository;
import com.bugtrackers.ms_user.repositories.UserRepository;

import lombok.AllArgsConstructor;
import com.bugtrackers.ms_user.models.Module;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.models.UserInformation;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;

    public List<ModuleResponse> getPages(Integer id) {
        List<Module> modules = this.moduleRepository.findModulesByUserId(id);
        System.out.println(modules);
        List<ModuleResponse> moduleResponses = modules.stream().map(ModuleResponse::new).toList();
        return moduleResponses;
    }

    public UserAllResponse getById(Integer id) {
        Optional<User> optional = this.userRepository.findById(id);

        if (optional.isEmpty()) {
            throw new UserNotFoundException("El usuario no se encontro!");
        }

        User user = optional.get();
        UserInformation userInformation = user.getUserInformation();

        return new UserAllResponse(
            user.getEmail(),
            user.getUsername(),
            userInformation.getNit(),
            userInformation.getImageProfile(),
            userInformation.getDescription()
        );
    }

    public UserAllResponse updateProfile(Integer id, UserAllRequest userAllRequest) {
        Optional<User> optional = this.userRepository.findById(id);

        if (optional.isEmpty()) {
            throw new UserNotFoundException("El usuario no se encontro!");
        }

        User user = optional.get();

        Optional<UserInformation> optionalInfo = this.userInformationRepository.findByUserId(id);

        if(optionalInfo.isEmpty()) {
            throw new UserNotFoundException("La informacion del usuario no se encontro!");
        }

        UserInformation userInformation = optionalInfo.get();


        userInformation.setNit(userAllRequest.getNit());
        userInformation.setImageProfile(userAllRequest.getImageProfile());
        userInformation.setDescription(userAllRequest.getDescription());
        
        this.userInformationRepository.save(userInformation);

        return new UserAllResponse(
            user.getEmail(),
            user.getUsername(),
            userInformation.getNit(),
            userInformation.getImageProfile(),
            userInformation.getDescription()
        );

    }

}
