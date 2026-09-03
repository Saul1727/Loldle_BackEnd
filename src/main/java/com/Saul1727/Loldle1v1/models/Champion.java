package com.Saul1727.Loldle1v1.models;

import com.Saul1727.Loldle1v1.models.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Champion {

    @Id
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(columnDefinition = "bytea")
    private byte[] image;


    @Enumerated(EnumType.STRING)
    private Position position;

    @Enumerated(EnumType.STRING)
    private Species species;

    @Enumerated(EnumType.STRING)
    private Resource resource;

    @Enumerated(EnumType.STRING)
    private Range_Type rangeType;

    @Enumerated(EnumType.STRING)
    private Region region;

    private int year;
}
