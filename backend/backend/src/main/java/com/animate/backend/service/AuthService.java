package com.animate.backend.service;


import com.animate.backend.dto.RegisterDTO;
import com.animate.backend.model.AnonToken;
import com.animate.backend.model.LoggedUser;
import com.animate.backend.model.Token;
import com.animate.backend.model.User;
import com.animate.backend.repository.ATokenRepository;
import com.animate.backend.repository.AnonRepository;
import com.animate.backend.repository.TokenRepository;
import com.animate.backend.repository.UserRepository;
import jakarta.persistence.Id;
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

    @Autowired
    private ATokenRepository aTokenRepository;
    private Integer AnonTOKEN_TTL = 5000;

    @Autowired
    private TokenRepository tokenRepository;
    private Integer TOKEN_TTL = 6000;






    public void signup(RegisterDTO registerDTO) throws Exception{


        String username = registerDTO.getUsername();
        String email = registerDTO.getEmail();
        String password = registerDTO.getPassword();
        String rePassword = registerDTO.getRePassword();
        String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*"
                + "@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        if (registerDTO.getEmail() == null || registerDTO.getPassword() == null) {
            throw new Exception("Dados de registro inválidos");
        }

        boolean emailValid = email.matches(emailRegex);
        if(!emailValid){
            throw new Exception("Email inválido");
        }

        if(!password.equals(rePassword)) {
            throw new Exception("Senhas não conferem");
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

    public AnonToken setAnon(Integer id, String username){

        User anon = new User();
        anon.setId(id);
        anon.setusername(username);
        anonRepository.save(anon);

        Optional<LoggedUser> found = userRepository.findById(id);
        if(found.isPresent()) {
            AnonToken anonToken = new AnonToken();
            anonToken.setUser(found.get());
            anonToken.setAnonToken(UUID.randomUUID().toString());
            anonToken.setExpirationTime(Instant.now().plusSeconds(AnonTOKEN_TTL).toEpochMilli());
            aTokenRepository.save(anonToken);
            return anonToken;
        }
        return null;

    }

    public Boolean validate(String token) {

        Optional<Token> found = tokenRepository.findByToken(token);
        return found.isPresent() && found.get().getExpirationTime()
                > Instant.now().toEpochMilli();
    }

    public void signout(String token) {

        Optional<Token> found = tokenRepository.findByToken(token);
        found.ifPresent(t -> {t.setExpirationTime(Instant.now().toEpochMilli());
            tokenRepository.save(t);
        });

    }

    public User toUser(String token) {

        Optional<Token> found = tokenRepository.findByToken(token);
        return found.isPresent() ? found.get().getUser() : null;
    }



}
