package com.Saul1727.Loldle1v1.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

// Freno sencillo de fuerza bruta en el login. Nada de Redis ni librerías externas,
// solo un contador en memoria por usuario. Con más de una instancia del backend
// esto habría que moverlo a algo compartido, pero para el alcance actual sobra.
@Component
public class LoginAttemptGuard {

    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_MINUTES = 15;

    private final ConcurrentHashMap<String, Attempts> attemptsByUser = new ConcurrentHashMap<>();

    public boolean isBlocked(String username) {
        Attempts attempts = attemptsByUser.get(username.toLowerCase());
        if (attempts == null) {
            return false;
        }
        if (attempts.count < MAX_ATTEMPTS) {
            return false;
        }
        boolean stillBlocked = Instant.now().isBefore(attempts.blockedUntil());
        if (!stillBlocked) {
            attemptsByUser.remove(username.toLowerCase());
        }
        return stillBlocked;
    }

    public void onFailedAttempt(String username) {
        attemptsByUser.compute(username.toLowerCase(), (key, current) -> {
            if (current == null) {
                return new Attempts(1, Instant.now());
            }
            return new Attempts(current.count + 1, current.firstAttempt);
        });
    }

    public void onSuccessfulLogin(String username) {
        attemptsByUser.remove(username.toLowerCase());
    }

    private record Attempts(int count, Instant firstAttempt) {
        Instant blockedUntil() {
            return firstAttempt.plusSeconds(BLOCK_MINUTES * 60);
        }
    }
}
