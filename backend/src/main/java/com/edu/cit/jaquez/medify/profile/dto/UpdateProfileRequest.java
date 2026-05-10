package com.edu.cit.jaquez.medify.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 80, message = "First name is too long")
        String firstName,

        @Size(max = 80, message = "Last name is too long")
        String lastName
) {
}
