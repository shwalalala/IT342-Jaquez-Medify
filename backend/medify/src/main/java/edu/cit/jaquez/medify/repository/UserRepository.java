package edu.cit.jaquez.medify.repository;

import edu.cit.jaquez.medify.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);
    
    @Query("SELECT u FROM User u")
    List<User> findAllUsers();
}