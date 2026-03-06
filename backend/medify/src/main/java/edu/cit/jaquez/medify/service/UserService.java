package edu.cit.jaquez.medify.service;

import edu.cit.jaquez.medify.entity.User;
import edu.cit.jaquez.medify.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User save(User u) {
        LocalDateTime now = LocalDateTime.now();
        if (u.getCreatedAt() == null) u.setCreatedAt(now);
        u.setUpdatedAt(now);

        if (u.getUserId() == null) { 
            if (userRepository.findByEmailIgnoreCase(u.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already registered");
            }
        }

        return userRepository.save(u);
    }

    public List<User> findAll() {
        return userRepository.findAllUsers();
    }

    public User findById(Long id) {
    return userRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) { userRepository.deleteById(id); }
}