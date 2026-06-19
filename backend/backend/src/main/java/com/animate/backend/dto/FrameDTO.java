package com.animate.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "Representa um frame em uma camada")
public class FrameDTO {

    @Schema(description = "ID único do frame")
    private UUID id;

    @Schema(description = "Índice do frame na sequência (0, 1, 2...)")
    private Integer index;

    @Schema(description = "Duração do frame em milissegundos", example = "100")
    private Integer duration;

    @Schema(description = "Dados de desenho (strokes, etc.) em JSON")
    private String strokeData;

    public FrameDTO() {}

    public FrameDTO(UUID id, Integer index, Integer duration, String strokeData) {
        this.id = id;
        this.index = index;
        this.duration = duration;
        this.strokeData = strokeData;
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Integer getIndex() { return index; }
    public void setIndex(Integer index) { this.index = index; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getStrokeData() { return strokeData; }
    public void setStrokeData(String strokeData) { this.strokeData = strokeData; }
}
