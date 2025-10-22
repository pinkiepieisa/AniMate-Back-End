package com.animate.backend.dto;

import com.animate.backend.model.ProfilePic;
import com.animate.backend.model.User;

public record PictureDTO(Long id, String imageUrl, User user) {
    public PictureDTO(ProfilePic picture) {
        this(picture.getId(), picture.getImageUrl(), picture.getUser());
    }
}
