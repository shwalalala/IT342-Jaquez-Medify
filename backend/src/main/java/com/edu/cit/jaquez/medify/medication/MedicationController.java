package com.edu.cit.jaquez.medify.medication;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.edu.cit.jaquez.medify.common.ApiResponse;
import com.edu.cit.jaquez.medify.medication.dto.MedicationRequest;
import com.edu.cit.jaquez.medify.medication.dto.MedicationResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/medications")
public class MedicationController {
    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @GetMapping
    public ApiResponse<List<MedicationResponse>> getMedications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "all") String period,
            @RequestParam(defaultValue = "date_desc") String sort
    ) {
        return ApiResponse.success(medicationService.getMedications(userDetails.getUsername(), search, period, sort));
    }

    @GetMapping("/recent")
    public ApiResponse<List<MedicationResponse>> recent(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(medicationService.recent(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ApiResponse<MedicationResponse> getMedication(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        return ApiResponse.success(medicationService.getMedication(userDetails.getUsername(), id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MedicationResponse>> createMedication(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MedicationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(medicationService.createMedication(userDetails.getUsername(), request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<MedicationResponse> updateMedication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody MedicationRequest request
    ) {
        return ApiResponse.success(medicationService.updateMedication(userDetails.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, String>> deleteMedication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        medicationService.deleteMedication(userDetails.getUsername(), id);
        return ApiResponse.success(Map.of("message", "Medication deleted successfully"));
    }
}
