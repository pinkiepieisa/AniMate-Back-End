package com.animate.backend.controller;

import com.animate.backend.dto.UserProfileDTO;
import com.animate.backend.dto.PictureDTO;
import com.animate.backend.model.User;
import com.animate.backend.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.animate.backend.model.ProfilePic;

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}, methods = {RequestMethod.PUT, RequestMethod.POST, RequestMethod.GET})
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    public record BioUpdateRequest(String bio) {}
    public record PictureUpdateRequest(String imageUrl, String imageUuid) {}
    public record UsernameUpdateRequest(String username) {}
    

    @PutMapping("/bio")
    public ResponseEntity<?> updateBio(@RequestParam String token, @RequestBody BioUpdateRequest request) {
        User user = userService.updateBioByToken(token, request.bio());
        if (user == null) {
            return ResponseEntity.status(401).body("Token inválido");
        }
        return ResponseEntity.ok("Bio atualizada com sucesso.");
    }

    @PutMapping("/username") 
    public ResponseEntity<?> updateUsername(@RequestParam String token, @RequestBody UsernameUpdateRequest request) {
    
        if (request.username() == null || request.username().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O nome de usuário não pode estar vazio.");
        }
        
        User user = userService.updateUsernameByToken(token, request.username());
        
        if (user == null) {
            return ResponseEntity.status(401).body("Token inválido ou falha ao encontrar usuário.");
        }
        
        return ResponseEntity.ok("Username atualizado com sucesso.");
    }    

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@RequestParam String token) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Token inválido");
        }
        UserProfileDTO userProfile = userService.getUserProfileByToken(token);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/picture")
    public ResponseEntity<?> upsertProfilePicture(@RequestParam String token, @RequestBody PictureUpdateRequest request) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Token inválido");
        }
    ProfilePic savedPicture = userService.upsertProfilePicture(token, request.imageUrl(), request.imageUuid());

        if (savedPicture == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token de usuário inválido");
        }
    
    // 4. Se deu tudo certo, retorne um DTO com os dados da foto salva
        return ResponseEntity.ok(new PictureDTO(savedPicture));
        
    }
    
}
