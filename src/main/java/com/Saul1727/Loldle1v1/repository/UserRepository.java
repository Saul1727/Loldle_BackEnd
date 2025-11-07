package com.Saul1727.Loldle1v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<com.Saul1727.Loldle1v1.repository.UserRepository, Long> { }


