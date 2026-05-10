package com.edu.cit.jaquez.medify.dashboard;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.edu.cit.jaquez.medify.dashboard.dto.DashboardResponse;
import com.edu.cit.jaquez.medify.dashboard.dto.MonthlyExpensePoint;
import com.edu.cit.jaquez.medify.medication.Medication;
import com.edu.cit.jaquez.medify.medication.MedicationRepository;
import com.edu.cit.jaquez.medify.medication.dto.MedicationResponse;
import com.edu.cit.jaquez.medify.user.User;
import com.edu.cit.jaquez.medify.user.UserRepository;
import com.edu.cit.jaquez.medify.user.UserResponse;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class DashboardService {
    private final UserRepository userRepository;
    private final MedicationRepository medicationRepository;

    public DashboardService(UserRepository userRepository, MedicationRepository medicationRepository) {
        this.userRepository = userRepository;
        this.medicationRepository = medicationRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Medication> meds = medicationRepository.findByUserOrderByPurchaseDateDesc(user);
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());

        BigDecimal weekly = meds.stream()
                .filter(med -> !med.getPurchaseDate().isBefore(weekStart) && !med.getPurchaseDate().isAfter(weekEnd))
                .map(Medication::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthly = meds.stream()
                .filter(med -> !med.getPurchaseDate().isBefore(monthStart) && !med.getPurchaseDate().isAfter(monthEnd))
                .map(Medication::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal allTime = meds.stream()
                .map(Medication::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MedicationResponse> current = meds.stream()
                .sorted(Comparator.comparing(Medication::getPurchaseDate).reversed())
                .limit(5)
                .map(MedicationResponse::from)
                .toList();

        List<MedicationResponse> recent = medicationRepository.findTop5ByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(MedicationResponse::from)
                .toList();

        return new DashboardResponse(
                UserResponse.from(user),
                weekly,
                monthly,
                allTime,
                meds.size(),
                current,
                recent,
                monthlySeries(meds, now)
        );
    }

    private List<MonthlyExpensePoint> monthlySeries(List<Medication> meds, LocalDate now) {
        List<MonthlyExpensePoint> points = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i).withDayOfMonth(1);
            LocalDate start = month.withDayOfMonth(1);
            LocalDate end = month.withDayOfMonth(month.lengthOfMonth());
            BigDecimal total = meds.stream()
                    .filter(med -> !med.getPurchaseDate().isBefore(start) && !med.getPurchaseDate().isAfter(end))
                    .map(Medication::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            String label = month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + month.getYear();
            points.add(new MonthlyExpensePoint(label, total));
        }
        return points;
    }
}
