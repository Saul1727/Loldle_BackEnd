package com.Saul1727.Loldle1v1.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UniverseIndexDto {
    public List<Entry> champions;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Entry {
        public String id;
        public String name;
    }
}
