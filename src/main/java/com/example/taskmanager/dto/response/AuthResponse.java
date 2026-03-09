package com.example.taskmanager.dto.response;

// Réponse retournée au client après une connexion ou inscription réussie
public class AuthResponse {

    // Token JWT à utiliser dans les requêtes suivantes
    private String token;

    // Email de l'utilisateur connecté
    private String email;

    // Nom de l'utilisateur connecté
    private String name;

    public AuthResponse() {}

    public AuthResponse(String token, String email, String name) {
        this.token = token;
        this.email = email;
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
