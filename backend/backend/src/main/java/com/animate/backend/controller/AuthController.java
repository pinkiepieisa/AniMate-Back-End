package com.animate.backend.controller;

import com.animate.backend.dto.RegisterDTO;
import com.animate.backend.model.AnonToken;
import com.animate.backend.model.Token;
import com.animate.backend.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito");
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, String> user) {

        Token token = authService.signin(user.get("email"), user.get("password"));
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(token);
    }

    @PostMapping("/check")
    public ResponseEntity<?> check(@RequestHeader String token){
        Boolean isValid = authService.validate(token);
        return (isValid) ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signout(@RequestHeader String token){

        authService.signout(token);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @PostMapping("/setAnon")
    public ResponseEntity<?> setAnon(@RequestBody Map<String, String> anon) {

        Integer id = Integer.parseInt(anon.get("id"));  // Conversão
        String username = anon.get("username");

        AnonToken token = authService.setAnon(id, username);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(token);
    }



}
