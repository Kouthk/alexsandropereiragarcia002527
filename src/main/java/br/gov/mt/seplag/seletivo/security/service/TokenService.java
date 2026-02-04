package br.gov.mt.seplag.seletivo.security.service;

import br.gov.mt.seplag.seletivo.exception.LayerDefinition;
import br.gov.mt.seplag.seletivo.exception.TokenException;
import br.gov.mt.seplag.seletivo.exception.enums.LayerEnum;
import br.gov.mt.seplag.seletivo.security.config.JwtProperties;
import br.gov.mt.seplag.seletivo.security.entity.Token;
import br.gov.mt.seplag.seletivo.security.entity.User;
import br.gov.mt.seplag.seletivo.security.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenService implements LayerDefinition {

    private final TokenRepository tokenRepository;
    private final JwtProperties jwtProperties;

    public TokenService(TokenRepository tokenRepository, JwtProperties jwtProperties) {
        this.tokenRepository = tokenRepository;
        this.jwtProperties = jwtProperties;
    }

    public String createRefreshToken(User user) {
        String refreshToken = UUID.randomUUID().toString();
        Token token = new Token();
        token.setTokenSession(refreshToken);
        token.setUser(user);
        token.setDeletedAt(LocalDateTime.now().plusDays(jwtProperties.refreshTokenExpirationDays()));
        tokenRepository.save(token);
        return refreshToken;
    }

    public User validateRefreshToken(String refreshToken) {
        Token token = tokenRepository.findByTokenSession(refreshToken)
                .orElseThrow(() -> new TokenException("Refresh token inválido.", this));

        if (token.getDeletedAt().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            throw new TokenException("Refresh token expirado.", this);
        }

        return token.getUser();
    }

    public void revokeRefreshToken(String refreshToken) {
        tokenRepository.findByTokenSession(refreshToken)
                .ifPresent(tokenRepository::delete);
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