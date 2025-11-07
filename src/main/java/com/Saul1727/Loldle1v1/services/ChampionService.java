package com.Saul1727.Loldle1v1.services;


import com.Saul1727.Loldle1v1.repository.ChampionRepository;
import org.springframework.stereotype.Service;

@Service
public class ChampionService {
    private final ChampionRepository championRepository;

    public ChampionService(ChampionRepository championRepository){
        this.championRepository = championRepository;
    }

}
