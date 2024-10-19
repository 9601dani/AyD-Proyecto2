package com.bugtrackers.ms_auth.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import com.bugtrackers.ms_auth.clients.EmailRestClient;
import com.bugtrackers.ms_auth.dto.request.AuthRequest;
import com.bugtrackers.ms_auth.dto.request.EmailRequest;
import com.bugtrackers.ms_auth.dto.request.LoginRequest;
import com.bugtrackers.ms_auth.dto.response.AuthResponse;
import com.bugtrackers.ms_auth.exceptions.EmailVerificationExpiredException;
import com.bugtrackers.ms_auth.exceptions.SettingNotFoundException;
import com.bugtrackers.ms_auth.exceptions.UserNotAllowedException;
import com.bugtrackers.ms_auth.exceptions.UserNotCreatedException;
import com.bugtrackers.ms_auth.exceptions.UserNotFoundException;
import com.bugtrackers.ms_auth.exceptions.UserNotVerifiedException;
import com.bugtrackers.ms_auth.models.CompanySetting;
import com.bugtrackers.ms_auth.models.EmailVerification;
import com.bugtrackers.ms_auth.models.Role;
import com.bugtrackers.ms_auth.models.User;
import com.bugtrackers.ms_auth.models.User2FA;
import com.bugtrackers.ms_auth.models.UserInformation;
import com.bugtrackers.ms_auth.models.UserRole;
import com.bugtrackers.ms_auth.repositories.AuthRepository;
import com.bugtrackers.ms_auth.repositories.CompanySettingRepository;
import com.bugtrackers.ms_auth.repositories.EmailVerificationRepository;
import com.bugtrackers.ms_auth.repositories.User2FARepository;
import com.bugtrackers.ms_auth.repositories.UserHasRoleRepository;
import com.bugtrackers.ms_auth.repositories.UserInformationRepository;

import java.util.Set;
import java.util.HashMap;

@TestPropertySource(properties = "website.url=https://testurl.com/verify-email")
public class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserHasRoleRepository userHasRoleRepository;

    @Mock
    private UserInformationRepository userInformationRepository;

    @Mock
    private EmailVerificationRepository emailVerificationRepository;

    @Mock
    private CompanySettingRepository companySettingRepository;

    @Mock
    private EmailRestClient emailClient;

    @Mock
    private User2FARepository user2faRepository;

    @InjectMocks
    private AuthService authService;

    private User mockUser;
    private AuthRequest authRequest;
    private AuthResponse authResponse;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(authService, "website", "http://localhost:4200");
        authRequest = new AuthRequest("email@example.com", "username", "password");
        mockUser = new User(1, "email@example.com", "username", "password", "token", true, true, true,
                LocalDateTime.now(),
                Set.of(new Role(2, "name", "description", LocalDateTime.now(), Set.of())));
        authResponse = new AuthResponse(mockUser);
        loginRequest = new LoginRequest("username", "password");
    }

    @Test
    void shouldRegisterNewUser() {

        EmailRequest emailRequest = new EmailRequest("email@example.com", "Verificación de Email", "value");

        HashMap<String, String> responseClient = new HashMap<>();
        responseClient.put("message", "message");

        CompanySetting companySetting = new CompanySetting();
        companySetting.setKeyName("name");
        companySetting.setKeyValue("value");

        User mockUserSaved = new User("email@example.com", "username", "password");
        Role role = new Role();
        role.setId(1);
        role.setName("name");
        role.setDescription("description");
        role.setCreatedAt(LocalDateTime.now());

        UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setUser(mockUserSaved);

        UserInformation userInformation = new UserInformation();
        userInformation.setUser(mockUserSaved);

        when(authRepository.findByUsername("username")).thenReturn(Optional.empty());
        when(authRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());
        when(authRepository.save(any(User.class))).thenReturn(mockUserSaved);
        when(passwordEncoder.encode("password")).thenReturn("encoded_password");
        when(userHasRoleRepository.save(userRole)).thenReturn(userRole);
        when(userInformationRepository.save(userInformation)).thenReturn(userInformation);
        when(companySettingRepository.findByKeyName("email_verification_template"))
                .thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_img")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companySetting));
        when(passwordEncoder.encode("email@example.com")).thenReturn("encoded_email");
        when(passwordEncoder.encode("usernameemail@example.com")).thenReturn("token");
        when(emailClient.sendEmail(emailRequest)).thenReturn(responseClient);
        when(passwordEncoder.encode(any(String.class))).thenReturn("tokenencoded");

        AuthResponse response = authService.register(authRequest);

        assertNotNull(response);
        assertEquals(authResponse.email(), response.email());
        assertEquals(authResponse.username(), response.username());
    }

    @Test
    void shouldNotRegisterNewUserFindByUsername() {
        when(authRepository.findByUsername("username")).thenReturn(Optional.of(mockUser));
        when(authRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotCreatedException.class, () -> {
            authService.register(authRequest);
        });

        String expectedMessage = "El nombre de usuario ya existe.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotRegisterNewUserFindByEmail() {
        when(authRepository.findByUsername("username")).thenReturn(Optional.empty());
        when(authRepository.findByEmail("email@example.com")).thenReturn(Optional.of(mockUser));

        Exception exception = assertThrows(UserNotCreatedException.class, () -> {
            authService.register(authRequest);
        });

        String expectedMessage = "El email ya existe.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotFindTemplate() {
        EmailRequest emailRequest = new EmailRequest("email@example.com", "Verificación de Email", "value");

        HashMap<String, String> responseClient = new HashMap<>();
        responseClient.put("message", "message");

        CompanySetting companySetting = new CompanySetting();
        companySetting.setKeyName("name");
        companySetting.setKeyValue("value");

        User mockUserSaved = new User("email@example.com", "username", "password");
        Role role = new Role();
        role.setId(1);
        role.setName("name");
        role.setDescription("description");
        role.setCreatedAt(LocalDateTime.now());

        UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setUser(mockUserSaved);

        UserInformation userInformation = new UserInformation();
        userInformation.setUser(mockUserSaved);
        when(authRepository.findByUsername("username")).thenReturn(Optional.empty());
        when(authRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());
        when(authRepository.save(any(User.class))).thenReturn(mockUserSaved);
        when(passwordEncoder.encode("password")).thenReturn("encoded_password");
        when(userHasRoleRepository.save(userRole)).thenReturn(userRole);
        when(userInformationRepository.save(userInformation)).thenReturn(userInformation);
        when(companySettingRepository.findByKeyName("email_verification_template")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("company_img")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companySetting));
        when(passwordEncoder.encode("email@example.com")).thenReturn("encoded_email");
        when(passwordEncoder.encode("usernameemail@example.com")).thenReturn("token");
        when(emailClient.sendEmail(emailRequest)).thenReturn(responseClient);
        when(passwordEncoder.encode(any(String.class))).thenReturn("tokenencoded");

        Exception exception = assertThrows(SettingNotFoundException.class, () -> {
            this.authService.register(authRequest);
        });

        String expectedMessage = "No se encontró la plantilla.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldNotFindLogo() {
        EmailRequest emailRequest = new EmailRequest("email@example.com", "Verificación de Email", "value");

        HashMap<String, String> responseClient = new HashMap<>();
        responseClient.put("message", "message");

        CompanySetting companySetting = new CompanySetting();
        companySetting.setKeyName("name");
        companySetting.setKeyValue("value");

        User mockUserSaved = new User("email@example.com", "username", "password");
        Role role = new Role();
        role.setId(1);
        role.setName("name");
        role.setDescription("description");
        role.setCreatedAt(LocalDateTime.now());

        UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setUser(mockUserSaved);

        UserInformation userInformation = new UserInformation();
        userInformation.setUser(mockUserSaved);
        when(authRepository.findByUsername("username")).thenReturn(Optional.empty());
        when(authRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());
        when(authRepository.save(any(User.class))).thenReturn(mockUserSaved);
        when(passwordEncoder.encode("password")).thenReturn("encoded_password");
        when(userHasRoleRepository.save(userRole)).thenReturn(userRole);
        when(userInformationRepository.save(userInformation)).thenReturn(userInformation);
        when(companySettingRepository.findByKeyName("email_verification_template"))
                .thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_img")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companySetting));
        when(passwordEncoder.encode("email@example.com")).thenReturn("encoded_email");
        when(passwordEncoder.encode("usernameemail@example.com")).thenReturn("token");
        when(emailClient.sendEmail(emailRequest)).thenReturn(responseClient);
        when(passwordEncoder.encode(any(String.class))).thenReturn("tokenencoded");

        Exception exception = assertThrows(SettingNotFoundException.class, () -> {
            this.authService.register(authRequest);
        });

        String expectedMessage = "No se encontró una configuración de la empresa.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldNotFindCompanyName() {
        EmailRequest emailRequest = new EmailRequest("email@example.com", "Verificación de Email", "value");

        HashMap<String, String> responseClient = new HashMap<>();
        responseClient.put("message", "message");

        CompanySetting companySetting = new CompanySetting();
        companySetting.setKeyName("name");
        companySetting.setKeyValue("value");

        User mockUserSaved = new User("email@example.com", "username", "password");
        Role role = new Role();
        role.setId(1);
        role.setName("name");
        role.setDescription("description");
        role.setCreatedAt(LocalDateTime.now());

        UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setUser(mockUserSaved);

        UserInformation userInformation = new UserInformation();
        userInformation.setUser(mockUserSaved);
        when(authRepository.findByUsername("username")).thenReturn(Optional.empty());
        when(authRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());
        when(authRepository.save(any(User.class))).thenReturn(mockUserSaved);
        when(passwordEncoder.encode("password")).thenReturn("encoded_password");
        when(userHasRoleRepository.save(userRole)).thenReturn(userRole);
        when(userInformationRepository.save(userInformation)).thenReturn(userInformation);
        when(companySettingRepository.findByKeyName("email_verification_template"))
                .thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_img")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("email@example.com")).thenReturn("encoded_email");
        when(passwordEncoder.encode("usernameemail@example.com")).thenReturn("token");
        when(emailClient.sendEmail(emailRequest)).thenReturn(responseClient);
        when(passwordEncoder.encode(any(String.class))).thenReturn("tokenencoded");

        Exception exception = assertThrows(SettingNotFoundException.class, () -> {
            this.authService.register(authRequest);
        });

        String expectedMessage = "No se encontró una configuración de la empresa.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldFindUserByUsername() {
        CompanySetting companySetting = new CompanySetting();
        companySetting.setKeyName("name");
        companySetting.setKeyValue("value");
        when(authRepository.findByUsernameOrEmail("username", "username")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(companySettingRepository.findByKeyName("email_2FA_template")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_img")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companySetting));

        loginRequest = new LoginRequest("username", "password");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(response.username(), "username");
        assertEquals(response.email(), "email@example.com");
        assertTrue(response.isActivated());
        assertTrue(response.isVerified());
    }

    @Test
    void shouldFindUserByEmail() {
        mockUser.setIs2FA(false);
        CompanySetting companySetting = new CompanySetting();
        companySetting.setKeyName("name");
        companySetting.setKeyValue("value");
        when(authRepository.findByUsernameOrEmail("email@example.com", "email@example.com"))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(companySettingRepository.findByKeyName("email_2FA_template")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_img")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companySetting));

        loginRequest = new LoginRequest("email@example.com", "password");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(response.username(), "username");
        assertEquals(response.email(), "email@example.com");
        assertTrue(response.isActivated());
        assertTrue(response.isVerified());
    }

    @Test
    void shouldNotFindUserByEmail() {
        when(authRepository.findByUsernameOrEmail("email@example.com", "email@example.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        loginRequest = new LoginRequest("email@example.com", "password");

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            authService.login(loginRequest);
        });

        String expectedMessage = "Usuario no encontrado.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotBeCorrectPassword() {
        when(authRepository.findByUsernameOrEmail("email@example.com", "email@example.com"))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(false);

        loginRequest = new LoginRequest("email@example.com", "password");

        Exception exception = assertThrows(UserNotAllowedException.class, () -> {
            authService.login(loginRequest);
        });

        String expectedMessage = "La contraseña no es correcta.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldFindUserNotActivated() {
        mockUser.setIsActivated(false);
        when(authRepository.findByUsernameOrEmail("email@example.com", "email@example.com"))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        loginRequest = new LoginRequest("email@example.com", "password");

        Exception exception = assertThrows(UserNotAllowedException.class, () -> {
            authService.login(loginRequest);
        });

        String expectedMessage = "El usuario está deshabilitado.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldFindUserNotVerified() {
        mockUser.setIsVerified(false);
        when(authRepository.findByUsernameOrEmail("email@example.com", "email@example.com"))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        loginRequest = new LoginRequest("email@example.com", "password");

        Exception exception = assertThrows(UserNotVerifiedException.class, () -> {
            authService.login(loginRequest);
        });

        String expectedMessage = "El usuario no se encuentra verificado.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldVerifyEmail() {
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setEmail("email@example.com");
        emailVerification.setToken("token");
        emailVerification.setExpiredAt(LocalDateTime.now().plusHours(1));

        when(this.emailVerificationRepository.findByTokenAndIsAvailable("token", true))
                .thenReturn(Optional.of(emailVerification));

        when(this.authRepository.findByEmail("email@example.com")).thenReturn(Optional.of(mockUser));

        String expectedMessage = "Usuario verificado exitosamente!";
        String message = this.authService.verifyEmail("token");
        assertNotNull(message);
        assertEquals(expectedMessage, message);
    }

    @Test
    void shouldNotFindEmailVerification() {
        when(this.emailVerificationRepository.findByTokenAndIsAvailable("token", true))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(EmailVerificationExpiredException.class, () -> {
            this.authService.verifyEmail("token");
        });

        String expectedMessage = "El token ya no se encuentra disponible.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldNotVerifyEmailByExpirationAt() {
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setEmail("email@example.com");
        emailVerification.setToken("token");
        emailVerification.setExpiredAt(LocalDateTime.now().minusHours(3));

        when(this.emailVerificationRepository.findByTokenAndIsAvailable("token", true))
                .thenReturn(Optional.of(emailVerification));

        Exception exception = assertThrows(EmailVerificationExpiredException.class, () -> {
            this.authService.verifyEmail("token");
        });

        String expectedMessage = "El token ya no se encuentra disponible.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotVerifyEmailByUserNotFound() {
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setEmail("email@example.com");
        emailVerification.setToken("token");
        emailVerification.setExpiredAt(LocalDateTime.now().plusHours(3));

        when(this.emailVerificationRepository.findByTokenAndIsAvailable("token", true))
                .thenReturn(Optional.of(emailVerification));

        when(this.authRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            this.authService.verifyEmail("token");
        });

        String expectedMessage = "Usuario no encontrado.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotFind2FATemplate() {
        CompanySetting companySetting = new CompanySetting();
        companySetting.setKeyName("name");
        companySetting.setKeyValue("value");
        when(authRepository.findByUsernameOrEmail("username", "username")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(this.companySettingRepository.findByKeyName("email_2FA_template")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("company_img")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companySetting));

        Exception exception = assertThrows(SettingNotFoundException.class, () -> {
            this.authService.login(loginRequest);
        });

        String expectedMessage = "No se encontró la plantilla.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotSend2FAByCompanyName() {
        CompanySetting companySetting = new CompanySetting();
        companySetting.setKeyName("name");
        companySetting.setKeyValue("value");
        when(authRepository.findByUsernameOrEmail("username", "username")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(this.companySettingRepository.findByKeyName("email_2FA_template")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_img")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.empty());

        Exception exception = assertThrows(SettingNotFoundException.class, () -> {
            this.authService.login(loginRequest);
        });

        String expectedMessage = "No se encontró una configuración de la empresa.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotSend2FAByCompanyLogo() {
        CompanySetting companySetting = new CompanySetting();
        companySetting.setKeyName("name");
        companySetting.setKeyValue("value");
        when(authRepository.findByUsernameOrEmail("username", "username")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(this.companySettingRepository.findByKeyName("email_2FA_template")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_img")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companySetting));

        Exception exception = assertThrows(SettingNotFoundException.class, () -> {
            this.authService.login(loginRequest);
        });

        String expectedMessage = "No se encontró una configuración de la empresa.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotSend2FAByCompanyLogoAndName() {
        CompanySetting companySetting = new CompanySetting();
        companySetting.setKeyName("name");
        companySetting.setKeyValue("value");
        when(authRepository.findByUsernameOrEmail("username", "username")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(this.companySettingRepository.findByKeyName("email_2FA_template")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_img")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.empty());

        Exception exception = assertThrows(SettingNotFoundException.class, () -> {
            this.authService.login(loginRequest);
        });

        String expectedMessage = "No se encontró una configuración de la empresa.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldSendEmailVerification() {
        when(this.authRepository.findByUsernameOrEmail("username", "username")).thenReturn(Optional.of(mockUser));
        CompanySetting companySetting = new CompanySetting();
        companySetting.setKeyName("name");
        companySetting.setKeyValue("value");
        when(authRepository.findByUsernameOrEmail("username", "username")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(this.companySettingRepository.findByKeyName("email_verification_template"))
                .thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_img")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companySetting));
        when(passwordEncoder.encode(any(String.class))).thenReturn("tokenencoded");

        String expectedMessage = "Correo enviado exitosamente!";
        String actualMessage = this.authService.reSendEmailVerification("username");
        assertNotNull(actualMessage);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotSendEmailVerificationById() {
        when(this.authRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            this.authService.reSendEmailVerification("username");
        });

        String expectedMessage = "Usuario no encontrado.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldSend2FA() {
        when(this.authRepository.findById(1)).thenReturn(Optional.of(mockUser));
        CompanySetting companySetting = new CompanySetting();
        companySetting.setKeyName("name");
        companySetting.setKeyValue("value");
        when(authRepository.findByUsernameOrEmail("username", "username")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(this.companySettingRepository.findByKeyName("email_2FA_template")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_img")).thenReturn(Optional.of(companySetting));
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companySetting));

        String expectedMessage = "Correo enviado exitosamente!";
        String actualMessage = this.authService.send2FA(1);
        assertNotNull(actualMessage);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotSend2FAById() {
        when(this.authRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            this.authService.send2FA(1);
        });

        String expectedMessage = "Usuario no encontrado.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldVerify2FA() {
        User2FA user2fa = new User2FA();
        user2fa.setId(1);
        user2fa.setSecretKey("secret");
        user2fa.setExpiredAt(LocalDateTime.now().plusHours(1));

        when(this.user2faRepository.findByUserIdAndSecretKeyAndIsAvailable(1, "secret", true))
                .thenReturn(Optional.of(user2fa));

        String expectedMessage = "Usuario autenticado exitosamente!";
        String actualMessage = this.authService.verify2FA(1, "secret");

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotVerify2FAByUser2FANotFound() {
        when(this.user2faRepository.findByUserIdAndSecretKeyAndIsAvailable(1, "secret", true))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(EmailVerificationExpiredException.class, () -> {
            this.authService.verify2FA(1, "secret");
        });

        String expectedMessage = "El token ya no se encuentra disponible.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotVerify2FAByEmailExpired() {
        User2FA user2fa = new User2FA();
        user2fa.setId(1);
        user2fa.setSecretKey("secret");
        user2fa.setExpiredAt(LocalDateTime.now().minusHours(1));

        when(this.user2faRepository.findByUserIdAndSecretKeyAndIsAvailable(1, "secret", true))
                .thenReturn(Optional.of(user2fa));

        Exception exception = assertThrows(EmailVerificationExpiredException.class, () -> {
            this.authService.verify2FA(1, "secret");
        });

        String expectedMessage = "El token ya no se encuentra disponible.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}
