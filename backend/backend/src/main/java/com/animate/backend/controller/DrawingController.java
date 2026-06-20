package com.animate.backend.controller;

import com.animate.backend.dto.DrawingDTO;
import com.animate.backend.dto.FrameDTO;
import com.animate.backend.dto.LayerDTO;
import com.animate.backend.service.DrawingService;

import com.animate.backend.service.LayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/drawings")
@Tag(name = "Desenhos", description = "Salvar, listar, abrir e editar desenhos do usuário.")
public class DrawingController {

    private final LayerService layerService;
    private final DrawingService drawingService;

    public DrawingController(DrawingService drawingService, LayerService layerService) {
        this.drawingService = drawingService;
        this.layerService = layerService;
    }




    // ── Request bodies ────────────────────────────────────────────────────────
    public record UpdateLayerVisibilityRequest(Boolean visible) {}

    public record DuplicateLayerRequest(String name) {}

    public record AddLayerRequest(String name) {}

    public record UpdateLayerNameRequest(String name) {}

    public record UpdateLayerOpacityRequest(Double opacity) {}

    public record SaveRequest(String title, String canvasData) {}

    public record UpdateRequest(String title, String canvasData) {}

    // ── Endpoints ─────────────────────────────────────────────────────────────

    // ── Endpoints de Camadas ──────────────────────────────────────────────────

    @Operation(summary = "Atualizar visibilidade da camada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Visibilidade atualizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Camada não encontrada.")
    })
    @PutMapping("/{drawingId}/layers/{layerId}/visibility")
    public ResponseEntity<?> updateLayerVisibility(@RequestParam String token,
                                                   @PathVariable UUID drawingId,
                                                   @PathVariable UUID layerId,
                                                   @RequestBody UpdateLayerVisibilityRequest request) {
        if (request.visible() == null) {
            return ResponseEntity.badRequest().body("Campo 'visible' é obrigatório.");
        }
        LayerDTO updated = layerService.updateLayerVisibility(token, drawingId, layerId, request.visible());
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Camada não encontrada.");
        }
        return ResponseEntity.ok(updated);
    }

    // ── Novo endpoint: duplicar camada (com conteúdo/frames copiados) ─
    @Operation(summary = "Duplicar camada",
            description = "Cria uma nova camada copiando nome, opacidade, visibilidade e frames da camada de origem.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Camada duplicada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Camada de origem não encontrada.")
    })
    @PostMapping("/{drawingId}/layers/{layerId}/duplicate")
    public ResponseEntity<?> duplicateLayer(@RequestParam String token,
                                            @PathVariable UUID drawingId,
                                            @PathVariable UUID layerId,
                                            @RequestBody(required = false) DuplicateLayerRequest request) {
        String desiredName = (request != null) ? request.name() : null;
        LayerDTO duplicated = layerService.duplicateLayer(token, drawingId, layerId, desiredName);
        if (duplicated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Camada não encontrada.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(duplicated);
    }

    @Operation(summary = "Listar camadas de um desenho")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de camadas retornada."),
            @ApiResponse(responseCode = "404", description = "Desenho não encontrado.")
    })
    @GetMapping("/{drawingId}/layers")
    public ResponseEntity<?> getLayers(@RequestParam String token,
                                       @PathVariable UUID drawingId) {
        List<LayerDTO> layers = layerService.getLayers(token, drawingId);
        if (layers == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Desenho não encontrado.");
        }
        return ResponseEntity.ok(layers);
    }

    @Operation(summary = "Adicionar camada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Camada criada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Desenho não encontrado.")
    })
    @PostMapping("/{drawingId}/layers")
    public ResponseEntity<?> addLayer(@RequestParam String token,
                                      @PathVariable UUID drawingId,
                                      @RequestBody AddLayerRequest request) {
        LayerDTO layer = layerService.addLayer(token, drawingId, request.name());
        if (layer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Desenho não encontrado.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(layer);
    }

    @Operation(summary = "Deletar camada")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Camada deletada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Camada não encontrada.")
    })
    @DeleteMapping("/{drawingId}/layers/{layerId}")
    public ResponseEntity<?> removeLayer(@RequestParam String token,
                                         @PathVariable UUID drawingId,
                                         @PathVariable UUID layerId) {
        boolean deleted = layerService.removeLayer(token, drawingId, layerId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Camada não encontrada.");
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar nome da camada")
    @PutMapping("/{drawingId}/layers/{layerId}/name")
    public ResponseEntity<?> updateLayerName(@RequestParam String token,
                                             @PathVariable UUID drawingId,
                                             @PathVariable UUID layerId,
                                             @RequestBody UpdateLayerNameRequest request) {
        LayerDTO updated = layerService.updateLayerName(token, drawingId, layerId, request.name());
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Camada não encontrada.");
        }
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Atualizar opacidade da camada")
    @PutMapping("/{drawingId}/layers/{layerId}/opacity")
    public ResponseEntity<?> updateLayerOpacity(@RequestParam String token,
                                                @PathVariable UUID drawingId,
                                                @PathVariable UUID layerId,
                                                @RequestBody UpdateLayerOpacityRequest request) {
        LayerDTO updated = layerService.updateLayerOpacity(token, drawingId, layerId, request.opacity());
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Camada não encontrada ou opacidade inválida.");
        }
        return ResponseEntity.ok(updated);
    }

// ── Endpoints de Frames ───────────────────────────────────────────────────

    @Operation(summary = "Adicionar frame a uma camada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Frame criado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Camada não encontrada.")
    })
    @PostMapping("/{drawingId}/layers/{layerId}/frames")
    public ResponseEntity<?> addFrame(@RequestParam String token,
                                      @PathVariable UUID drawingId,
                                      @PathVariable UUID layerId) {
        FrameDTO frame = layerService.addFrame(token, drawingId, layerId);
        if (frame == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Camada não encontrada.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(frame);
    }

    @Operation(summary = "Deletar frame de uma camada")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Frame deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Frame não encontrado.")
    })
    @DeleteMapping("/{drawingId}/layers/{layerId}/frames/{frameId}")
    public ResponseEntity<?> removeFrame(@RequestParam String token,
                                         @PathVariable UUID drawingId,
                                         @PathVariable UUID layerId,
                                         @PathVariable UUID frameId) {
        boolean deleted = layerService.removeFrame(token, drawingId, layerId, frameId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Frame não encontrado.");
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Salvar novo desenho",
               description = "Persiste um desenho em JSON no banco vinculado ao usuário autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Desenho salvo com sucesso."),
        @ApiResponse(responseCode = "400", description = "canvasData ausente ou vazio."),
        @ApiResponse(responseCode = "401", description = "Token inválido.")
    })
    @PostMapping
    public ResponseEntity<?> save(@RequestParam String token,
                                  @RequestBody SaveRequest request) {
        if (request.canvasData() == null || request.canvasData().isBlank()) {
            return ResponseEntity.badRequest().body("canvasData é obrigatório.");
        }
        DrawingDTO dto = drawingService.save(token, request.title(), request.canvasData());
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Listar desenhos do usuário",
               description = "Retorna todos os desenhos (sem canvasData) do usuário autenticado, mais recentes primeiro.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso."),
        @ApiResponse(responseCode = "401", description = "Token inválido.")
    })
    @GetMapping
    public ResponseEntity<?> list(@RequestParam String token) {
        List<DrawingDTO> list = drawingService.listByToken(token);
        if (list == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido.");
        }
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Abrir um desenho",
               description = "Retorna os dados completos (incluindo canvasData) de um desenho específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Desenho retornado com sucesso."),
        @ApiResponse(responseCode = "401", description = "Token inválido."),
        @ApiResponse(responseCode = "404", description = "Desenho não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestParam String token,
                                     @PathVariable UUID id) {
        DrawingDTO dto = drawingService.getById(token, id);
        if (dto == null) {
            // Pode ser token inválido ou desenho não encontrado; retornamos 404 para não expor existência
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Desenho não encontrado.");
        }
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Atualizar um desenho",
               description = "Atualiza título e/ou canvasData de um desenho existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Desenho atualizado com sucesso."),
        @ApiResponse(responseCode = "401", description = "Token inválido."),
        @ApiResponse(responseCode = "404", description = "Desenho não encontrado.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestParam String token,
                                    @PathVariable UUID id,
                                    @RequestBody UpdateRequest request) {
        DrawingDTO dto = drawingService.update(token, id, request.title(), request.canvasData());
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Desenho não encontrado.");
        }
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Deletar um desenho",
               description = "Remove permanentemente um desenho do usuário autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Desenho deletado com sucesso."),
        @ApiResponse(responseCode = "401", description = "Token inválido."),
        @ApiResponse(responseCode = "404", description = "Desenho não encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestParam String token,
                                    @PathVariable UUID id) {
        boolean deleted = drawingService.delete(token, id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Desenho não encontrado.");
        }
        return ResponseEntity.noContent().build();
    }
}
