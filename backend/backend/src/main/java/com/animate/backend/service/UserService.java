package com.animate.backend.service;

import com.animate.backend.model.User;
import com.animate.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animate.backend.dto.PictureDTO;
import com.animate.backend.model.ProfilePic;
import com.animate.backend.repository.PictureRepository;
import java.util.Optional;

@Service
public class UserService {

    
    private final UserRepository userRepository;
    private final PictureRepository pictureRepository;
    private final TokenService tokenService;

    public UserService(UserRepository userRepository, TokenService tokenService,PictureRepository pictureRepository) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.pictureRepository = pictureRepository;
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

    public ProfilePic upsertProfilePicture(String token, String imageUrl) {
        User user = getUserByToken(token);
        if (user == null) {
            return null;
        }

        
        Optional<ProfilePic> existingPicOptional = pictureRepository.findByUser(user);

        ProfilePic profilePic;
        if (existingPicOptional.isPresent()) {
            
            profilePic = existingPicOptional.get();
            profilePic.setImageUrl(imageUrl);
        } else {
            
            profilePic = new ProfilePic(imageUrl, user);
        }

        return pictureRepository.save(profilePic);
    }

    public User updateUsernameByToken(String token, String newUsername) {
        String userId = tokenService.getUserIdFromToken(token);
        if (userId == null) return null;

        Optional<User> optionalUser = userRepository.findById(Integer.parseInt(userId));
        if (optionalUser.isEmpty()) return null;

        User user = optionalUser.get();
        
        user.setUsername(newUsername); // Assumindo que a classe User tem um setUsername
        userRepository.save(user);
        return user;
    }
}

   

