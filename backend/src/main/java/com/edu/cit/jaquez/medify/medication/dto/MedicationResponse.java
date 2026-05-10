package com.edu.cit.jaquez.medify.medication.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.edu.cit.jaquez.medify.medication.Medication;

public record MedicationResponse(
        Long id,
        String medicineName,
        String brandName,
        String dosage,
        String purpose,
        Integer quantity,
        BigDecimal price,
        LocalDate purchaseDate,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MedicationResponse from(Medication medication) {
        return new MedicationResponse(
                medication.getId(),
                medication.getMedicineName(),
                medication.getBrandName(),
                medication.getDosage(),
                medication.getPurpose(),
                medication.getQuantity(),
                medication.getPrice(),
                medication.getPurchaseDate(),
                medication.getNotes(),
                medication.getCreatedAt(),
                medication.getUpdatedAt()
        );
    }
}
