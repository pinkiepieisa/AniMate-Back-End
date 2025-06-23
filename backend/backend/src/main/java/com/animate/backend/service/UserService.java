package com.animate.backend.service;

import com.animate.backend.model.User;
import com.animate.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public UserService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public User updateBioByToken(String token, String bio) {
        String userId = tokenService.getUserIdFromToken(token);
        if (userId == null) return null;

        Optional<User> optionalUser = userRepository.findById(Integer.parseInt(userId));
        if (optionalUser.isEmpty()) return null;

        User user = optionalUser.get();
        user.setBio(bio);
        userRepository.save(user);
        return user;
    }

    public User getUserByToken(String token) {
        String userId = tokenService.getUserIdFromToken(token);
        if (userId == null) return null;

        return userRepository.findById(Integer.parseInt(userId)).orElse(null);
    }
}
