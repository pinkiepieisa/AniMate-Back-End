package com.animate.backend.model;

//TO-DO Conectar a um banco

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name="Messages")
@Getter
@Setter // anotações do lombok para getters e setters automáticos
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String from;
    private String username;
    private String text;
    private Timestamp time;



}
