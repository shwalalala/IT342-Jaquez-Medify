package com.edu.cit.jaquez.medify.auth.dto;

import com.edu.cit.jaquez.medify.user.UserResponse;

public record AuthResponse(UserResponse user, String accessToken) {
}
