package com.animate.backend.model;

//TO-DO Conectar a um banco

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;


@Entity
@Table(name="Messages")
@Getter
@Setter // anotações do lombok para getters e setters automáticos
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String from_user;
    private String username;
    private String text;
    private Timestamp time;



}
