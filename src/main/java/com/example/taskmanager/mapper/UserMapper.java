package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.response.AuthResponse;
import com.example.taskmanager.model.User;

// Convertit les entités User en objets DTO pour les réponses API
public class UserMapper {

    // Convertit un User en AuthResponse après connexion ou inscription
    public static AuthResponse toAuthResponse(User user, String token) {
        AuthResponse response = new AuthResponse();

        // Token JWT généré après authentification
        response.setToken(token);

        // Email de l'utilisateur connecté
        response.setEmail(user.getEmail());

        // Nom de l'utilisateur connecté
        response.setName(user.getName());

        return response;
    }
}
