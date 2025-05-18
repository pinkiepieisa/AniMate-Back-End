package com.animate.backend.model;

//TO-DO Conectar a um banco

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="menssages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String from;
    private String username;
    private String text;
    private Date sentOn;



}
