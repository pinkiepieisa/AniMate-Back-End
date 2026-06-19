package com.animate.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@Schema(description = "Representa uma camada em uma animação")
public class LayerDTO {

    // Getters e Setters
    @Schema(description = "ID único da camada")
    private UUID id;

    @Schema(description = "Nome da camada", example = "Background")
    private String name;

    @Schema(description = "Opacidade (0.0 a 1.0)", example = "1.0")
    private Double opacity;

    @Schema(description = "Se a camada está visível")
    private Boolean visible;

    @Schema(description = "Frames que pertencem a esta camada")
    private List<FrameDTO> frames;

    public LayerDTO() {}

    public LayerDTO(UUID id, String name, Double opacity, Boolean visible, List<FrameDTO> frames) {
        this.id = id;
        this.name = name;
        this.opacity = opacity;
        this.visible = visible;
        this.frames = frames;
    }

}
