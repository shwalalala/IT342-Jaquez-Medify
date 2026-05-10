package com.edu.cit.jaquez.medify.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*([0-9]|[^A-Za-z0-9])).{6,}$",
                message = "Password must include at least 1 capital letter and 1 number or symbol")
        String newPassword,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {
}
