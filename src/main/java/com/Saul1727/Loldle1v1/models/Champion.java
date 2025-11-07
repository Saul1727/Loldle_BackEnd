package com.Saul1727.Loldle1v1.models;

import com.Saul1727.Loldle1v1.models.Enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
public class Champion {

    @Id
    private String name;

    private String gender;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
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
