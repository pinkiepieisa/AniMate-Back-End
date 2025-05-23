package com.animate.backend.service;


import com.animate.backend.dto.RegisterDTO;
import com.animate.backend.model.LoggedUser;
import com.animate.backend.model.Token;
import com.animate.backend.repository.AnonRepository;
import com.animate.backend.repository.TokenRepository;
import com.animate.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnonRepository anonRepository;
    private Integer AnonTOKEN_TTL = 5;

    @Autowired
    private TokenRepository tokenRepository;
    private Integer TOKEN_TTL = 1;



    public void signup(RegisterDTO registerDTO) throws Exception{


        String username = registerDTO.getUsername();
        String email = registerDTO.getEmail();
        String password = registerDTO.getPassword();
        String rePassword = registerDTO.getRePassword();

        if(!password.equals(rePassword)) {
            throw new Exception("Passwords do not match");
        }
        Optional<LoggedUser> userFound = userRepository.findByEmail(email);
        if(userFound.isPresent()) {
            throw new Exception("Usuário já cadastrado");
        }

        LoggedUser user = new LoggedUser();
        user.setusername(username);
        user.setEmail(email);
        user.setPassword(password);
        userRepository.save(user);


    }
    public Token signin(String email, String password) {
        LoggedUser user = new LoggedUser();
        user.setEmail(email);
        user.setPassword(password);

        Optional<LoggedUser> userFound = userRepository.findByEmail(email);
        if(userFound.isPresent() && userFound.get().getPassword().equals(password)) {
            Token token = new Token();
            token.setUser(userFound.get());
            token.setToken(UUID.randomUUID().toString());
            token.setExpirationTime(Instant.now().plusSeconds(TOKEN_TTL).toEpochMilli());
            tokenRepository.save(token);
            return token;
        }
        return null;
    }



}
