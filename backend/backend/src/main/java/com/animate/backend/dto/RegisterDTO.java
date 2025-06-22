package com.animate.backend.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO implements Serializable {
    private String username;
    private String email;
    private String password;
    private String rePassword;
}
