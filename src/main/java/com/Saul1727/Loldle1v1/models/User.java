package com.Saul1727.Loldle1v1.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(name = "app_user")
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private Long id;
    private String username;
    private String password;
}

