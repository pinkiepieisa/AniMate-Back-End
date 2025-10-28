package com.animate.backend.service;

import com.animate.backend.model.User;
import com.animate.backend.repository.UserRepository;


import org.springframework.stereotype.Service;

import com.animate.backend.dto.UserProfileDTO;
import com.animate.backend.model.ProfilePic;
import com.animate.backend.repository.PictureRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    
    private final UserRepository userRepository;
    private final PictureRepository pictureRepository;
    private final TokenService tokenService;
    private final ChibisafeService chibisafeService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, TokenService tokenService,PictureRepository pictureRepository, ChibisafeService chibisafeService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.pictureRepository = pictureRepository;
        this.chibisafeService = chibisafeService;
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

    public ProfilePic upsertProfilePicture(String token, String imageUrl, String imageUuid) {
        User user = getUserByToken(token);
        if (user == null) {
            return null;
        }

        
        Optional<ProfilePic> existingPicOptional = pictureRepository.findByUser(user);

        ProfilePic profilePic;
        if (existingPicOptional.isPresent()) {
            profilePic = existingPicOptional.get();
            // delete previous file on chibisafe if uuid present
            String prevUuid = profilePic.getImageUuid();
            if (prevUuid != null && !prevUuid.isBlank()) {
                try {
                    logger.debug("Attempting to delete previous chibisafe uuid={}", prevUuid);
                    boolean deleted = chibisafeService.deleteFile(prevUuid);
                    logger.debug("Chibisafe delete result for {} = {}", prevUuid, deleted);
                } catch (Exception e) {
                    logger.warn("Error while deleting previous chibisafe file {}: {}", prevUuid, e.getMessage());
                }
            }
            profilePic.setImageUrl(imageUrl);
            profilePic.setImageUuid(imageUuid);
        } else {
            profilePic = new ProfilePic(imageUrl, imageUuid, user);
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

    public UserProfileDTO getUserProfileByToken(String token) {

        
        User user = getUserByToken(token);
        if(user == null){
            return null;
        }

        // Busca a foto de perfil associada ao usuário
    ProfilePic pic = pictureRepository.findByUser(user).orElse(null);
    String imageUrl = pic != null ? pic.getImageUrl() : null;
    String imageUuid = pic != null ? pic.getImageUuid() : null;

    // Cria e retorna o DTO com todos os dados, incluindo uuid
    return new UserProfileDTO(user.getUsername(), user.getBio(), imageUrl, imageUuid);
    
    }

    // Utility: find user by email
    public User getUserByEmail(String email) {
        if (email == null) return null;
        return userRepository.findByEmail(email).orElse(null);
    }

    // Utility: update and save user's password (expects encoded password)
    public User updatePassword(User user, String encodedPassword) {
        if (user == null) return null;
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }
}

   

