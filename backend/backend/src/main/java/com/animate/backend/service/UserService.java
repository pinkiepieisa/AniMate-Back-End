package com.animate.backend.service;

import com.animate.backend.model.LoggedUser;
import com.animate.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public LoggedUser updateBioByEmail(String email, String bio) {
        LoggedUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setBio(bio);
        return userRepository.save(user);
    }
}


