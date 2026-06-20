package com.animate.backend.service;

import com.animate.backend.dto.DrawingDTO;
import com.animate.backend.model.Drawing;
import com.animate.backend.model.User;
import com.animate.backend.repository.DrawingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DrawingService {

    private static final Logger logger = LoggerFactory.getLogger(DrawingService.class);

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


    public DrawingDTO save(String token, String title, String canvasData) {
        User user = resolveUser(token);
        if (user == null) return null;
        Drawing drawing = new Drawing();
        drawing.setTitle(title != null ? title : "Sem título");
        drawing.setCanvasData(validateCanvasData(canvasData));
        drawing.setOwner(user);

        Drawing saved = drawingRepository.save(drawing);
        logger.info("Drawing saved: id={} owner={}", saved.getId(), user.getId());
        return new DrawingDTO(saved);
    }


    public List<DrawingDTO> listByToken(String token) {
        User user = resolveUser(token);
        if (user == null) return null;

        return drawingRepository.findByOwnerOrderByUpdatedAtDesc(user)
                .stream()
                .map(d -> new DrawingDTO(d, true))   // summary = sem canvasData
                .collect(Collectors.toList());
    }


    public DrawingDTO getById(String token, UUID drawingId) {
        User user = resolveUser(token);
        if (user == null) return null;

        Optional<Drawing> found = drawingRepository.findByIdAndOwner(drawingId, user);
        return found.map(DrawingDTO::new).orElse(null);
    }


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
            drawing.setCanvasData(validateCanvasData(canvasData));
        }

        Drawing updated = drawingRepository.save(drawing);
        logger.info("Drawing updated: id={} owner={}", updated.getId(), user.getId());
        return new DrawingDTO(updated);
    }


    public boolean delete(String token, UUID drawingId) {
        User user = resolveUser(token);
        if (user == null) return false;

        Optional<Drawing> found = drawingRepository.findByIdAndOwner(drawingId, user);
        if (found.isEmpty()) return false;

        drawingRepository.delete(found.get());
        logger.info("Drawing deleted: id={} owner={}", drawingId, user.getId());
        return true;
    }


    private String validateCanvasData(String canvasData) {
        if (canvasData == null || canvasData.isBlank()) {
            logger.warn("canvasData vazio recebido; salvando objeto vazio");
            return "{}";
        }

        try {
            objectMapper.readTree(canvasData);
            return canvasData;
        } catch (Exception e) {
            logger.warn("canvasData não é um JSON válido; recusando atualização", e);
            throw new IllegalArgumentException("canvasData não é um JSON válido.", e);
        }
    }
}