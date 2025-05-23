package com.animate.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="Tokens")
@Getter @Setter
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String token;
    @ManyToOne @Setter
    private LoggedUser user;
    private Long expirationTime;

    public Token() {}

    public Token(String token, LoggedUser user, Long expirationTime) {
        this.token = token;
        this.user = user;
        this.expirationTime = expirationTime;
    }

}
