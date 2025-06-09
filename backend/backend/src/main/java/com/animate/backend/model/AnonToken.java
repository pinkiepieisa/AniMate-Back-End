package com.animate.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="anon_tokens")
@Getter
@Setter
public class AnonToken {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String anonToken;
    @ManyToOne @JsonIgnore
    private User user;
    private Long expirationTime;

    public AnonToken() {}

    public AnonToken(String anonToken, User user, Long expirationTime) {
        this.anonToken = anonToken;
        this.user = user;
        this.expirationTime = expirationTime;
    }

}
