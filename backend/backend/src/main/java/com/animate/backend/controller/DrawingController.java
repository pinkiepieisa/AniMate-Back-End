package com.animate.backend.controller;

import com.animate.backend.dto.DrawingDTO;
import com.animate.backend.service.DrawingService;

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

    private final DrawingService drawingService;

    public DrawingController(DrawingService drawingService) {
        this.drawingService = drawingService;
    }

    // ── Request bodies ────────────────────────────────────────────────────────
    public record SaveRequest(String title, String canvasData) {}

    public record UpdateRequest(String title, String canvasData) {}

    // ── Endpoints ─────────────────────────────────────────────────────────────

    @Operation(summary = "Salvar novo desenho",
            description = "Persiste um desenho em JSON no banco vinculado ao usuário autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Desenho salvo com sucesso."),
            @ApiResponse(responseCode = "400", description = "canvasData ausente, vazio ou inválido."),
            @ApiResponse(responseCode = "401", description = "Token inválido.")
    })
    @PostMapping
    public ResponseEntity<?> save(@RequestParam String token,
                                  @RequestBody SaveRequest request) {
        if (request.canvasData() == null || request.canvasData().isBlank()) {
            return ResponseEntity.badRequest().body("canvasData é obrigatório.");
        }
        try {
            DrawingDTO dto = drawingService.save(token, request.title(), request.canvasData());
            if (dto == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido.");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("canvasData inválido: " + e.getMessage());
        }
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
            @ApiResponse(responseCode = "400", description = "canvasData inválido."),
            @ApiResponse(responseCode = "401", description = "Token inválido."),
            @ApiResponse(responseCode = "404", description = "Desenho não encontrado.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestParam String token,
                                    @PathVariable UUID id,
                                    @RequestBody UpdateRequest request) {
        try {
            DrawingDTO dto = drawingService.update(token, id, request.title(), request.canvasData());
            if (dto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Desenho não encontrado.");
            }
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("canvasData inválido: " + e.getMessage());
        }
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