package com.Saul1727.Loldle1v1.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UniverseChampionDto {
    public String name;
    public ChampionDetails champion;
    public Biography biography;
    public Image image;
    public List<Role> roles;
    public List<Race> races;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChampionDetails {
        @JsonProperty("release-date")
        public String releaseDate;

        @JsonProperty("associated-faction-slug")
        public String associatedFactionSlug;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Biography {
        public String full;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image {
        public String uri;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Role {
        public String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Race {
        public String name;
    }
}
