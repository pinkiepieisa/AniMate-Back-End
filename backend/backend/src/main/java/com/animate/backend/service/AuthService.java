package com.animate.backend.service;

import com.animate.backend.dto.RegisterDTO;
import com.animate.backend.model.AnonToken;
import com.animate.backend.model.Token;
import com.animate.backend.model.User;
import com.animate.backend.repository.ATokenRepository;
import com.animate.backend.repository.AnonRepository;
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

    @Autowired
    private AnonRepository anonRepository;

    @Autowired
    private ATokenRepository aTokenRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final Integer TOKEN_TTL = 6000;
    private final Integer ANON_TOKEN_TTL = 5000;

    // Cadastro
    public void signup(RegisterDTO registerDTO) throws Exception {
        String username = registerDTO.getUsername();
        String email = registerDTO.getEmail();
        String password = registerDTO.getPassword();
        String rePassword = registerDTO.getRePassword();
        String bio = registerDTO.getBio();

        String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*"
                + "@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        if (email == null || password == null) {
            throw new Exception("Dados de registro inválidos");
        }

        if (!email.matches(emailRegex)) {
            throw new Exception("Email inválido");
        }

        if (!password.equals(rePassword)) {
            throw new Exception("Senhas não conferem");
        }

        Optional<User> userFound = userRepository.findByEmail(email);
        if (userFound.isPresent()) {
            throw new Exception("Usuário já cadastrado");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Criptografando a senha
        user.setBio(bio);

        userRepository.save(user);
    }

    // Login
    public Token signin(String email, String password) {
        Optional<User> userFound = userRepository.findByEmail(email);

        if (userFound.isPresent()) {
            User user = userFound.get();

            // Comparando a senha digitada com a senha criptografada
            if (passwordEncoder.matches(password, user.getPassword())) {
                Token token = new Token();
                token.setUser(user);
                token.setToken(UUID.randomUUID().toString());
                token.setExpirationTime(Instant.now().plusSeconds(TOKEN_TTL).toEpochMilli());

                tokenRepository.save(token);
                return token;
            }
        }

        return null;
    }

    // Criação de token anônimo
    public AnonToken setAnon(Integer id, String username) {
        Optional<User> userFound = userRepository.findById(id);

        if (userFound.isPresent()) {
            User anonUser = userFound.get();
            anonUser.setUsername(username);
            anonRepository.save(anonUser);

            AnonToken anonToken = new AnonToken();
            anonToken.setUser(anonUser);
            anonToken.setAnonToken(UUID.randomUUID().toString());
            anonToken.setExpirationTime(Instant.now().plusSeconds(ANON_TOKEN_TTL).toEpochMilli());

            aTokenRepository.save(anonToken);
            return anonToken;
        }

        return null;
    }

    // Validação de token
    public boolean validate(String token) {
        Optional<Token> found = tokenRepository.findByToken(token);

        return found.isPresent() &&
                found.get().getExpirationTime() > Instant.now().toEpochMilli();
    }

    // Logout
    public void signout(String token) {
        Optional<Token> found = tokenRepository.findByToken(token);

        found.ifPresent(t -> {
            t.setExpirationTime(Instant.now().toEpochMilli());
            tokenRepository.save(t);
        });
    }

    // Obter usuário a partir do token
    public User toUser(String token) {
        Optional<Token> found = tokenRepository.findByToken(token);
        return found.map(Token::getUser).orElse(null);
    }
}
