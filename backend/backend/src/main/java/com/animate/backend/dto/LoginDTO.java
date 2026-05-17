package com.animate.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Dados necessários para autenticação do usuário.")
public class LoginDTO {

    @Schema(description = "Email cadastrado do usuário", example = "usuario@email.com")
    private String email;

    @Schema(description = "Senha do usuário", example = "MinhaSenh@123")
    private String password;

    @Schema(description = "Se verdadeiro, o token terá duração maior (sessão prolongada)", example = "false")
    private boolean rememberMe;

}