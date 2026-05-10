package com.edu.cit.jaquez.medify.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.edu.cit.jaquez.medify.auth.dto.*;
import com.edu.cit.jaquez.medify.security.JwtService;
import com.edu.cit.jaquez.medify.user.Role;
import com.edu.cit.jaquez.medify.user.User;
import com.edu.cit.jaquez.medify.user.UserRepository;
import com.edu.cit.jaquez.medify.user.UserResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final LoginAttemptService loginAttemptService;

    @Value("${app.dev-mode:true}")
    private boolean devMode;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       EmailService emailService,
                       LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.loginAttemptService = loginAttemptService;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        User user = new User();
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName() == null ? null : request.lastName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        assignVerificationToken(user);
        userRepository.save(user);

        String devLink = emailService.sendVerificationEmail(user);
        return new RegisterResponse(
                "Registration successful. Please verify your email before logging in.",
                devMode ? devLink : null
        );
    }

    @Transactional
    public AuthResponse verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification token"));

        if (user.getVerificationTokenExpiresAt() == null || user.getVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification token expired");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiresAt(null);
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        return new AuthResponse(UserResponse.from(user), accessToken);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String key = request.email();
        if (loginAttemptService.isBlocked(key)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many login attempts. Try again after 15 minutes.");
        }

        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> {
                    loginAttemptService.loginFailed(key);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
                });

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            loginAttemptService.loginFailed(key);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        if (!user.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Please verify your email before logging in");
        }

        loginAttemptService.loginSucceeded(key);
        String accessToken = jwtService.generateToken(user);
        return new AuthResponse(UserResponse.from(user), accessToken);
    }

    @Transactional
    public RegisterResponse resendVerification(ResendVerificationRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already verified");
        }

        assignVerificationToken(user);
        userRepository.save(user);
        String devLink = emailService.sendVerificationEmail(user);
        return new RegisterResponse("Verification email resent.", devMode ? devLink : null);
    }

    private void assignVerificationToken(User user) {
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
    }
}
