package com.animate.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="AnonUsers")
@Setter
@Getter
public class AnonUser extends User {

    @Id
    private Integer anonId;



}
