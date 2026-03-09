package com.example.taskmanager.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

// Classe utilitaire pour la génération et validation des tokens JWT
@Component
public class JwtTokenProvider {

    // Clé secrète définie dans application.yml
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Durée de validité du token en millisecondes (définie dans application.yml)
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // Génère la clé de signature à partir du secret
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Génère un token JWT à partir de l'email de l'utilisateur
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(email)           // Email comme sujet du token
                .setIssuedAt(now)            // Date de création
                .setExpiration(expiryDate)   // Date d'expiration
                .signWith(getSigningKey())   // Signature avec la clé secrète
                .compact();
    }

    // Extrait l'email de l'utilisateur depuis le token JWT
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Vérifie si le token est valide et non expiré
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            // Token expiré
            System.out.println("Token expiré : " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            // Token mal formé
            System.out.println("Token invalide : " + ex.getMessage());
        } catch (Exception ex) {
            // Toute autre erreur liée au token
            System.out.println("Erreur token : " + ex.getMessage());
        }
        return false;
    }
}
