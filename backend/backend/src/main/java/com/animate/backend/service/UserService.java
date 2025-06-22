package com.animate.backend.service;

import com.animate.backend.model.User;
import com.animate.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User updateBioByEmail(String email, String bio) {
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        user.setBio(bio);
        return userRepository.save(user);
    }
}


