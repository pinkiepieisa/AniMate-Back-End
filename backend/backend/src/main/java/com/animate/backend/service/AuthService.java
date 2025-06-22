package com.animate.backend.service;

import com.animate.backend.dto.RegisterDTO;
import com.animate.backend.model.Token;
import com.animate.backend.model.User;
import com.animate.backend.repository.TokenRepository;
import com.animate.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final Integer TOKEN_TTL = 6000; // segundos

    public void signup(RegisterDTO registerDTO) throws Exception {

        String email = registerDTO.getEmail();
        String password = registerDTO.getPassword();
        String rePassword = registerDTO.getRePassword();

        if (email == null || password == null) {
            throw new Exception("Dados de registro inválidos");
        }

        if (!password.equals(rePassword)) {
            throw new Exception("Senhas não conferem");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("Usuário já cadastrado");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    public Token signin(String email, String password) {
        Optional<User> userFound = userRepository.findByEmail(email);
        if (userFound.isPresent() && passwordEncoder.matches(password, userFound.get().getPassword())) {
            Token token = new Token();
            token.setUser(userFound.get());
            token.setToken(UUID.randomUUID().toString());
            token.setExpirationTime(Instant.now().plusSeconds(TOKEN_TTL).toEpochMilli());
            tokenRepository.save(token);
            return token;
        }
        return null;
    }

    public Boolean validate(String token) {
        Optional<Token> found = tokenRepository.findByToken(token);
        return found.isPresent() && found.get().getExpirationTime() > Instant.now().toEpochMilli();
    }

    public void signout(String token) {
        Optional<Token> found = tokenRepository.findByToken(token);
        found.ifPresent(t -> {
            t.setExpirationTime(Instant.now().toEpochMilli());
            tokenRepository.save(t);
        });
    }

    public User toUser(String token) {
        Optional<Token> found = tokenRepository.findByToken(token);
        return found.map(Token::getUser).orElse(null);
    }
}
