package edu.cit.jaquez.medify.controller;

import edu.cit.jaquez.medify.dto.AdminLoginRequestDto;
import edu.cit.jaquez.medify.dto.AdminLoginResponseDto;
import edu.cit.jaquez.medify.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/admin/login")
    public ResponseEntity<AdminLoginResponseDto> loginAdmin(
            @RequestBody AdminLoginRequestDto request) {

        AdminLoginResponseDto response = authService.loginAdmin(request);
        return ResponseEntity.ok(response);
    }

    
}