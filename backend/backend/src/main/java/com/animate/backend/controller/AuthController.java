package com.animate.backend.controller;

import com.animate.backend.dto.RegisterDTO;
import com.animate.backend.model.Token;
import com.animate.backend.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody RegisterDTO registerDTO) {
        try {
            authService.signup(registerDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Cadastrado com sucesso!");
        } catch (Exception e) {
            logger.error("Erro ao registrar usuário: {}", registerDTO.getUsername(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: " + e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, String> user) {
        String email = user.get("email");
        String password = user.get("password");

        Token token = authService.signin(email, password);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha incorretos");
        }

        //Retornar somente o token como String
        return ResponseEntity.ok(Map.of(
                "token", token.getToken(),
                "expirationTime", token.getExpirationTime(),
                "userId", token.getUser().getId(),
                "username", token.getUser().getUsername(),
                "email", token.getUser().getEmail()
        ));
    }

    @PostMapping("/check")
    public ResponseEntity<?> check(@RequestHeader String token) {
        boolean isValid = authService.validate(token);
        return isValid
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signout(@RequestHeader String token) {
        authService.signout(token);
        return ResponseEntity.ok().build();
    }
}
