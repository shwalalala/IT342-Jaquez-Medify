package com.edu.cit.jaquez.medify.profile;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.edu.cit.jaquez.medify.common.ApiResponse;
import com.edu.cit.jaquez.medify.profile.dto.ChangePasswordRequest;
import com.edu.cit.jaquez.medify.profile.dto.UpdateProfileRequest;
import com.edu.cit.jaquez.medify.user.UserResponse;

import java.util.Map;

@RestController
@RequestMapping("/users/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ApiResponse<UserResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(profileService.getProfile(userDetails.getUsername()));
    }

    @PutMapping
    public ApiResponse<UserResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ApiResponse.success(profileService.updateProfile(userDetails.getUsername(), request));
    }

    @PutMapping("/password")
    public ApiResponse<Map<String, String>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        profileService.changePassword(userDetails.getUsername(), request);
        return ApiResponse.success(Map.of("message", "Password changed successfully"));
    }

    @PostMapping("/image")
    public ApiResponse<UserResponse> uploadProfileImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("file") MultipartFile file
    ) {
        return ApiResponse.success(profileService.uploadProfileImage(userDetails.getUsername(), file));
    }
}
