package com.bugtrackers.ms_auth.services;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserHasRoleRepository userHasRoleRepository;
    private final UserInformationRepository userInformationRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final CompanySettingRepository companySettingRepository;
    private final EmailRestClient emailClient;
    private final User2FARepository user2FARepository;

    @Value("${website.url}")
    private String website;

    public AuthResponse register(AuthRequest user) {
        Optional<User> existingUser = authRepository.findByEmail(user.email());
        if (existingUser.isPresent()) {
            throw new UserNotCreatedException("El email ya existe.");
        }
        existingUser = authRepository.findByUsername(user.username());
        if (existingUser.isPresent()) {
            throw new UserNotCreatedException("El nombre de usuario ya existe.");
        }
        User newUser = new User();
        newUser.setEmail(user.email());
        newUser.setUsername(user.username());

        newUser.setPassword(passwordEncoder.encode(user.password()));

        User userSaved = authRepository.save(newUser);

        UserInformation userInformation = new UserInformation();
        userInformation.setUser(userSaved);

        this.userInformationRepository.save(userInformation);

        Role role = new Role();
        role.setId(2);

        UserRole userRole = new UserRole();
        userRole.setUser(userSaved);
        userRole.setRole(role);

        this.userHasRoleRepository.save(userRole);

        sendEmail(userSaved.getEmail(), "email_verification_template", "/verify-email", "Verificación de email.", false);

        AuthResponse response = new AuthResponse(userSaved);
        return response;
    }

    private void sendEmail(String email, String templateKeyName, String websitePath, String subject, Boolean isResetPassword) {
        Optional<CompanySetting> optionalTemplate = this.companySettingRepository
            .findByKeyName(templateKeyName);

        if(optionalTemplate.isEmpty()) {
            throw new SettingNotFoundException("No se encontró la plantilla.");
        }

        UUID uuid = UUID.randomUUID();
        CompanySetting template = optionalTemplate.get();
        String token = this.passwordEncoder.encode(uuid.toString());

        String htmlTemplate = template.getKeyValue();
        htmlTemplate = htmlTemplate.replace("WEBSITE", this.website + websitePath);
        htmlTemplate = htmlTemplate.replace("TOKEN", token);

        Optional<CompanySetting> optionalLogo = this.companySettingRepository.findByKeyName("company_img");
        Optional<CompanySetting> optionalName = this.companySettingRepository.findByKeyName("company_name");

        if(optionalLogo.isEmpty() || optionalName.isEmpty()) {
            throw new SettingNotFoundException("No se encontró una configuración de la empresa.");
        }

        CompanySetting logo = optionalLogo.get();
        CompanySetting name = optionalName.get();

        htmlTemplate = htmlTemplate.replace("COMPANY_LOGO", logo.getKeyValue());
        htmlTemplate = htmlTemplate.replace("COMPANY_NAME", name.getKeyValue());

        EmailRequest emailRequest = new EmailRequest(email, subject, htmlTemplate, false);

        this.emailClient.sendEmail(emailRequest);

        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setEmail(email);
        emailVerification.setToken(token);
        emailVerification.setIsResetPassword(isResetPassword);

        this.emailVerificationRepository.save(emailVerification);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Optional<User> userOptional = this.authRepository.findByUsernameOrEmail(loginRequest.usernameOrEmail(), loginRequest.usernameOrEmail());

        if(userOptional.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        User user = userOptional.get();

        if(!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new UserNotAllowedException("La contraseña no es correcta.");
        }

        if(!user.getIsActivated()) {
            throw new UserNotAllowedException("El usuario está deshabilitado.");
        }

        if(!user.getIsVerified()) {
            throw new UserNotVerifiedException("El usuario no se encuentra verificado.");
        }

        String token = tokenService.getToken(user);
        user.setAuthToken(token);

        this.authRepository.save(user);

        if(user.getIs2FA()) {
            send2FAEmail(user);
        }

        AuthResponse response = new AuthResponse(user);

        return response;
    }

    private void send2FAEmail(User user) {
        Optional<CompanySetting> optionalTemplate = this.companySettingRepository.findByKeyName("email_2FA_template");

        if(optionalTemplate.isEmpty()) {
            throw new SettingNotFoundException("No se encontró la plantilla.");
        }

        String token = UUID.randomUUID().toString().substring(0, 6);
        String htmlTemplate = optionalTemplate.get().getKeyValue();
        htmlTemplate = htmlTemplate.replace("TOKEN_GENERATED", token);

        Optional<CompanySetting> optionalLogo = this.companySettingRepository.findByKeyName("company_img");
        Optional<CompanySetting> optionalName = this.companySettingRepository.findByKeyName("company_name");

        if(optionalLogo.isEmpty() || optionalName.isEmpty()) {
            throw new SettingNotFoundException("No se encontró una configuración de la empresa.");
        }

        CompanySetting logo = optionalLogo.get();
        CompanySetting name = optionalName.get();

        htmlTemplate = htmlTemplate.replace("COMPANY_LOGO", logo.getKeyValue());
        htmlTemplate = htmlTemplate.replace("COMPANY_NAME", name.getKeyValue());

        EmailRequest emailRequest = new EmailRequest(user.getEmail(), "Código de autenticación.", htmlTemplate, false);

        this.emailClient.sendEmail(emailRequest);

        User2FA user2FA = new User2FA();
        user2FA.setUser(user);
        user2FA.setSecretKey(token);

        this.user2FARepository.save(user2FA);

    }

    public String verifyEmail(String token) {
        Optional<EmailVerification> eOptional = this.emailVerificationRepository.findByTokenAndIsAvailable(token, true);

        if(eOptional.isEmpty()) {
            throw new EmailVerificationExpiredException("El token no es válido.");
        }

        EmailVerification emailVerification = eOptional.get();

        if(LocalDateTime.now().isAfter(emailVerification.getExpiredAt())) {
            emailVerification.setIsAvailable(false);
            this.emailVerificationRepository.save(emailVerification);
            throw new EmailVerificationExpiredException("El token ya no se encuentra disponible.");
        }

        if(emailVerification.getIsResetPassword()) {
            throw new EmailVerificationExpiredException("El token no es válido.");
        }

        Optional<User> uOptional = this.authRepository.findByEmail(emailVerification.getEmail());

        if(uOptional.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        User user = uOptional.get();
        user.setIsVerified(true);
        this.authRepository.save(user);

        emailVerification.setIsAvailable(false);
        this.emailVerificationRepository.save(emailVerification);
        return "Usuario verificado exitosamente!";
    }


    public String sendVerification(String emailOrUsername) {
        Optional<User> uOptional = this.authRepository.findByUsernameOrEmail(emailOrUsername, emailOrUsername);

        if(uOptional.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        String email = uOptional.get().getEmail();
        sendEmail(email, "email_verification_template", "/verify-email", "Verificación de email.", false);

        return "Correo enviado exitosamente!";
    }

    public String send2FA(Integer id) {
        Optional<User> uOptional = this.authRepository.findById(id);

        if(uOptional.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        send2FAEmail(uOptional.get());

        return "Correo enviado exitosamente!";
    }

    public String verify2FA(Integer id, String code) {
        Optional<User2FA> uOptional = this.user2FARepository.findByUserIdAndSecretKeyAndIsAvailable(id, code, true);
        if(uOptional.isEmpty()) {
            throw new EmailVerificationExpiredException("El token no es válido.");
        }

        User2FA user2fa = uOptional.get();

        if(LocalDateTime.now().isAfter(user2fa.getExpiredAt())) {
            user2fa.setIsAvailable(false);
            this.user2FARepository.save(user2fa);
            throw new EmailVerificationExpiredException("El token ya no se encuentra disponible.");
        }

        user2fa.setIsAvailable(false);
        this.user2FARepository.save(user2fa);

        return "Usuario autenticado exitosamente!";
    }

    public String sendRecoveryPassword(String email) {
        Optional<User> uOptional = this.authRepository.findByEmail(email);

        if(uOptional.isEmpty()){
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        sendEmail(email, "email_recovery_password_template", "/reset-password", "Recuperación de contraseña", true);
        return "Correo enviado exitosamente!";
    }

    public String resetPassword(String token, String password) {
        Optional<EmailVerification> eOptional = this.emailVerificationRepository.findByTokenAndIsAvailable(token, true);

        if(eOptional.isEmpty()) {
            throw new EmailVerificationExpiredException("El token no es válido.");
        }

        EmailVerification emailVerification = eOptional.get();

        if(!emailVerification.getIsResetPassword()) {
            throw new EmailVerificationExpiredException("El token no es válido.");
        }

        if(LocalDateTime.now().isAfter(emailVerification.getExpiredAt())) {
            emailVerification.setIsAvailable(false);
            this.emailVerificationRepository.save(emailVerification);
            throw new EmailVerificationExpiredException("El token ya no se encuentra disponible.");
        }

        Optional<User> uOptional = this.authRepository.findByEmail(emailVerification.getEmail());

        if(uOptional.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        User user = uOptional.get();
        user.setPassword(this.passwordEncoder.encode(password));
        this.authRepository.save(user);
        emailVerification.setIsAvailable(false);
        this.emailVerificationRepository.save(emailVerification);

        return "Contraseña actualizada exitosamente!";
    }

}
