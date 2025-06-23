package com.animate.backend.dto;

import com.animate.backend.model.User;

public record UserProfileDTO(Integer id, String username, String email, String bio) {
    public UserProfileDTO(User user) {
        this(user.getId(), user.getUsername(), user.getEmail(), user.getBio());
    }
}
