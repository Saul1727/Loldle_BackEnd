package com.Saul1727.Loldle1v1.services;

import com.Saul1727.Loldle1v1.repository.UserRepository;
import com.Saul1727.Loldle1v1.models.User;
import java.util.Optional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public  UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public Optional<User> createUser(User user, String password){
        return userRepository.createUser(user,password);
    }
    public Optional<User> findUserByUsername( String username){
        return userRepository.findByUsername(username);
    }
    public Optional<User> loginUser(String username, String password){
        return userRepository.loginUser(username, password);
    }
    public Optional<User> registerUser(User user){
        return userRepository.registerUser(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
