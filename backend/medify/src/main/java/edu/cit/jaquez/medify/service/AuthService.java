package edu.cit.jaquez.medify.service;

import edu.cit.jaquez.medify.dto.AdminLoginRequestDto;
import edu.cit.jaquez.medify.dto.AdminLoginResponseDto;
import edu.cit.jaquez.medify.entity.User;
import edu.cit.jaquez.medify.repository.UserRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

@PostConstruct
public void forceResetSuperAdminPassword() {
    userRepository.findByEmailIgnoreCase("superadmin@cit.edu")
        .ifPresent(user -> {
            user.setPassword(passwordEncoder.encode("admin123"));
            userRepository.save(user);
            System.out.println(" SuperAdmin password forcibly reset");
        });
}

            @PostConstruct
            public void migratePlainPasswordsToBCrypt() {
                userRepository.findAll().forEach(user -> {

                    // Only hash passwords that are NOT already BCrypt
                    if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                        user.setPassword(passwordEncoder.encode(user.getPassword()));
                        userRepository.save(user);
                    }
                });

                System.out.println("✅ Existing user passwords migrated to BCrypt");
            }



    public AdminLoginResponseDto loginAdmin(AdminLoginRequestDto request) {

        if (request.getEmail() == null || request.getPassword() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email and password are required"
            );
        }

        User user = userRepository
                .findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid email or password"
                        ));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                );
            }


        System.out.println(
                "DEBUG LOGIN => email=" + user.getEmail()
                        + ", isAdmin=" + user.getIsAdmin()
                        + ", rawRole='" + user.getRole() + "'"
        );

        if (!Boolean.TRUE.equals(user.getIsAdmin())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User is not authorized as Admin"
            );
        }

        AdminLoginResponseDto dto = new AdminLoginResponseDto();
        dto.setUserId(user.getUserId());
        dto.setFname(user.getFname());
        dto.setLname(user.getLname());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        return dto;
    }
}