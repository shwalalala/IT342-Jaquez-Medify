package com.edu.cit.jaquez.medify.medication;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edu.cit.jaquez.medify.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
    List<Medication> findByUserOrderByPurchaseDateDesc(User user);
    Optional<Medication> findByIdAndUser(Long id, User user);
    List<Medication> findTop5ByUserOrderByCreatedAtDesc(User user);
    List<Medication> findByUserAndPurchaseDateBetweenOrderByPurchaseDateDesc(User user, LocalDate start, LocalDate end);
}
