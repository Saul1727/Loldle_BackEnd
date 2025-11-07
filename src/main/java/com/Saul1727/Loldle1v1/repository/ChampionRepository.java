package com.Saul1727.Loldle1v1.repository;

import lombok.Builder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChampionRepository extends JpaRepository<com.Saul1727.Loldle1v1.repository.ChampionRepository, Long> {

}

