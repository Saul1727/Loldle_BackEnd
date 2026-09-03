package com.Saul1727.Loldle1v1.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DDragonChampionData {
    public String name;
    public String partype; // "Mana", "Energy", "None", etc.
    public Stats stats;
}
