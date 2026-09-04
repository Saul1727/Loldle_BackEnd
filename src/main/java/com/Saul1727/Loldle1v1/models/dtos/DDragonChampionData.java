package com.Saul1727.Loldle1v1.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DDragonChampionData {
    // El id (p.ej. "MonkeyKing" para Wukong) es distinto del name ("Wukong") y,
    // en minúsculas, coincide con el slug que usa la Universe API para cada campeón.
    public String id;
    public String name;
    public String partype; // "Mana", "Energy", "None", etc.
    public Stats stats;
}
