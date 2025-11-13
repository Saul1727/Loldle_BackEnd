package com.Saul1727.Loldle1v1.repository;

import com.Saul1727.Loldle1v1.models.ChampionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChampionRepository extends JpaRepository<ChampionModel, Long> {
    Optional<ChampionModel> findByName(String name);


}

