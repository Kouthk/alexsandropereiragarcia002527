package br.gov.mt.seplag.seletivo.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpirationMinutes,
        long refreshTokenExpirationDays
) {
}