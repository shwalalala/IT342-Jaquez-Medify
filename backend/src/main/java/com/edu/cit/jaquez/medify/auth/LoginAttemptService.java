package com.edu.cit.jaquez.medify.auth;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 5;
    private static final int WINDOW_MINUTES = 15;

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String key) {
        Attempt attempt = attempts.get(normalize(key));
        if (attempt == null) return false;
        if (attempt.lastAttempt.plusMinutes(WINDOW_MINUTES).isBefore(LocalDateTime.now())) {
            attempts.remove(normalize(key));
            return false;
        }
        return attempt.count >= MAX_ATTEMPTS;
    }

    public void loginSucceeded(String key) {
        attempts.remove(normalize(key));
    }

    public void loginFailed(String key) {
        String normalized = normalize(key);
        attempts.compute(normalized, (ignored, old) -> {
            if (old == null || old.lastAttempt.plusMinutes(WINDOW_MINUTES).isBefore(LocalDateTime.now())) {
                return new Attempt(1, LocalDateTime.now());
            }
            return new Attempt(old.count + 1, LocalDateTime.now());
        });
    }

    private String normalize(String key) {
        return key == null ? "unknown" : key.trim().toLowerCase();
    }

    private record Attempt(int count, LocalDateTime lastAttempt) {
    }
}
