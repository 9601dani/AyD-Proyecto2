package com.bugtrackers.ms_user.services;

import java.util.List;

import com.bugtrackers.ms_user.dto.response.AppointmentResponse;
import com.bugtrackers.ms_user.models.Appointment;
import com.bugtrackers.ms_user.repositories.AppointmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.bugtrackers.ms_user.dto.request.UserAllRequest;
import com.bugtrackers.ms_user.dto.response.ModuleResponse;
import com.bugtrackers.ms_user.dto.response.UserAllResponse;
import com.bugtrackers.ms_user.exceptions.UserNotFoundException;
import com.bugtrackers.ms_user.repositories.ModuleRepository;
import com.bugtrackers.ms_user.repositories.RolePageRepository;
import com.bugtrackers.ms_user.repositories.UserInformationRepository;
import com.bugtrackers.ms_user.repositories.UserRepository;

import lombok.AllArgsConstructor;
import com.bugtrackers.ms_user.models.Module;
import com.bugtrackers.ms_user.models.Page;
import com.bugtrackers.ms_user.models.RolePage;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.models.UserInformation;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class UserService {
    
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;
    private final AppointmentRepository appointmentRepository;
    private final RolePageRepository rolePageRepository;

    public List<ModuleResponse> getPages(Integer id) {
        List<Module> modules = this.moduleRepository.findModulesByUserId(id);
        System.out.println(modules);
        List<RolePage> rolePages = this.rolePageRepository.findRolePageByUserId(id);
        
        for(Module module: modules) {
            List<Page> pages = rolePages.stream().map(RolePage::getPage)
                .filter(p -> p.getModule().getId() == module.getId())
                .toList();

            module.setPages(pages);
        }

        List<ModuleResponse> moduleResponses = modules.stream()
        .map(ModuleResponse::new).toList();
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
            userInformation.getDescription(),
            userInformation.getDpi(),
            userInformation.getPhoneNumber(),
            user.getIs2FA()
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
        userInformation.setDpi(userAllRequest.getDpi());
        userInformation.setPhoneNumber(userAllRequest.getPhoneNumber());
        
        this.userInformationRepository.save(userInformation);

        return new UserAllResponse(
            user.getEmail(),
            user.getUsername(),
            userInformation.getNit(),
            userInformation.getImageProfile(),
            userInformation.getDescription(),
            userInformation.getDpi(),
            userInformation.getPhoneNumber(),
            user.getIs2FA()
        );

    }

    public String getInfo(Integer id) {
        Optional<UserInformation> optional = this.userInformationRepository.findByUserId(id);

        if (optional.isEmpty()) {
            throw new UserNotFoundException("La informacion del usuario no se encontro!");
        }

        return optional.get().getImageProfile();
    }

    public Integer updateImageProfile(Integer id, String pathImg) {
        Optional<UserInformation> userInfo = userInformationRepository.findByUserId(id);
        if (userInfo.isEmpty()) {
            throw new UserNotFoundException("La información del usuario no se encontró para el ID: " + id);
        }

        try {
            userInformationRepository.updateImageProfile(id, pathImg);
        } catch (Exception e) {
            throw new RuntimeException("Error updating image for user ID: " + id, e);
        }

        return id;
    }

    public String set2fa(Integer id) {
        Optional<User> uOptional = this.userRepository.findById(id);

        if(uOptional.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        User user = uOptional.get();
        user.setIs2FA(!user.getIs2FA());
        this.userRepository.save(user);
        return "2FA actualizada exitosamente!";
    }


    public List<AppointmentResponse> getMyAppointments(Integer id) {
        List<Appointment> appointments = this.appointmentRepository.findByUserId(id);
        return appointments.stream().map(AppointmentResponse::new).toList();
    }

}
