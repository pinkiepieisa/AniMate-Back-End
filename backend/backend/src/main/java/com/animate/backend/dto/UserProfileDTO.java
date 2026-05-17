package com.animate.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do perfil público do usuário")
public class UserProfileDTO {

    @Schema(description = "Nome de usuário", example = "animador_supremo")
    private String username;

    @Schema(description = "Biografia do usuário", example = "Apaixonado por animação!")
    private String bio;

    @Schema(description = "URL pública da foto de perfil", example = "https://chibisafe.com/imagens/foto.png")
    private String imageUrl;

    @Schema(description = "UUID da foto de perfil no Chibisafe", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String imageUuid;

    public UserProfileDTO(String username, String bio, String imageUrl, String imageUuid) {
        this.username = username;
        this.bio = bio;
        this.imageUrl = imageUrl;
        this.imageUuid = imageUuid;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getImageUuid() { return imageUuid; }
    public void setImageUuid(String imageUuid) { this.imageUuid = imageUuid; }
}