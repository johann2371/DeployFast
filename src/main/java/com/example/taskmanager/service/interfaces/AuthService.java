package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.request.LoginRequest;
import com.example.taskmanager.dto.request.RegisterRequest;
import com.example.taskmanager.dto.response.AuthResponse;

// Contrat du service d'authentification
// Toute implémentation doit fournir ces deux méthodes
public interface AuthService {

    // Inscrit un nouvel utilisateur et retourne un token JWT
    AuthResponse register(RegisterRequest request);

    // Connecte un utilisateur existant et retourne un token JWT
    AuthResponse login(LoginRequest request);
}
