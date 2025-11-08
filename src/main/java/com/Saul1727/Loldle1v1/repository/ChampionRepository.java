package com.Saul1727.Loldle1v1.repository;

import com.Saul1727.Loldle1v1.models.Champion;
import lombok.Builder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChampionRepository extends JpaRepository<Champion, Long> {
    Optional<Champion> findByName(String name);


}

