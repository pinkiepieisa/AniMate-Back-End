package com.animate.backend.dto;

import com.animate.backend.model.Drawing;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter // <-- ADICIONE AQUI
@Setter
@Schema(description = "Dados de um desenho salvo")
public class DrawingDTO {

    @Schema(description = "ID único do desenho")
    private UUID id;

    @Schema(description = "Título do desenho", example = "Meu esboço")
    private String title;

    @Schema(description = "Dados do canvas em JSON (strokes, camadas, etc.)")
    private String canvasData;

    @Schema(description = "Data de criação")
    private Instant createdAt;

    @Schema(description = "Data da última atualização")
    private Instant updatedAt;

    // Construtor completo (para GET /drawings/{id})
    public DrawingDTO(Drawing d) {
        this.id = d.getId();
        this.title = d.getTitle();
        this.canvasData = d.getCanvasData();
        this.createdAt = d.getCreatedAt();
        this.updatedAt = d.getUpdatedAt();
    }

    // Construtor resumido (para GET /drawings — listagem sem canvasData)
    public DrawingDTO(Drawing d, boolean summary) {
        this.id = d.getId();
        this.title = d.getTitle();
        this.createdAt = d.getCreatedAt();
        this.updatedAt = d.getUpdatedAt();
        if (!summary) {
            this.canvasData = d.getCanvasData();
        }
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getCanvasData() { return canvasData; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
