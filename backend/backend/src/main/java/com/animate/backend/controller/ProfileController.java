package com.animate.backend.controller;

import com.animate.backend.model.LoggedUser;
import com.animate.backend.service.UserService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/LoggedUsers")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }


    public record BioUpdateRequest(String bio) {}

    @PutMapping("/bio")
    public ResponseEntity<LoggedUser> updateUserBio(
            @RequestBody BioUpdateRequest request,
            Principal principal) {

        String email = principal.getName();
        LoggedUser updatedUser = userService.updateBioByEmail(email, request.bio());

        return ResponseEntity.ok(updatedUser);
    }
}
