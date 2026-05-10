package com.edu.cit.jaquez.medify.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

import com.edu.cit.jaquez.medify.medication.dto.MedicationResponse;
import com.edu.cit.jaquez.medify.user.UserResponse;

public record DashboardResponse(
        UserResponse user,
        BigDecimal weeklyExpense,
        BigDecimal monthlyExpense,
        BigDecimal allTimeExpense,
        int currentMedicationCount,
        List<MedicationResponse> currentMedications,
        List<MedicationResponse> recentlyAdded,
        List<MonthlyExpensePoint> monthlySeries
) {
}
