package com.animate.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="loggedUsers")
@Setter
@Getter
public class LoggedUser extends User {

    private String bio;
    private String email;
    private String password;

    public LoggedUser() {
        super();
    }

    public LoggedUser(Integer id, String username, String bio, String email, String password) {
        super(id, username);
        this.bio = bio;
        this.email = email;
        this.password = password;
    }
}

