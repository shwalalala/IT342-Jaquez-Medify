package com.edu.cit.jaquez.medify.medication;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.edu.cit.jaquez.medify.medication.dto.MedicationRequest;
import com.edu.cit.jaquez.medify.medication.dto.MedicationResponse;
import com.edu.cit.jaquez.medify.user.User;
import com.edu.cit.jaquez.medify.user.UserRepository;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;

@Service
public class MedicationService {
    private final MedicationRepository medicationRepository;
    private final UserRepository userRepository;

    public MedicationService(MedicationRepository medicationRepository, UserRepository userRepository) {
        this.medicationRepository = medicationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<MedicationResponse> getMedications(String email, String search, String period, String sort) {
        User user = getUser(email);
        List<Medication> medications = medicationRepository.findByUserOrderByPurchaseDateDesc(user);

        LocalDate now = LocalDate.now();
        if (period != null && !period.equalsIgnoreCase("all")) {
            medications = medications.stream().filter(med -> isInsidePeriod(med.getPurchaseDate(), now, period)).toList();
        }

        if (search != null && !search.isBlank()) {
            String term = search.trim().toLowerCase();
            medications = medications.stream()
                    .filter(med -> contains(med.getMedicineName(), term) || contains(med.getBrandName(), term) || contains(med.getPurpose(), term))
                    .toList();
        }

        Comparator<Medication> comparator = comparator(sort);
        return medications.stream()
                .sorted(comparator)
                .map(MedicationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MedicationResponse getMedication(String email, Long id) {
        return MedicationResponse.from(getMedicationEntity(email, id));
    }

    @Transactional
    public MedicationResponse createMedication(String email, MedicationRequest request) {
        User user = getUser(email);
        Medication medication = new Medication();
        medication.setUser(user);
        applyRequest(medication, request);
        return MedicationResponse.from(medicationRepository.save(medication));
    }

    @Transactional
    public MedicationResponse updateMedication(String email, Long id, MedicationRequest request) {
        Medication medication = getMedicationEntity(email, id);
        applyRequest(medication, request);
        return MedicationResponse.from(medicationRepository.save(medication));
    }

    @Transactional
    public void deleteMedication(String email, Long id) {
        Medication medication = getMedicationEntity(email, id);
        medicationRepository.delete(medication);
    }

    @Transactional(readOnly = true)
    public List<MedicationResponse> recent(String email) {
        User user = getUser(email);
        return medicationRepository.findTop5ByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(MedicationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal totalForCurrentMonth(User user) {
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());
        return medicationRepository.findByUserAndPurchaseDateBetweenOrderByPurchaseDateDesc(user, start, end)
                .stream()
                .map(Medication::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public User getUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Medication getMedicationEntity(String email, Long id) {
        User user = getUser(email);
        return medicationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medication not found"));
    }

    private void applyRequest(Medication medication, MedicationRequest request) {
        medication.setMedicineName(request.medicineName().trim());
        medication.setBrandName(request.brandName() == null ? null : request.brandName().trim());
        medication.setDosage(request.dosage().trim());
        medication.setPurpose(request.purpose().trim());
        medication.setQuantity(request.quantity());
        medication.setPrice(request.price());
        medication.setPurchaseDate(request.purchaseDate());
        medication.setNotes(request.notes());
    }

    private boolean contains(String value, String term) {
        return value != null && value.toLowerCase().contains(term);
    }

    private boolean isInsidePeriod(LocalDate date, LocalDate now, String period) {
        if (period.equalsIgnoreCase("week")) {
            LocalDate start = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate end = start.plusDays(6);
            return !date.isBefore(start) && !date.isAfter(end);
        }
        if (period.equalsIgnoreCase("month")) {
            return date.getMonth() == now.getMonth() && date.getYear() == now.getYear();
        }
        if (period.equalsIgnoreCase("lastMonth")) {
            LocalDate lastMonth = now.minusMonths(1);
            return date.getMonth() == lastMonth.getMonth() && date.getYear() == lastMonth.getYear();
        }
        return true;
    }

    private Comparator<Medication> comparator(String sort) {
        if (sort == null) return Comparator.comparing(Medication::getPurchaseDate).reversed();
        return switch (sort) {
            case "name" -> Comparator.comparing(m -> m.getMedicineName().toLowerCase());
            case "price" -> Comparator.comparing(Medication::getPrice).reversed();
            case "date_asc" -> Comparator.comparing(Medication::getPurchaseDate);
            default -> Comparator.comparing(Medication::getPurchaseDate).reversed();
        };
    }
}
