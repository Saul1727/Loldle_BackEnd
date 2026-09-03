package com.Saul1727.Loldle1v1.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

// --- DTOs para el JSON de DDragon (para los datos que faltan) ---

@JsonIgnoreProperties(ignoreUnknown = true)
public class DDragonResponseDto {
    // El JSON tiene una clave "data" que contiene un mapa de campeones
    public Map<String, DDragonChampionData> data;
}

