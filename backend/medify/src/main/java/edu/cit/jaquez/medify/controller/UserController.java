package edu.cit.jaquez.medify.controller;

import edu.cit.jaquez.medify.entity.User;
import edu.cit.jaquez.medify.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> all() {
        return userService.findAll();
    }

    @PostMapping
    // public User create(@RequestBody User u) {
    //     return u;
    // } this was for department before, no feature atm

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User u) {

        try {
            User existing = userService.findById(id);
            if (existing == null) {
                return ResponseEntity.notFound().build();
            }

            existing.setFname(u.getFname());
            existing.setLname(u.getLname());
            existing.setEmail(u.getEmail());
            existing.setRole(u.getRole());
            existing.setIsAdmin(u.getIsAdmin());

            // ✅ KEEP PASSWORD IF NOT PROVIDED
            if (u.getPassword() != null && !u.getPassword().isEmpty()) {
                existing.setPassword(u.getPassword());
            }

            existing.setUpdatedAt(LocalDateTime.now());

            return ResponseEntity.ok(userService.save(existing));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }

    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}