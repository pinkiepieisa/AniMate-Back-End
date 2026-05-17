package com.animate.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para cadastro de um novo usuário.")
public class RegisterDTO {

    @Schema(description = "Nome de usuário único", example = "animador_supremo")
    private String username;

    @Schema(description = "Email do usuário", example = "usuario@email.com")
    private String email;

    @Schema(description = "Senha do usuário", example = "MinhaSenh@123")
    private String password;

    @Schema(description = "Confirmação da senha, deve ser igual ao campo password", example = "MinhaSenh@123")
    private String rePassword;

    @Schema(description = "Biografia do usuário (opcional)", example = "Apaixonado por animação!")
    private String bio;

}