package com.Saul1727.Loldle1v1.controllers;

import com.Saul1727.Loldle1v1.models.User;
import com.Saul1727.Loldle1v1.models.dtos.LoginRequest;
import com.Saul1727.Loldle1v1.models.dtos.UserRegisterRequest;
import com.Saul1727.Loldle1v1.security.LoginAttemptGuard;
import com.Saul1727.Loldle1v1.services.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@CrossOrigin(origins = "${app.frontend-origin:http://localhost:3000}")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final LoginAttemptGuard loginAttemptGuard;

    public UserController(UserService userService, LoginAttemptGuard loginAttemptGuard){
        this.userService = userService;
        this.loginAttemptGuard = loginAttemptGuard;
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username){
        Optional<User> user = userService.findUserByUsername(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        try {
            User savedUser = userService.registerUser(request);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        String username = request.getUsername();

        if (loginAttemptGuard.isBlocked(username)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Demasiados intentos fallidos, inténtalo de nuevo más tarde");
        }

        Optional<User> user = userService.loginUser(username, request.getPassword());
        if (user.isPresent()) {
            loginAttemptGuard.onSuccessfulLogin(username);
            return ResponseEntity.ok(user.get());
        }

        loginAttemptGuard.onFailedAttempt(username);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos");
    }
}
