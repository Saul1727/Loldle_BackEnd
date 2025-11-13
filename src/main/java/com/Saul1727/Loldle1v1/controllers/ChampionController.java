package com.Saul1727.Loldle1v1.controllers;


import com.Saul1727.Loldle1v1.models.ChampionModel;
import com.Saul1727.Loldle1v1.services.ChampionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/champion")
public class ChampionController {

    private ChampionService championService;

    public ChampionController(ChampionService championService){
        this.championService = championService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<ChampionModel> getChampionByName(@PathVariable String name){
        Optional<ChampionModel> champion = championService.findChampionByName(name);

        return champion.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
        // Traduccion a castellano

        /*if (champion.isPresent()) {
            return ResponseEntity.ok(champion.get());
        } else {
            return ResponseEntity.notFound().build();
        }*/
    }
// Linkeado con el servicio el cual usa el getAll predefinido del repo.
    @GetMapping
    public List<ChampionModel> getAllChampions(){
        return championService.getAllChampions();
    }

}
