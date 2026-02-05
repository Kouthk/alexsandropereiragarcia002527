package br.gov.mt.seplag.seletivo.service;

import br.gov.mt.seplag.seletivo.exception.AuthorizationException;
import br.gov.mt.seplag.seletivo.security.config.JwtProperties;
import br.gov.mt.seplag.seletivo.security.entity.User;
import br.gov.mt.seplag.seletivo.security.repository.UserRepository;
import br.gov.mt.seplag.seletivo.security.service.AuthService;
import br.gov.mt.seplag.seletivo.security.service.JwtService;
import br.gov.mt.seplag.seletivo.security.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private TokenService tokenService;
    @Mock private UserRepository userRepository;
    @Mock private JwtProperties jwtProperties;

    private AuthService authService;

    @BeforeEach
    void setup() {
        authService = new AuthService(authenticationManager, jwtService, tokenService, userRepository, jwtProperties);
    }

    @Test
    void loginComCredenciaisInvalidasDeveLancarAuthorizationException() {
        when(userRepository.findByUsername("usuario"))
                .thenReturn(Optional.of(new User()));

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login("usuario", "errada"))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("Credenciais inválidas.");
    }
}
