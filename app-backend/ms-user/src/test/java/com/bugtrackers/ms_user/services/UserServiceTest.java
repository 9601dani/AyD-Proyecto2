package com.bugtrackers.ms_user.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bugtrackers.ms_user.dto.request.UserAllRequest;
import com.bugtrackers.ms_user.dto.response.ModuleResponse;
import com.bugtrackers.ms_user.dto.response.UserAllResponse;
import com.bugtrackers.ms_user.exceptions.UserNotFoundException;
import com.bugtrackers.ms_user.models.Module;
import com.bugtrackers.ms_user.models.Page;
import com.bugtrackers.ms_user.repositories.ModuleRepository;
import com.bugtrackers.ms_user.repositories.UserInformationRepository;
import com.bugtrackers.ms_user.repositories.UserRepository;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.models.UserInformation;

public class UserServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserInformationRepository userInformationRepository;

    private User user;
    private UserInformation userInformation;

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
        user = new User("email", "username", "password");
        userInformation = new UserInformation("nit", "imageProfile", "description", user);
    }

    @Test
    void testGetPages() {
        when(moduleRepository.findModulesByUserId(1)).thenReturn(mockModules);

        List<ModuleResponse> modules = userService.getPages(1);

        assertNotNull(modules);
    }

    @Test
    void testGetById() {
        user.setUserInformation(userInformation);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        
        UserAllResponse userAllResponse = new UserAllResponse(user.getEmail(), user.getUsername(), userInformation.getNit(), userInformation.getImageProfile(), userInformation.getDescription());

        UserAllResponse userAllResponseTest = userService.getById(1);

        assertEquals(userAllResponse, userAllResponseTest);
        
    }

    @Test
    void getByIdException(){
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class,()->{
            userService.getById(1);
        });

        String expectedMessage = "El usuario no se encontro!";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void updateProfileTest(){
        user.setUserInformation(userInformation);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userInformationRepository.findByUserId(1)).thenReturn(Optional.of(userInformation));

        UserAllRequest userAllRequest = new UserAllRequest("nitUpdate", "imageProfileUpdate", "descriptionUpdate");
        
        UserAllResponse userAllResponseExpected = new UserAllResponse(user.getEmail(), user.getUsername(), userAllRequest.getNit(), userAllRequest.getImageProfile(), userAllRequest.getDescription());
        

        UserAllResponse userAllResponseActual = userService.updateProfile(1, userAllRequest);

        assertEquals(userAllResponseExpected, userAllResponseActual);
    }

    @Test
    void updateProfileException1(){
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        UserAllRequest userAllRequest = new UserAllRequest("nitUpdate", "imageProfileUpdate", "descriptionUpdate");


        Exception exception = assertThrows(UserNotFoundException.class, () -> {
        userService.updateProfile(1, userAllRequest);
    });

        String expectedMessage = "El usuario no se encontro!";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateProfileException(){
        user.setUserInformation(userInformation);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userInformationRepository.findByUserId(1)).thenReturn(Optional.empty());

        UserAllRequest userAllRequest = new UserAllRequest("nitUpdate", "imageProfileUpdate", "descriptionUpdate");

        Exception exception = assertThrows((UserNotFoundException.class),()->{
            userService.updateProfile(1, userAllRequest);
        });

        String expectedMessage = "La informacion del usuario no se encontro!";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }



}
