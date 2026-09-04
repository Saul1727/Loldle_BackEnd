package com.Saul1727.Loldle1v1.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// Estructura real de /champions/{slug}/index.json (verificada contra la respuesta real
// de la API, no contra documentación: la Universe API no es oficial ni está documentada).
// OJO: release-date, associated-faction-slug y races van DENTRO de "champion", no en la raíz.
@JsonIgnoreProperties(ignoreUnknown = true)
public class UniverseChampionDto {
    public String id;
    public String name;
    public ChampionDetails champion;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChampionDetails {
        @JsonProperty("release-date")
        public String releaseDate;

        @JsonProperty("associated-faction-slug")
        public String associatedFactionSlug;

        public List<Race> races;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Race {
        public String name;
        public String slug;
    }
}
