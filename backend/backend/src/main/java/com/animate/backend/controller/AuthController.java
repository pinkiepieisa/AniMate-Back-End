package com.animate.backend.controller;

import com.animate.backend.dto.LoginDTO;
import com.animate.backend.dto.RegisterDTO;
import com.animate.backend.model.Token;
import com.animate.backend.model.User;
import com.animate.backend.service.AuthService;
import com.animate.backend.service.PasswordService;
import com.animate.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordResetService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public record ForgotPasswordRequest(String email) {}
    public record ResetPasswordRequest(String token, String newPassword) {}

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
public ResponseEntity<?> signin(@RequestBody LoginDTO request) {

    // Pegando dados enviados pelo frontend
    String email = request.getEmail();
    String password = request.getPassword();

    // Campo novo que criamos no LoginDTO
    // Ele indica se o usuário quer permanecer logado
    boolean rememberMe = request.isRememberMe();

    // Chamando o service passando o rememberMe
    // O AuthService decidirá quanto tempo o token dura
    Token token = authService.signin(email, password, rememberMe);

    // Se login falhar
    if (token == null) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Email ou senha incorretos");
    }

    // Resposta enviada ao frontend
    Map<String, Object> response = new HashMap<>();
    response.put("token", token.getToken());
    response.put("expirationTime", token.getExpirationTime());
    response.put("userId", token.getUser().getId());
    response.put("username", token.getUser().getUsername());
    response.put("email", token.getUser().getEmail());
    return ResponseEntity.ok(response);
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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        if (request == null || request.email() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email é obrigatório"));
        }
        User user = userService.getUserByEmail(request.email());
        if (user == null) {
            // Do not reveal whether user exists — respond success to avoid user enumeration
            return ResponseEntity.ok(Map.of("status", "Se o e-mail existir, um link de recuperação será enviado."));
        }
        passwordResetService.createPasswordResetToken(user);
        return ResponseEntity.ok(Map.of("status", "Se o e-mail existir, um link de recuperação será enviado."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (request == null || request.token() == null || request.newPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Token e nova senha são obrigatórios"));
        }
        boolean ok = passwordResetService.resetPassword(request.token(), request.newPassword());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Token inválido ou expirado"));
        }
        return ResponseEntity.ok(Map.of("status", "Senha atualizada com sucesso"));
    }
}
