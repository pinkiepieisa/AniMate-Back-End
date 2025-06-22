package com.animate.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "logged_users")
@Getter
@Setter
public class LoggedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    public LoggedUser() {
    }

    public LoggedUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters e setters já são gerados pelo Lombok com @Getter e @Setter

}
