package com.Saul1727.Loldle1v1.repository;

import com.Saul1727.Loldle1v1.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> { 
    Optional<User> findByUsername(String username);
    Optional<User> createUser(User user, String password);
    Optional<User> loginUser(String username, String password);
    Optional<User> registerUser(User user);
}


