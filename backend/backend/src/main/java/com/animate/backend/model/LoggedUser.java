package com.animate.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="logged_users")
@Setter
@Getter // anotações do lombok para getters e setters automáticos
@PrimaryKeyJoinColumn(name = "id")
public class LoggedUser extends User {

    private String bio;
    private String email;
    private String password;

    public LoggedUser() {
        super();
    }

    public LoggedUser(String username, String bio, String email, String password) {
        super(username);
        this.bio = bio;
        this.email = email;
        this.password = password;
    }
}

