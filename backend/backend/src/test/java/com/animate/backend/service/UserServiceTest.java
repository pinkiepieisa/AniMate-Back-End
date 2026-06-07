package com.animate.backend.service;

import com.animate.backend.model.User;
import com.animate.backend.repository.PictureRepository;
import com.animate.backend.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PictureRepository pictureRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private ChibisafeService chibisafeService;

    @InjectMocks
    private UserService userService;

    @Test
    void deveRetornarUsuarioQuandoEmailExistir() {

        String email = "teste@email.com";

        User usuario = new User();
        usuario.setEmail(email);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(usuario));

        User resultado = userService.getUserByEmail(email);

        assertNotNull(resultado);
        assertEquals(email, resultado.getEmail());

        verify(userRepository, times(1))
                .findByEmail(email);
    }
}