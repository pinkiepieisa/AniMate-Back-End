package com.animate.backend.service;

import com.animate.backend.dto.LayerDTO;
import com.animate.backend.dto.FrameDTO;
import com.animate.backend.model.Drawing;
import com.animate.backend.model.User;
import com.animate.backend.repository.DrawingRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class LayerService {

    private static final Logger logger = LoggerFactory.getLogger(LayerService.class);

    private final DrawingRepository drawingRepository;
    private final UserService userService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper
            = new com.fasterxml.jackson.databind.ObjectMapper();

    public LayerService(DrawingRepository drawingRepository, UserService userService) {
        this.drawingRepository = drawingRepository;
        this.userService = userService;
    }

    // ── Adicionar Camada ──────────────────────────────────────────────────────
    // Camadas criadas pelo usuário NUNCA são locked (só a camada "Fundo",
    // criada automaticamente — ver DrawingService.createDefaultLayers).

    public LayerDTO addLayer(String token, UUID drawingId, String layerName) {
        var found = getDrawingIfOwned(token, drawingId);
        if (found.isEmpty()) return null;

        Drawing drawing = found.get();
        try {
            ObjectNode canvasData = (ObjectNode) objectMapper.readTree(drawing.getCanvasData());

            if (!canvasData.has("layers")) {
                canvasData.putArray("layers");
            }

            UUID layerId = UUID.randomUUID();
            ObjectNode newLayer = objectMapper.createObjectNode();
            newLayer.put("id", layerId.toString());
            newLayer.put("name", layerName != null ? layerName : "Layer");
            newLayer.put("opacity", 1.0);
            newLayer.put("visible", true);
            newLayer.put("locked", false); // NOVO: camadas novas nunca são travadas
            newLayer.putArray("frames");

            ((ArrayNode) canvasData.get("layers")).add(newLayer);
            drawing.setCanvasData(objectMapper.writeValueAsString(canvasData));

            drawingRepository.save(drawing);
            logger.info("Layer added: layerId={} drawingId={}", layerId, drawingId);

            return new LayerDTO(layerId, layerName, 1.0, true, false, new ArrayList<>());

        } catch (Exception e) {
            logger.error("Erro ao adicionar camada", e);
            return null;
        }
    }

    // ── Remover Camada ────────────────────────────────────────────────────────
    // Camadas locked (Fundo) não podem ser removidas, mesmo via API direta.

    public boolean removeLayer(String token, UUID drawingId, UUID layerId) {
        var found = getDrawingIfOwned(token, drawingId);
        if (found.isEmpty()) return false;

        Drawing drawing = found.get();
        try {
            ObjectNode canvasData = (ObjectNode) objectMapper.readTree(drawing.getCanvasData());

            if (!canvasData.has("layers")) return false;

            ArrayNode layers = (ArrayNode) canvasData.get("layers");
            boolean removed = false;

            for (int i = 0; i < layers.size(); i++) {
                JsonNode layer = layers.get(i);
                if (layer.get("id").asText().equals(layerId.toString())) {
                    if (isLocked(layer)) {
                        logger.warn("Tentativa de remover camada locked: layerId={} drawingId={}", layerId, drawingId);
                        return false; // camada de Fundo é protegida
                    }
                    layers.remove(i);
                    removed = true;
                    break;
                }
            }

            if (removed) {
                drawing.setCanvasData(objectMapper.writeValueAsString(canvasData));
                drawingRepository.save(drawing);
                logger.info("Layer removed: layerId={} drawingId={}", layerId, drawingId);
            }

            return removed;

        } catch (Exception e) {
            logger.error("Erro ao remover camada", e);
            return false;
        }
    }

    // ── Editar Nome da Camada ─────────────────────────────────────────────────
    // Bloqueado para camadas locked (Fundo).

    public LayerDTO updateLayerName(String token, UUID drawingId, UUID layerId, String newName) {
        var found = getDrawingIfOwned(token, drawingId);
        if (found.isEmpty()) return null;

        Drawing drawing = found.get();
        try {
            ObjectNode canvasData = (ObjectNode) objectMapper.readTree(drawing.getCanvasData());

            if (!canvasData.has("layers")) return null;

            ArrayNode layers = (ArrayNode) canvasData.get("layers");
            for (JsonNode layer : layers) {
                if (layer.get("id").asText().equals(layerId.toString())) {
                    if (isLocked(layer)) {
                        logger.warn("Tentativa de renomear camada locked: layerId={} drawingId={}", layerId, drawingId);
                        return null;
                    }
                    ((ObjectNode) layer).put("name", newName);
                    drawing.setCanvasData(objectMapper.writeValueAsString(canvasData));
                    drawingRepository.save(drawing);
                    logger.info("Layer name updated: layerId={} newName={}", layerId, newName);
                    return parseLayer((ObjectNode) layer);
                }
            }

            return null;

        } catch (Exception e) {
            logger.error("Erro ao atualizar nome da camada", e);
            return null;
        }
    }

    // ── Editar Opacidade da Camada ────────────────────────────────────────────
    // Bloqueado para camadas locked (Fundo).

    public LayerDTO updateLayerOpacity(String token, UUID drawingId, UUID layerId, Double opacity) {
        if (opacity == null || opacity < 0.0 || opacity > 1.0) {
            logger.warn("Opacidade inválida: {}", opacity);
            return null;
        }

        var found = getDrawingIfOwned(token, drawingId);
        if (found.isEmpty()) return null;

        Drawing drawing = found.get();
        try {
            ObjectNode canvasData = (ObjectNode) objectMapper.readTree(drawing.getCanvasData());

            if (!canvasData.has("layers")) return null;

            ArrayNode layers = (ArrayNode) canvasData.get("layers");
            for (JsonNode layer : layers) {
                if (layer.get("id").asText().equals(layerId.toString())) {
                    if (isLocked(layer)) {
                        logger.warn("Tentativa de alterar opacidade de camada locked: layerId={} drawingId={}", layerId, drawingId);
                        return null;
                    }
                    ((ObjectNode) layer).put("opacity", opacity);
                    drawing.setCanvasData(objectMapper.writeValueAsString(canvasData));
                    drawingRepository.save(drawing);
                    logger.info("Layer opacity updated: layerId={} opacity={}", layerId, opacity);
                    return parseLayer((ObjectNode) layer);
                }
            }

            return null;

        } catch (Exception e) {
            logger.error("Erro ao atualizar opacidade", e);
            return null;
        }
    }

    // ── Editar Visibilidade da Camada (NOVO) ──────────────────────────────────
    // Diferente de nome/opacidade, a camada de Fundo PODE ter a visibilidade
    // alterada (faz sentido poder esconder o fundo temporariamente).

    public LayerDTO updateLayerVisibility(String token, UUID drawingId, UUID layerId, Boolean visible) {
        if (visible == null) return null;

        var found = getDrawingIfOwned(token, drawingId);
        if (found.isEmpty()) return null;

        Drawing drawing = found.get();
        try {
            ObjectNode canvasData = (ObjectNode) objectMapper.readTree(drawing.getCanvasData());

            if (!canvasData.has("layers")) return null;

            ArrayNode layers = (ArrayNode) canvasData.get("layers");
            for (JsonNode layer : layers) {
                if (layer.get("id").asText().equals(layerId.toString())) {
                    ((ObjectNode) layer).put("visible", visible);
                    drawing.setCanvasData(objectMapper.writeValueAsString(canvasData));
                    drawingRepository.save(drawing);
                    logger.info("Layer visibility updated: layerId={} visible={}", layerId, visible);
                    return parseLayer((ObjectNode) layer);
                }
            }

            return null;

        } catch (Exception e) {
            logger.error("Erro ao atualizar visibilidade", e);
            return null;
        }
    }

    // ── Duplicar Camada (NOVO) ────────────────────────────────────────────────
    // Copia nome (ou nome desejado), opacidade, visibilidade e todos os frames
    // (incluindo strokeData). A camada duplicada nunca é locked, e duplicar a
    // camada de Fundo é bloqueado.

    public LayerDTO duplicateLayer(String token, UUID drawingId, UUID layerId, String desiredName) {
        var found = getDrawingIfOwned(token, drawingId);
        if (found.isEmpty()) return null;

        Drawing drawing = found.get();
        try {
            ObjectNode canvasData = (ObjectNode) objectMapper.readTree(drawing.getCanvasData());

            if (!canvasData.has("layers")) return null;

            ArrayNode layers = (ArrayNode) canvasData.get("layers");
            for (JsonNode layerNode : layers) {
                if (layerNode.get("id").asText().equals(layerId.toString())) {
                    if (isLocked(layerNode)) {
                        logger.warn("Tentativa de duplicar camada locked: layerId={} drawingId={}", layerId, drawingId);
                        return null; // camada de Fundo não pode ser duplicada
                    }

                    ObjectNode original = (ObjectNode) layerNode;
                    UUID newLayerId = UUID.randomUUID();

                    String baseName = original.get("name").asText();
                    String finalName = (desiredName != null && !desiredName.isBlank())
                            ? desiredName
                            : baseName + " (cópia)";

                    ObjectNode copia = objectMapper.createObjectNode();
                    copia.put("id", newLayerId.toString());
                    copia.put("name", finalName);
                    copia.put("opacity", original.get("opacity").asDouble());
                    copia.put("visible", original.get("visible").asBoolean());
                    copia.put("locked", false);

                    ArrayNode framesCopia = copia.putArray("frames");
                    if (original.has("frames")) {
                        for (JsonNode frame : original.get("frames")) {
                            ObjectNode frameCopia = objectMapper.createObjectNode();
                            frameCopia.put("id", UUID.randomUUID().toString());
                            frameCopia.put("index", frame.get("index").asInt());
                            if (frame.has("duration") && !frame.get("duration").isNull()) {
                                frameCopia.put("duration", frame.get("duration").asInt());
                            } else {
                                frameCopia.putNull("duration");
                            }
                            String strokeData = frame.has("strokeData") ? frame.get("strokeData").asText() : "";
                            frameCopia.put("strokeData", strokeData);
                            framesCopia.add(frameCopia);
                        }
                    }

                    layers.add(copia);
                    drawing.setCanvasData(objectMapper.writeValueAsString(canvasData));
                    drawingRepository.save(drawing);
                    logger.info("Layer duplicated: originalId={} newId={} drawingId={}", layerId, newLayerId, drawingId);

                    return parseLayer(copia);
                }
            }

            return null;

        } catch (Exception e) {
            logger.error("Erro ao duplicar camada", e);
            return null;
        }
    }

    // ── Adicionar Frame ───────────────────────────────────────────────────────
    // Bloqueado para camadas locked (Fundo não recebe frames desenhados).

    public FrameDTO addFrame(String token, UUID drawingId, UUID layerId) {
        var found = getDrawingIfOwned(token, drawingId);
        if (found.isEmpty()) return null;

        Drawing drawing = found.get();
        try {
            ObjectNode canvasData = (ObjectNode) objectMapper.readTree(drawing.getCanvasData());

            if (!canvasData.has("layers")) return null;

            ArrayNode layers = (ArrayNode) canvasData.get("layers");
            for (JsonNode layer : layers) {
                if (layer.get("id").asText().equals(layerId.toString())) {
                    if (isLocked(layer)) {
                        logger.warn("Tentativa de adicionar frame em camada locked: layerId={} drawingId={}", layerId, drawingId);
                        return null;
                    }

                    ArrayNode frames = (ArrayNode) layer.get("frames");
                    int nextIndex = frames.size();

                    UUID frameId = UUID.randomUUID();
                    ObjectNode newFrame = objectMapper.createObjectNode();
                    newFrame.put("id", frameId.toString());
                    newFrame.put("index", nextIndex);
                    newFrame.putNull("duration"); // null = herda o fps global do Drawing
                    newFrame.put("strokeData", "");

                    frames.add(newFrame);
                    drawing.setCanvasData(objectMapper.writeValueAsString(canvasData));
                    drawingRepository.save(drawing);
                    logger.info("Frame added: frameId={} layerId={}", frameId, layerId);

                    return new FrameDTO(frameId, nextIndex, null, "");
                }
            }

            return null;

        } catch (Exception e) {
            logger.error("Erro ao adicionar frame", e);
            return null;
        }
    }

    // ── Remover Frame ─────────────────────────────────────────────────────────

    public boolean removeFrame(String token, UUID drawingId, UUID layerId, UUID frameId) {
        var found = getDrawingIfOwned(token, drawingId);
        if (found.isEmpty()) return false;

        Drawing drawing = found.get();
        try {
            ObjectNode canvasData = (ObjectNode) objectMapper.readTree(drawing.getCanvasData());

            if (!canvasData.has("layers")) return false;

            ArrayNode layers = (ArrayNode) canvasData.get("layers");
            for (JsonNode layer : layers) {
                if (layer.get("id").asText().equals(layerId.toString())) {
                    ArrayNode frames = (ArrayNode) layer.get("frames");
                    boolean removed = false;

                    for (int i = 0; i < frames.size(); i++) {
                        if (frames.get(i).get("id").asText().equals(frameId.toString())) {
                            frames.remove(i);
                            removed = true;

                            // Reindexar frames
                            for (int j = i; j < frames.size(); j++) {
                                ((ObjectNode) frames.get(j)).put("index", j);
                            }
                            break;
                        }
                    }

                    if (removed) {
                        drawing.setCanvasData(objectMapper.writeValueAsString(canvasData));
                        drawingRepository.save(drawing);
                        logger.info("Frame removed: frameId={} layerId={}", frameId, layerId);
                    }

                    return removed;
                }
            }

            return false;

        } catch (Exception e) {
            logger.error("Erro ao remover frame", e);
            return false;
        }
    }

    // ── Listar Camadas ────────────────────────────────────────────────────────

    public List<LayerDTO> getLayers(String token, UUID drawingId) {
        var found = getDrawingIfOwned(token, drawingId);
        if (found.isEmpty()) return null;

        Drawing drawing = found.get();
        try {
            ObjectNode canvasData = (ObjectNode) objectMapper.readTree(drawing.getCanvasData());

            if (!canvasData.has("layers")) {
                return new ArrayList<>();
            }

            List<LayerDTO> layers = new ArrayList<>();
            for (JsonNode layer : canvasData.get("layers")) {
                layers.add(parseLayer((ObjectNode) layer));
            }

            return layers;

        } catch (Exception e) {
            logger.error("Erro ao listar camadas", e);
            return null;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Optional<Drawing> getDrawingIfOwned(String token, UUID drawingId) {
        User user = userService.getUserByToken(token);
        if (user == null) return Optional.empty();
        return drawingRepository.findByIdAndOwner(drawingId, user);
    }

    // Camadas antigas (salvas antes desta mudança) podem não ter o campo "locked".
    // Trata ausência como false, para não travar desenhos já existentes.
    private boolean isLocked(JsonNode layer) {
        return layer.has("locked") && layer.get("locked").asBoolean(false);
    }

    private LayerDTO parseLayer(ObjectNode layer) {
        UUID id = UUID.fromString(layer.get("id").asText());
        String name = layer.get("name").asText();
        Double opacity = layer.get("opacity").asDouble();
        Boolean visible = layer.get("visible").asBoolean();
        Boolean locked = layer.has("locked") && layer.get("locked").asBoolean(false);

        List<FrameDTO> frames = new ArrayList<>();
        if (layer.has("frames")) {
            for (JsonNode frame : layer.get("frames")) {
                frames.add(parseFrame((ObjectNode) frame));
            }
        }

        return new LayerDTO(id, name, opacity, visible, locked, frames);
    }

    private FrameDTO parseFrame(ObjectNode frame) {
        UUID id = UUID.fromString(frame.get("id").asText());
        Integer index = frame.get("index").asInt();
        Integer duration = (frame.has("duration") && !frame.get("duration").isNull())
                ? frame.get("duration").asInt()
                : null;

        // Compatibilidade retroativa: frames antigos podem ter sido salvos com "imageData"
        String strokeData;
        if (frame.has("strokeData")) {
            strokeData = frame.get("strokeData").asText();
        } else if (frame.has("imageData")) {
            strokeData = frame.get("imageData").asText();
        } else {
            strokeData = "";
        }

        return new FrameDTO(id, index, duration, strokeData);
    }
}