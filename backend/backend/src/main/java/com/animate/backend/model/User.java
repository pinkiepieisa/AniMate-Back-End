package com.animate.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name ="Users")
@Inheritance(strategy = InheritanceType.JOINED)//é uma superclasse
@Setter @Getter // anotações do lombok para getters e setters automáticos
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String username;

    public User(){

    }


    public User(Integer id,String username) {
        this.username = username;
        this.id = id;
    }

    public void setusername(String username) {
    }
}
