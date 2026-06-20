package com.animate.backend.service;

import com.animate.backend.dto.DrawingDTO;
import com.animate.backend.model.Drawing;
import com.animate.backend.model.User;
import com.animate.backend.repository.DrawingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DrawingService {

    private static final Logger logger = LoggerFactory.getLogger(DrawingService.class);
    private static final int DEFAULT_FPS = 12;

    private final DrawingRepository drawingRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DrawingService(DrawingRepository drawingRepository, UserService userService) {
        this.drawingRepository = drawingRepository;
        this.userService = userService;
    }

    // Resolve o usuário a partir do token; retorna null se inválido
    private User resolveUser(String token) {
        return userService.getUserByToken(token);
    }

    /**
     * Salva um novo desenho para o usuário autenticado.
     * @return DrawingDTO com o desenho criado, ou null se token inválido
     */
    public DrawingDTO save(String token, String title, String canvasData) {
        User user = resolveUser(token);
        if (user == null) return null;
        Drawing drawing = new Drawing();
        drawing.setTitle(title != null ? title : "Sem título");
        drawing.setCanvasData(sanitizeCanvasData(canvasData));
        drawing.setOwner(user);

        Drawing saved = drawingRepository.save(drawing);
        logger.info("Drawing saved: id={} owner={}", saved.getId(), user.getId());
        return new DrawingDTO(saved);
    }

    /**
     * Lista todos os desenhos do usuário (sem canvasData para economizar payload).
     */
    public List<DrawingDTO> listByToken(String token) {
        User user = resolveUser(token);
        if (user == null) return null;

        return drawingRepository.findByOwnerOrderByUpdatedAtDesc(user)
                .stream()
                .map(d -> new DrawingDTO(d, true))   // summary = sem canvasData
                .collect(Collectors.toList());
    }

    /**
     * Busca um desenho completo pelo id (inclui canvasData).
     * Só retorna se o desenho pertencer ao usuário do token.
     */
    public DrawingDTO getById(String token, UUID drawingId) {
        User user = resolveUser(token);
        if (user == null) return null;

        Optional<Drawing> found = drawingRepository.findByIdAndOwner(drawingId, user);
        return found.map(DrawingDTO::new).orElse(null);
    }

    /**
     * Atualiza título e/ou canvasData de um desenho existente.
     */
    public DrawingDTO update(String token, UUID drawingId, String title, String canvasData) {
        User user = resolveUser(token);
        if (user == null) return null;

        Optional<Drawing> found = drawingRepository.findByIdAndOwner(drawingId, user);
        if (found.isEmpty()) return null;

        Drawing drawing = found.get();
        if (title != null && !title.isBlank()) {
            drawing.setTitle(title);
        }
        if (canvasData != null && !canvasData.isBlank()) {
            drawing.setCanvasData(sanitizeCanvasData(canvasData));
        }

        Drawing updated = drawingRepository.save(drawing);
        logger.info("Drawing updated: id={} owner={}", updated.getId(), user.getId());
        return new DrawingDTO(updated);
    }

    /**
     * Remove um desenho. Só deleta se pertencer ao usuário do token.
     * @return true se deletado, false se não encontrado ou token inválido
     */
    public boolean delete(String token, UUID drawingId) {
        User user = resolveUser(token);
        if (user == null) return false;

        Optional<Drawing> found = drawingRepository.findByIdAndOwner(drawingId, user);
        if (found.isEmpty()) return false;

        drawingRepository.delete(found.get());
        logger.info("Drawing deleted: id={} owner={}", drawingId, user.getId());
        return true;
    }

    // Garante que canvasData nunca seja nulo e que seja JSON válido; em caso de inválido, usa objeto vazio
    private String sanitizeCanvasData(String canvasData) {
        ObjectNode root;

        if (canvasData == null || canvasData.isBlank()) {
            root = objectMapper.createObjectNode();
        } else {
            try {
                root = (ObjectNode) objectMapper.readTree(canvasData);
            } catch (Exception e) {
                logger.warn("canvasData inválido, substituindo por objeto vazio", e);
                root = objectMapper.createObjectNode();
            }
        }

        ensureDefaultLayers(root);
        ensureFps(root);

        try {
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            logger.error("Erro ao serializar canvasData sanitizado", e);
            return "{}";
        }
    }
    private void ensureDefaultLayers(ObjectNode root) {
        boolean precisaCriarPadrao = !root.has("layers")
                || !root.get("layers").isArray()
                || root.get("layers").isEmpty();

        if (!precisaCriarPadrao) return;

        ArrayNode layers = root.putArray("layers");

        ObjectNode fundo = objectMapper.createObjectNode();
        fundo.put("id", UUID.randomUUID().toString());
        fundo.put("name", "Fundo");
        fundo.put("opacity", 1.0);
        fundo.put("visible", true);
        fundo.put("locked", true);
        fundo.putArray("frames");
        layers.add(fundo);

        ObjectNode layer1 = objectMapper.createObjectNode();
        layer1.put("id", UUID.randomUUID().toString());
        layer1.put("name", "Layer 1");
        layer1.put("opacity", 1.0);
        layer1.put("visible", true);
        layer1.put("locked", false);
        layer1.putArray("frames");
        layers.add(layer1);

        logger.info("Camadas padrão criadas (Fundo + Layer 1) para desenho novo/sem layers");
    }

    // Garante que o desenho tem um fps definido (usado pela timeline global).
    private void ensureFps(ObjectNode root) {
        if (!root.has("fps") || root.get("fps").isNull()) {
            root.put("fps", DEFAULT_FPS);
        }
    }
}
