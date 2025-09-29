package com.animate.backend.service;

import com.animate.backend.model.User;
import com.animate.backend.model.Token;
import com.animate.backend.repository.TokenRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    public String generateToken(User user) {
        // Implementação real deve gerar token JWT
        return "token-de-exemplo";
    }

    public boolean isValid(String token) {
        // Implementar validação real do token
        return true;
    }

    public void invalidateToken(String token) {
        // Implementar blacklist se necessário
    }

public String getUserIdFromToken(String token) {
    Optional<Token> tokenObj = tokenRepository.findByToken(token);
    if (tokenObj.isEmpty() || tokenObj.get().getUser() == null) return null;
    return String.valueOf(tokenObj.get().getUser().getId());
}
}