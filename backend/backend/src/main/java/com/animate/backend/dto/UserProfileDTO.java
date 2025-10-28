package com.animate.backend.dto;

import com.animate.backend.model.ProfilePic;
import com.animate.backend.model.User;

public class UserProfileDTO {
    private String username;
    private String bio;
    private String imageUrl; // Adicione este campo
    private String imageUuid;

    // Construtor
    public UserProfileDTO(String username, String bio, String imageUrl, String imageUuid) {
        this.username = username;
        this.bio = bio;
        this.imageUrl = imageUrl; // Adicione esta linha
        this.imageUuid = imageUuid;
    }

    // Getters e Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    // Adicione Getter e Setter para imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUuid() {
        return imageUuid;
    }

    public void setImageUuid(String imageUuid) {
        this.imageUuid = imageUuid;
    }
}