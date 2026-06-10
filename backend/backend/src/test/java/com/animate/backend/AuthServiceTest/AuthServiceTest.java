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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private AnonRepository anonRepository;
    @Mock private ATokenRepository aTokenRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;

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
    //               SIGNIN
    // ==========================================

    @Nested
    @DisplayName("signin")
    class SigninTests {

        @Test
        @DisplayName("login com sucesso")
        void deveRealizarLoginComSucesso() {
            when(userRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("123456", "senhaCriptografada")).thenReturn(true);
            when(tokenRepository.save(any(Token.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

            Token token = authService.signin("usuario@email.com", "123456", false);

            assertNotNull(token);
            assertNotNull(token.getToken());
            assertEquals(user, token.getUser());
            verify(tokenRepository, times(1)).save(any(Token.class));
        }

        @Test
        @DisplayName("senha incorreta retorna null")
        void deveRetornarNullQuandoSenhaForIncorreta() {
            when(userRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("senhaErrada", "senhaCriptografada")).thenReturn(false);

            Token token = authService.signin("usuario@email.com", "senhaErrada", false);

            assertNull(token);
            verify(tokenRepository, never()).save(any(Token.class));
        }

        @Test
        @DisplayName("usuario inexistente retorna null")
        void deveRetornarNullQuandoUsuarioNaoExistir() {
            when(userRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

            Token token = authService.signin("naoexiste@email.com", "123456", false);

            assertNull(token);
            verify(tokenRepository, never()).save(any(Token.class));
        }

        @Test
        @DisplayName("BCrypt matches() usado - nunca texto puro")
        void deveUsarPasswordMatchesNuncaCompararTextoPuro() {
            when(userRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("123456", "senhaCriptografada")).thenReturn(true);
            when(tokenRepository.save(any(Token.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

            authService.signin("usuario@email.com", "123456", false);

            verify(passwordEncoder).matches("123456", "senhaCriptografada");
        }

        @Test
        @DisplayName("sem rememberMe expira em 6000s")
        void tokenSemRememberMeDeveExpirarEmAproximadamente6000s() {
            when(userRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(tokenRepository.save(any(Token.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

            long antes = Instant.now().plusSeconds(5990).toEpochMilli();
            Token token = authService.signin("usuario@email.com", "123456", false);
            long depois = Instant.now().plusSeconds(6010).toEpochMilli();

            assertTrue(token.getExpirationTime() >= antes);
            assertTrue(token.getExpirationTime() <= depois);
        }

        @Test
        @DisplayName("com rememberMe expira em 30 dias")
        void tokenComRememberMeDeveExpirarEm30Dias() {
            when(userRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(tokenRepository.save(any(Token.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

            long trintaDias = 60L * 60 * 24 * 30;
            long antes = Instant.now().plusSeconds(trintaDias - 10).toEpochMilli();
            Token token = authService.signin("usuario@email.com", "123456", true);
            long depois = Instant.now().plusSeconds(trintaDias + 10).toEpochMilli();

            assertTrue(token.getExpirationTime() >= antes);
            assertTrue(token.getExpirationTime() <= depois);
        }
    }

    // ==========================================
    //               SIGNUP
    // ==========================================

    @Nested
    @DisplayName("signup")
    class SignupTests {

        @Test
        @DisplayName("cadastro com sucesso")
        void deveCadastrarUsuarioComSucesso() throws Exception {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername("novoUsuario");
            dto.setEmail("novo@email.com");
            dto.setPassword("senha123");
            dto.setRePassword("senha123");
            dto.setBio("Minha bio");

            when(userRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("senha123")).thenReturn("senhaHash");

            assertDoesNotThrow(() -> authService.signup(dto));
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("senha salva hasheada - nunca texto puro")
        void deveSalvarSenhaHasheadaNuncaEmTextoPuro() throws Exception {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername("usuario");
            dto.setEmail("novo@email.com");
            dto.setPassword("senha123");
            dto.setRePassword("senha123");

            when(userRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("senha123")).thenReturn("$2a$10$hashBcrypt");

            authService.signup(dto);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertNotEquals("senha123", captor.getValue().getPassword());
            assertEquals("$2a$10$hashBcrypt", captor.getValue().getPassword());
        }

        @Test
        @DisplayName("senhas diferentes lancam excecao")
        void deveLancarExcecaoQuandoSenhasNaoConferem() {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername("usuario");
            dto.setEmail("teste@email.com");
            dto.setPassword("senha123");
            dto.setRePassword("senhaDiferente");

            Exception ex = assertThrows(Exception.class, () -> authService.signup(dto));
            assertEquals("Senhas não conferem", ex.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("username nulo lanca excecao")
        void deveLancarExcecaoQuandoUsernameForNulo() {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername(null);
            dto.setEmail("teste@email.com");
            dto.setPassword("senha123");
            dto.setRePassword("senha123");

            Exception ex = assertThrows(Exception.class, () -> authService.signup(dto));
            assertEquals("Nome de usuário é obrigatório", ex.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("username em branco lanca excecao")
        void deveLancarExcecaoQuandoUsernameForEmBranco() {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername("   ");
            dto.setEmail("teste@email.com");
            dto.setPassword("senha123");
            dto.setRePassword("senha123");

            Exception ex = assertThrows(Exception.class, () -> authService.signup(dto));
            assertEquals("Nome de usuário é obrigatório", ex.getMessage());
        }

        @Test
        @DisplayName("email nulo lanca excecao")
        void deveLancarExcecaoQuandoEmailForNulo() {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername("usuario");
            dto.setEmail(null);
            dto.setPassword("senha123");
            dto.setRePassword("senha123");

            Exception ex = assertThrows(Exception.class, () -> authService.signup(dto));
            assertEquals("Email é obrigatório", ex.getMessage());
        }

        @Test
        @DisplayName("email invalido lanca excecao")
        void deveLancarExcecaoQuandoEmailForInvalido() {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername("usuario");
            dto.setEmail("emailsemarroba");
            dto.setPassword("senha123");
            dto.setRePassword("senha123");

            Exception ex = assertThrows(Exception.class, () -> authService.signup(dto));
            assertEquals("Email inválido", ex.getMessage());
        }

        @Test
        @DisplayName("email ja cadastrado lanca excecao")
        void deveLancarExcecaoQuandoEmailJaCadastrado() {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername("usuario");
            dto.setEmail("usuario@email.com");
            dto.setPassword("senha123");
            dto.setRePassword("senha123");

            when(userRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(user));

            Exception ex = assertThrows(Exception.class, () -> authService.signup(dto));
            assertEquals("Usuário já cadastrado", ex.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    // ==========================================
    //               VALIDATE
    // ==========================================

    @Nested
    @DisplayName("validate")
    class ValidateTests {

        @Test
        @DisplayName("token valido retorna true")
        void deveRetornarTrueParaTokenValidoENaoExpirado() {
            Token token = new Token();
            token.setExpirationTime(Instant.now().plusSeconds(3600).toEpochMilli());
            when(tokenRepository.findByToken("token-valido")).thenReturn(Optional.of(token));

            assertTrue(authService.validate("token-valido"));
        }

        @Test
        @DisplayName("token expirado retorna false")
        void deveRetornarFalseParaTokenExpirado() {
            Token token = new Token();
            token.setExpirationTime(Instant.now().minusSeconds(1).toEpochMilli());
            when(tokenRepository.findByToken("token-expirado")).thenReturn(Optional.of(token));

            assertFalse(authService.validate("token-expirado"));
        }

        @Test
        @DisplayName("token inexistente retorna false")
        void deveRetornarFalseParaTokenInexistente() {
            when(tokenRepository.findByToken("fantasma")).thenReturn(Optional.empty());

            assertFalse(authService.validate("fantasma"));
        }
    }

    // ==========================================
    //               SIGNOUT
    // ==========================================

    @Nested
    @DisplayName("signout")
    class SignoutTests {

        @Test
        @DisplayName("token invalido apos logout")
        void deveInvalidarTokenNoSignout() {
            Token token = new Token();
            token.setExpirationTime(Instant.now().plusSeconds(3600).toEpochMilli());
            when(tokenRepository.findByToken("tok")).thenReturn(Optional.of(token));

            authService.signout("tok");

            ArgumentCaptor<Token> captor = ArgumentCaptor.forClass(Token.class);
            verify(tokenRepository).save(captor.capture());
            assertTrue(captor.getValue().getExpirationTime() <= Instant.now().toEpochMilli());
        }

        @Test
        @DisplayName("token inexistente nao lanca excecao")
        void naoDeveLancarExcecaoNoSignoutComTokenInexistente() {
            when(tokenRepository.findByToken("naoexiste")).thenReturn(Optional.empty());

            assertDoesNotThrow(() -> authService.signout("naoexiste"));
            verify(tokenRepository, never()).save(any());
        }
    }

    // ==========================================
    //               TO USER
    // ==========================================

    @Nested
    @DisplayName("toUser")
    class ToUserTests {

        @Test
        @DisplayName("retorna usuario correto para token valido")
        void deveRetornarUsuarioParaTokenValido() {
            Token token = new Token();
            token.setUser(user);
            when(tokenRepository.findByToken("tok")).thenReturn(Optional.of(token));

            assertEquals(user, authService.toUser("tok"));
        }

        @Test
        @DisplayName("token inexistente retorna null")
        void deveRetornarNullParaTokenInexistenteEmToUser() {
            when(tokenRepository.findByToken("naoexiste")).thenReturn(Optional.empty());

            assertNull(authService.toUser("naoexiste"));
        }
    }
}
