package com.Saul1727.Loldle1v1.services;

import com.Saul1727.Loldle1v1.models.User;
import com.Saul1727.Loldle1v1.models.dtos.UserRegisterRequest;
import com.Saul1727.Loldle1v1.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Optional<User> findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    // Recibe el DTO de registro (no el User directamente) para que nadie pueda
    // colarnos un id u otro campo que no sea username/password.
    public User registerUser(UserRegisterRequest request){
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> loginUser(String username, String rawPassword){
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
