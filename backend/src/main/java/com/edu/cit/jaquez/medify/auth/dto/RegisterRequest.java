package com.edu.cit.jaquez.medify.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*([0-9]|[^A-Za-z0-9])).{6,}$",
                message = "Password must include at least 1 capital letter and 1 number or symbol")
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {
}
