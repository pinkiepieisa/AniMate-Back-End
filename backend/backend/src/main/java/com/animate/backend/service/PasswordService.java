package com.animate.backend.service;

import com.animate.backend.model.PasswordResetToken;
import com.animate.backend.model.User;
import com.animate.backend.repository.PasswordResetTokenRepository;
import com.animate.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordService.class);

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Token validity in seconds (e.g., 3600 = 1 hour)
    private final long TOKEN_VALID_SECONDS = 3600;

    public String createPasswordResetToken(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setUser(user);
        prt.setExpirationTime(Instant.now().getEpochSecond() + TOKEN_VALID_SECONDS);
        prt.setUsed(false);
        tokenRepository.save(prt);

        // send email if mailSender configured, otherwise just log the link (dev mode)
        String resetLink = String.format("%s/auth/reset-password?token=%s", "http://localhost:8080", token);
        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                message.setSubject("AniMate - Recuperação de senha");
                message.setText("Use este link para redefinir sua senha: " + resetLink + "\nObs: o link expira em 1 hora.");
                mailSender.send(message);
                logger.info("Password reset email sent to {}", user.getEmail());
            } catch (Exception e) {
                logger.warn("Failed to send password reset email: {}", e.getMessage());
                logger.info("Reset link for {}: {}", user.getEmail(), resetLink);
            }
        } else {
            logger.info("(dev) Password reset link for {}: {}", user.getEmail(), resetLink);
        }

        return token;
    }

    public Optional<User> validateTokenAndGetUser(String token) {
        Optional<PasswordResetToken> maybe = tokenRepository.findByToken(token);
        if (maybe.isEmpty()) return Optional.empty();
        PasswordResetToken prt = maybe.get();
        if (prt.isUsed()) return Optional.empty();
        if (prt.getExpirationTime() < Instant.now().getEpochSecond()) return Optional.empty();
        return Optional.ofNullable(prt.getUser());
    }

    public boolean markTokenUsed(String token) {
        Optional<PasswordResetToken> maybe = tokenRepository.findByToken(token);
        if (maybe.isEmpty()) return false;
        PasswordResetToken prt = maybe.get();
        prt.setUsed(true);
        tokenRepository.save(prt);
        return true;
    }

    public boolean resetPassword(String token, String rawPassword) {
        Optional<User> maybeUser = validateTokenAndGetUser(token);
        if (maybeUser.isEmpty()) return false;
        User user = maybeUser.get();
        String encoded = passwordEncoder.encode(rawPassword);
        user.setPassword(encoded);
        userRepository.save(user);
        markTokenUsed(token);
        return true;
    }
}
