package com.bugtrackers.ms_user.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

import com.bugtrackers.ms_user.dto.response.AppointmentResponse;
import com.bugtrackers.ms_user.models.*;
import com.bugtrackers.ms_user.repositories.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bugtrackers.ms_user.dto.request.UserAllRequest;
import com.bugtrackers.ms_user.dto.response.ModuleResponse;
import com.bugtrackers.ms_user.dto.response.UserAllResponse;
import com.bugtrackers.ms_user.exceptions.UserNotFoundException;
import com.bugtrackers.ms_user.repositories.ModuleRepository;
import com.bugtrackers.ms_user.repositories.RolePageRepository;
import com.bugtrackers.ms_user.repositories.UserInformationRepository;
import com.bugtrackers.ms_user.repositories.UserRepository;

public class UserServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserInformationRepository userInformationRepository;

    @Mock
    private RolePageRepository rolePageRepository;

    private User user;
    private UserInformation userInformation;

    @InjectMocks
    private UserService userService;

    private List<com.bugtrackers.ms_user.models.Module> mockModules;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockModules = List.of(
            new com.bugtrackers.ms_user.models.Module(1, "module", "/direction", true, LocalDateTime.now(), List.of(
                new Page(1, "page", "/path", null, true, LocalDateTime.now(), List.of())
            ))
        );
        user = new User("email", "username", "password");
        userInformation = new UserInformation("nit", "imageProfile", "description", user, "dpi", "tel");
    }

    @Test
    void testGetPages() {

        Role role = new Role(1, "role", "description", LocalDateTime.now(), null, List.of());
        Page page = new Page(1, "name", "path", mockModules.get(0), true, LocalDateTime.now(), List.of(role));

        List<RolePage> mockPages= List.of(
            new RolePage(1, role, page, true, true, true),
            new RolePage(2, role, page, true, true, true),
            new RolePage(3, role, page, true, true, true),
            new RolePage(4, role, page, true, true, true)
        );

        when(moduleRepository.findModulesByUserId(1)).thenReturn(mockModules);
        when(rolePageRepository.findRolePageByUserId(1)).thenReturn(mockPages);

        List<ModuleResponse> modules = userService.getPages(1);

        assertNotNull(modules);
    }

    @Test
    void testGetById() {
        user.setUserInformation(userInformation);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        
        UserAllResponse userAllResponse = new UserAllResponse(user.getEmail(), user.getUsername(), userInformation.getNit(), userInformation.getImageProfile(), userInformation.getDescription(), userInformation.getDpi(), userInformation.getPhoneNumber(), false);

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

        UserAllRequest userAllRequest = new UserAllRequest("nitUpdate", "imageProfileUpdate", "descriptionUpdate", "dpi", "tel");
        
        UserAllResponse userAllResponseExpected = new UserAllResponse(user.getEmail(), user.getUsername(), userAllRequest.getNit(), userAllRequest.getImageProfile(), userAllRequest.getDescription(), userAllRequest.getDpi(), userAllRequest.getPhoneNumber(), false);
        

        UserAllResponse userAllResponseActual = userService.updateProfile(1, userAllRequest);

        assertEquals(userAllResponseExpected, userAllResponseActual);
    }

    @Test
    void updateProfileException1(){
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        UserAllRequest userAllRequest = new UserAllRequest("nitUpdate", "imageProfileUpdate", "descriptionUpdate", "dpi", "tel");


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

        UserAllRequest userAllRequest = new UserAllRequest("nitUpdate", "imageProfileUpdate", "descriptionUpdate", "dpi", "tel");

        Exception exception = assertThrows((UserNotFoundException.class),()->{
            userService.updateProfile(1, userAllRequest);
        });

        String expectedMessage = "La informacion del usuario no se encontro!";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void getInfoTest(){
        when(userInformationRepository.findByUserId(1)).thenReturn(Optional.of(userInformation));

        String userInformationTest = userService.getInfo(1);

        assertEquals(userInformation.getImageProfile(), userInformationTest);
    }

    @Test
    void getInfoException(){
        when(userInformationRepository.findByUserId(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class,()->{
            userService.getInfo(1);
        });

        String expectedMessage = "La informacion del usuario no se encontro!";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateImageProfileTest() {
        when(userInformationRepository.findByUserId(1)).thenReturn(Optional.of(userInformation));

        String newImagePath = "imageProfileUpdate";

        doAnswer(invocation -> {
            userInformation.setImageProfile(newImagePath);
            return null;
        }).when(userInformationRepository).updateImageProfile(1, newImagePath);

        Integer userId = userService.updateImageProfile(1, newImagePath);

        verify(userInformationRepository).updateImageProfile(1, newImagePath);

        assertEquals(1, userId);
        assertEquals(newImagePath, userInformation.getImageProfile());
    }


    @Test
    void updateImageProfile_UserNotFound() {
        when(userInformationRepository.findByUserId(1)).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateImageProfile(1, "imageProfileUpdate");
        });
        assertEquals("La información del usuario no se encontró para el ID: 1", exception.getMessage());
        verify(userInformationRepository, never()).updateImageProfile(anyInt(), anyString());
    }

    @Test
    void updateImageProfile_ThrowsRuntimeException() {
        when(userInformationRepository.findByUserId(1)).thenReturn(Optional.of(userInformation));
        doThrow(new RuntimeException("Database error")).when(userInformationRepository).updateImageProfile(1, "new-image.jpg");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateImageProfile(1, "new-image.jpg");
        });
        assertEquals("Error updating image for user ID: 1", exception.getMessage());
    }


    @Test
    void shouldSet2faTrue() {
        when(this.userRepository.findById(1)).thenReturn(Optional.of(user));

        String expectedMessage = "2FA actualizada exitosamente!";
        String actualMessage = this.userService.set2fa(1);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldSet2faFalse() {
        user.setIs2FA(true);
        when(this.userRepository.findById(1)).thenReturn(Optional.of(user));

        String expectedMessage = "2FA actualizada exitosamente!";
        String actualMessage = this.userService.set2fa(1);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotSet2faByUser() {
        when(this.userRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            this.userService.set2fa(1);
        });

        String expectedMessage = "Usuario no encontrado.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldGetMyAppointments() {
        List<Appointment> mockAppointments = List.of(
                new Appointment(1, user, new Resource(), new BigDecimal("100.00"), "PENDING",
                        LocalDateTime.now(), LocalDateTime.of(2024, 10, 1, 9, 0),
                        LocalDateTime.of(2024, 10, 1, 10, 0),
                        new Employee(), new ArrayList<>()),
                new Appointment(2, user, new Resource(), new BigDecimal("150.00"), "PENDING",
                        LocalDateTime.now(), LocalDateTime.of(2024, 11, 15, 14, 0),
                        LocalDateTime.of(2024, 11, 15, 15, 0),
                        new Employee(), new ArrayList<>())
        );

        when(this.appointmentRepository.findByUserId(1)).thenReturn(mockAppointments);

        List<AppointmentResponse> appointmentResponses = this.userService.getMyAppointments(1);

        assertNotNull(appointmentResponses);
        assertEquals(2, appointmentResponses.size());

        assertEquals("PENDING", appointmentResponses.get(0).state());
        assertEquals(new BigDecimal("100.00"), appointmentResponses.get(0).total());
        assertEquals(LocalDateTime.of(2024, 10, 1, 9, 0), appointmentResponses.get(0).startTime());
        assertEquals(LocalDateTime.of(2024, 10, 1, 10, 0), appointmentResponses.get(0).endTime());

        assertEquals("PENDING", appointmentResponses.get(1).state());
        assertEquals(new BigDecimal("150.00"), appointmentResponses.get(1).total());
        assertEquals(LocalDateTime.of(2024, 11, 15, 14, 0), appointmentResponses.get(1).startTime());
        assertEquals(LocalDateTime.of(2024, 11, 15, 15, 0), appointmentResponses.get(1).endTime());
    }




}
