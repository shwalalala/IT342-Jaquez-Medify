package com.edu.cit.jaquez.medify.medication.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MedicationRequest(
        @NotBlank(message = "Medicine name is required")
        @Size(max = 160, message = "Medicine name is too long")
        String medicineName,

        @Size(max = 160, message = "Brand name is too long")
        String brandName,

        @NotBlank(message = "Dosage is required")
        @Size(max = 80, message = "Dosage is too long")
        String dosage,

        @NotBlank(message = "Purpose is required")
        String purpose,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        Integer quantity,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @NotNull(message = "Date purchased is required")
        @PastOrPresent(message = "Date purchased cannot be in the future")
        LocalDate purchaseDate,

        String notes
) {
}
