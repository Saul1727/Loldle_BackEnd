package com.Saul1727.Loldle1v1.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "champion")
@Getter
@Setter
public class ChampionModel {

    @Id
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "gender")
    private String gender;

    @Column(name = "position")
    private String position;

    @Column(name = "range_type")
    private String rangeType;

    @Column(name = "region")
    private String region;

    @Column(name = "resource")
    private String resource;

    @Column(name = "species")
    private String species;

    @Lob
    @Column(name = "image", columnDefinition = "BYTEA")
    private byte[] image;
}