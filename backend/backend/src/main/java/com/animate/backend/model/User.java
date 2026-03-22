package com.animate.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;
    private String bio;
    private String email;
    private String password;

    public User() {}

    public User(String username, String email, String password, String bio) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.bio = bio;
    }
}