package com.Saul1727.Loldle1v1.services;


import com.Saul1727.Loldle1v1.models.ChampionModel;
import com.Saul1727.Loldle1v1.repository.ChampionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChampionService {
    private final ChampionRepository championRepository;

    public ChampionService(ChampionRepository championRepository){
        this.championRepository = championRepository;
    }

    public Optional<ChampionModel> findChampionByName(String name){
        return championRepository.findByName(name);
    }

    public List<ChampionModel> getAllChampions(){
        return championRepository.findAll();
    }
}
