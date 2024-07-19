package com.prax.crypto.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;

@Configuration
public class JwtConfig {

    private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);

    @Value("${spring.security.jwt.public-key}")
    private String publicKey;

    @Value("${spring.security.jwt.private-key}")
    private String privateKey;

    private RSAPublicKey getPublicKey() {
        try {
            var keyBytes = Base64.getDecoder().decode(publicKey);
            var spec = new X509EncodedKeySpec(keyBytes);
            var keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decode public key", e);
        }
    }

    private RSAPrivateKey getPrivateKey() {
        try {
            var keyBytes = Base64.getDecoder().decode(privateKey);
            var spec = new PKCS8EncodedKeySpec(keyBytes);
            var keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decode private key", e);
        }
    }

    @Bean
    JwtDecoder jwtDecoder() {
            return NimbusJwtDecoder.withPublicKey(getPublicKey()).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        var jwk = new RSAKey.Builder(getPublicKey())
                .privateKey(getPrivateKey())
                .build();

        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwks);
    }
}