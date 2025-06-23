package com.animate.backend.controller;

import com.animate.backend.dto.UserProfileDTO;
import com.animate.backend.model.User;
import com.animate.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    public record BioUpdateRequest(String bio) {}

    @PutMapping("/bio")
    public ResponseEntity<?> updateBio(@RequestParam String token, @RequestBody BioUpdateRequest request) {
        User user = userService.updateBioByToken(token, request.bio());
        if (user == null) {
            return ResponseEntity.status(401).body("Token inválido");
        }
        return ResponseEntity.ok(new UserProfileDTO(user));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@RequestParam String token) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Token inválido");
        }
        return ResponseEntity.ok(new UserProfileDTO(user));
    }
}
