package com.animate.backend.AuthServiceTest;

import com.animate.backend.dto.RegisterDTO;
import com.animate.backend.model.Token;
import com.animate.backend.model.User;
import com.animate.backend.repository.ATokenRepository;
import com.animate.backend.repository.AnonRepository;
import com.animate.backend.repository.TokenRepository;
import com.animate.backend.repository.UserRepository;
import com.animate.backend.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private AnonRepository anonRepository;

    @Mock
    private ATokenRepository aTokenRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    // O Mockito usa reflexão para injetar os @Mock acima mesmo nos campos privados com @Autowired
    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("usuarioTeste");
        user.setEmail("usuario@email.com");
        user.setPassword("senhaCriptografada");
    }

    // ==========================================
    //          TESTES DE LOGIN (SIGNIN)
    // ==========================================

    @Test
    void deveRealizarLoginComSucesso() {
        // Arrange
        when(userRepository.findByEmail("usuario@email.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", "senhaCriptografada"))
                .thenReturn(true);

        // SOLUÇÃO DO RETORNO NULL: Faz o tokenRepository.save() retornar o próprio token que recebeu
        when(tokenRepository.save(any(Token.class)))
                .thenAnswer(AdditionalAnswers.returnsFirstArg());

        // Act
        Token token = authService.signin("usuario@email.com", "123456", false);

        // Assert
        assertNotNull(token);
        assertNotNull(token.getToken());
        assertEquals(user, token.getUser());

        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void deveRetornarNullQuandoSenhaForIncorreta() {
        // Arrange
        when(userRepository.findByEmail("usuario@email.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("senhaErrada", "senhaCriptografada"))
                .thenReturn(false);

        // Act
        Token token = authService.signin("usuario@email.com", "senhaErrada", false);

        // Assert
        assertNull(token);
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void deveRetornarNullQuandoUsuarioNaoExistir() {
        // Arrange
        when(userRepository.findByEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        // Act
        Token token = authService.signin("naoexiste@email.com", "123456", false);

        // Assert
        assertNull(token);
        verify(tokenRepository, never()).save(any(Token.class));
    }

    // ==========================================
    //         TESTES DE CADASTRO (SIGNUP)
    // ==========================================

    @Test
    void deveCadastrarUsuarioComSucesso() throws Exception {
        // Arrange
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("novoUsuario");
        dto.setEmail("novo@email.com");
        dto.setPassword("senha123");
        dto.setRePassword("senha123");
        dto.setBio("Minha bio");

        when(userRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha123")).thenReturn("senhaHash");

        // Act & Assert (Não deve lançar nenhuma exceção)
        assertDoesNotThrow(() -> authService.signup(dto));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deveLancarExcecaoQuandoSenhasNaoConferem() {
        // Arrange
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("usuario");
        dto.setEmail("teste@email.com");
        dto.setPassword("senha123");
        dto.setRePassword("senhaDiferente");

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> authService.signup(dto));
        assertEquals("Senhas não conferem", exception.getMessage());
        
        verify(userRepository, never()).save(any(User.class));
    }
}