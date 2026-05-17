package com.animate.backend.dto;

import com.animate.backend.model.ProfilePic;
import com.animate.backend.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados da foto de perfil do usuário.")
public record PictureDTO(

        @Schema(description = "ID da foto de perfil", example = "1")
        Long id,

        @Schema(description = "URL pública da imagem", example = "https://chibisafe.com/imagens/foto.png")
        String imageUrl,

        @Schema(description = "UUID da imagem no Chibisafe", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        String imageUuid,

        @Schema(description = "Usuário dono da foto")
        User user

) {
    public PictureDTO(ProfilePic picture) {
        this(picture.getId(), picture.getImageUrl(), picture.getImageUuid(), picture.getUser());
    }
}