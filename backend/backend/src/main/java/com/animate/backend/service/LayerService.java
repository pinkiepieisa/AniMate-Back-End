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

    public LayerDTO addLayer(String token, UUID drawingId, String layerName) {
        var found = getDrawingIfOwned(token, drawingId);
        if (found.isEmpty()) return null;

        Drawing drawing = found.get();
        try {
            ObjectNode canvasData = (ObjectNode) objectMapper.readTree(drawing.getCanvasData());

            // Inicializa array de layers se não existir
            if (!canvasData.has("layers")) {
                canvasData.putArray("layers");
            }

            UUID layerId = UUID.randomUUID();
            ObjectNode newLayer = objectMapper.createObjectNode();
            newLayer.put("id", layerId.toString());
            newLayer.put("name", layerName != null ? layerName : "Layer");
            newLayer.put("opacity", 1.0);
            newLayer.put("visible", true);
            newLayer.putArray("frames");

            ((ArrayNode) canvasData.get("layers")).add(newLayer);
            drawing.setCanvasData(objectMapper.writeValueAsString(canvasData));

            drawingRepository.save(drawing);
            logger.info("Layer added: layerId={} drawingId={}", layerId, drawingId);

            return new LayerDTO(layerId, layerName, 1.0, true, new ArrayList<>());

        } catch (Exception e) {
            logger.error("Erro ao adicionar camada", e);
            return null;
        }
    }

    // ── Remover Camada ────────────────────────────────────────────────────────

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
                if (layers.get(i).get("id").asText().equals(layerId.toString())) {
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

    // ── Adicionar Frame ───────────────────────────────────────────────────────

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
                    ArrayNode frames = (ArrayNode) layer.get("frames");
                    int nextIndex = frames.size();

                    UUID frameId = UUID.randomUUID();
                    ObjectNode newFrame = objectMapper.createObjectNode();
                    newFrame.put("id", frameId.toString());
                    newFrame.put("index", nextIndex);
                    newFrame.put("duration", 100); // padrão: 100ms
                    newFrame.put("strokeData", "{}");

                    frames.add(newFrame);
                    drawing.setCanvasData(objectMapper.writeValueAsString(canvasData));
                    drawingRepository.save(drawing);
                    logger.info("Frame added: frameId={} layerId={}", frameId, layerId);

                    return new FrameDTO(frameId, nextIndex, 100, "{}");
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

    private LayerDTO parseLayer(ObjectNode layer) {
        UUID id = UUID.fromString(layer.get("id").asText());
        String name = layer.get("name").asText();
        Double opacity = layer.get("opacity").asDouble();
        Boolean visible = layer.get("visible").asBoolean();

        List<FrameDTO> frames = new ArrayList<>();
        if (layer.has("frames")) {
            for (JsonNode frame : layer.get("frames")) {
                frames.add(parseFrame((ObjectNode) frame));
            }
        }

        return new LayerDTO(id, name, opacity, visible, frames);
    }

    private FrameDTO parseFrame(ObjectNode frame) {
        UUID id = UUID.fromString(frame.get("id").asText());
        Integer index = frame.get("index").asInt();
        Integer duration = frame.get("duration").asInt();
        String strokeData = frame.get("strokeData").asText();

        return new FrameDTO(id, index, duration, strokeData);
    }
}
