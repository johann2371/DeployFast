package com.example.taskmanager.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// Charge les propriétés JWT depuis application.yml
// Préfixe "jwt" dans le fichier de configuration
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    // Clé secrète pour signer les tokens JWT
    private String secret;

    // Durée de validité du token en millisecondes (ex: 86400000 = 24h)
    private long expiration;

    public JwtConfig() {}

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}
