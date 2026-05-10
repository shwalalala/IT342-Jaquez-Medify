package com.edu.cit.jaquez.medify.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edu.cit.jaquez.medify.auth.dto.*;
import com.edu.cit.jaquez.medify.common.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(authService.register(request)));
    }

    @GetMapping("/verify-email")
    public ApiResponse<AuthResponse> verifyEmail(@RequestParam String token) {
        return ApiResponse.success(authService.verifyEmail(token));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/resend-verification")
    public ApiResponse<RegisterResponse> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        return ApiResponse.success(authService.resendVerification(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Map<String, String>> logout() {
        return ApiResponse.success(Map.of("message", "Logged out successfully"));
    }
}
