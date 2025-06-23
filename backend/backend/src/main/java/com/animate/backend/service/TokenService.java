package com.animate.backend.service;

import com.animate.backend.model.User;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

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
        // Extrair id do usuário do token (exemplo fixo)
        return "1";
    }
}
