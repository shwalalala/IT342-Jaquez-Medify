package com.edu.cit.jaquez.medify.user;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String role,
        String profileImageUrl,
        boolean emailVerified
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getProfileImageUrl(),
                user.isEmailVerified()
        );
    }
}
