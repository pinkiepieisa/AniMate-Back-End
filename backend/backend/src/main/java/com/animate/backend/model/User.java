package com.animate.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name ="Users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String username;


    public String getusername() {
        return username;
    }
    public void setusername(String username) {
        this.username = username;
    }

    //construtores
    public User(){

    }

    public User(Integer id,String username) {
        this.username = username;
        this.id = id;
    }



}
