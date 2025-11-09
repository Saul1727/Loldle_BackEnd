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


    private String gender;

    @Lob
    @Column(columnDefinition = "bytea")
    private byte[] image;

    private String position;


    private String species;


    private String resource;


    private String rangeType;


    private String region;

    private int year;
}
