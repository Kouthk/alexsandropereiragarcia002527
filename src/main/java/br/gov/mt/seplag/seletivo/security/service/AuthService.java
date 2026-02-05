package br.gov.mt.seplag.seletivo.security.service;

import br.gov.mt.seplag.seletivo.exception.AuthorizationException;
import br.gov.mt.seplag.seletivo.exception.LayerDefinition;
import br.gov.mt.seplag.seletivo.exception.enums.LayerEnum;
import br.gov.mt.seplag.seletivo.security.config.JwtProperties;
import br.gov.mt.seplag.seletivo.security.dto.AuthResponseDTO;
import br.gov.mt.seplag.seletivo.security.entity.User;
import br.gov.mt.seplag.seletivo.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements LayerDefinition {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            TokenService tokenService,
            UserRepository userRepository,
            JwtProperties jwtProperties
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.jwtProperties = jwtProperties;
    }

    public AuthResponseDTO login(String username, String password) {
        try {

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AuthorizationException("Usuário não encontrado.", this));

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            String accessToken = jwtService.generateAccessToken(username);

            String refreshToken = tokenService.createRefreshToken(user);

            return new AuthResponseDTO(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    jwtProperties.accessTokenExpirationMinutes() * 60
            );
        } catch (BadCredentialsException ex) {
            throw new AuthorizationException("Credenciais inválidas.", this);
        }
    }

    @Transactional
    public AuthResponseDTO refresh(String refreshToken) {
        User user = tokenService.validateRefreshToken(refreshToken);
        tokenService.revokeRefreshToken(refreshToken);

        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String newRefreshToken = tokenService.createRefreshToken(user);

        return new AuthResponseDTO(
                accessToken,
                newRefreshToken,
                "Bearer",
                jwtProperties.accessTokenExpirationMinutes() * 60
        );
    }

    public void logout(String refreshToken) {
        tokenService.revokeRefreshToken(refreshToken);
    }

    @Override
    public String getClassName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public LayerEnum getLayer() {
        return LayerEnum.SECURITY;
    }
}