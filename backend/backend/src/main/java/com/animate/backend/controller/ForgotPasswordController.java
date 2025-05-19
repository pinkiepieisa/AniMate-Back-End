package com.animate.backend.controller;

import com.animate.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final UserRepository userRepository;

    public ForgotPasswordController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Inserção do e-mail para verificação
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Por favor, nos forneça um e-mail válido"));
        //! Ajeitar e entender porque está dando erro
    }
}
