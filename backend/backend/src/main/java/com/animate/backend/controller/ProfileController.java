package com.animate.backend.controller;

import com.animate.backend.dto.UserProfileDTO;
import com.animate.backend.dto.PictureDTO;
import com.animate.backend.model.User;
import com.animate.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.animate.backend.model.ProfilePic;

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500" }, methods = { RequestMethod.PUT,
        RequestMethod.POST, RequestMethod.GET })
@Tag(name = "Perfil", description = "Endpoints para visualização e atualização do perfil do usuário.")

public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    public record BioUpdateRequest(String bio) {}

    public record PictureUpdateRequest(String imageUrl, String imageUuid) {}

    public record UsernameUpdateRequest(String username) {}

    @Operation(summary = "Atualizar bio", description = "Atualiza a biografia do usuário autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bio atualizada com sucesso!"),
        @ApiResponse(responseCode = "401", description = "Token inválido.")
    })

    @PutMapping("/bio")
    public ResponseEntity<?> updateBio(@RequestHeader(value = "Authorization", required = false) String authHeader, @RequestBody BioUpdateRequest request) {
        String token = extractTokenFromHeader(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido");
        }
        User user = userService.updateBioByToken(token, request.bio());
        if (user == null) {
            return ResponseEntity.status(401).body("Token inválido");
        }
        return ResponseEntity.ok("Bio atualizada com sucesso.");
    }

    @Operation(summary = "Atualizar username", description = "Atualiza o nome de usuário do perfil autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Username atualizado com sucesso!"),
        @ApiResponse(responseCode = "400", description = "Username vazio ou inválido."),
        @ApiResponse(responseCode = "401", description = "Token inválido ou usuário não encontrado.")
    })

    @PutMapping("/username")
    public ResponseEntity<?> updateUsername(@RequestHeader(value = "Authorization", required = false) String authHeader, @RequestBody UsernameUpdateRequest request) {

        String token = extractTokenFromHeader(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido");
        }

        if (request.username() == null || request.username().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O nome de usuário não pode estar vazio.");
        }

        User user = userService.updateUsernameByToken(token, request.username());

        if (user == null) {
            return ResponseEntity.status(401).body("Token inválido ou falha ao encontrar usuário.");
        }

        return ResponseEntity.ok("Username atualizado com sucesso.");
    }

    @Operation(summary = "Buscar perfil", description = "Retorna os dados do perfil do usuário autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso!"),
        @ApiResponse(responseCode = "401", description = "Token inválido.")
    })

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido");
        }
        User user = userService.getUserByToken(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Token inválido");
        }
        UserProfileDTO userProfile = userService.getUserProfileByToken(token);
        return ResponseEntity.ok(userProfile);
    }

    @Operation(summary = "Atualizar foto de perfil", description = "Insere ou substitui a foto de perfil do usuário autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Foto de perfil atualizada com sucesso!"),
        @ApiResponse(responseCode = "401", description = "Token inválido.")
    })

    @PutMapping("/picture")
    public ResponseEntity<?> upsertProfilePicture(@RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody PictureUpdateRequest request) {
        String token = extractTokenFromHeader(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido");
        }
        User user = userService.getUserByToken(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Token inválido");
        }
        ProfilePic savedPicture = userService.upsertProfilePicture(token, request.imageUrl(), request.imageUuid());

        if (savedPicture == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token de usuário inválido");
        }

        // Se deu tudo certo, retorne um DTO com os dados da foto salva
        return ResponseEntity.ok(new PictureDTO(savedPicture));
    }

    /**
     * Extrai o token do header Authorization.
     * Formato esperado: Bearer <token>
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
