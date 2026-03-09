package com.example.taskmanager.service.impl;

import com.example.taskmanager.dto.request.LoginRequest;
import com.example.taskmanager.dto.request.RegisterRequest;
import com.example.taskmanager.dto.response.AuthResponse;
import com.example.taskmanager.exception.UserAlreadyExistsException;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.model.User;
import com.example.taskmanager.model.enums.Role;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtTokenProvider;
import com.example.taskmanager.service.interfaces.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Implémentation du service d'authentification
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    // Inscrit un nouvel utilisateur après vérification de l'email
    @Override
    public AuthResponse register(RegisterRequest request) {

        // Vérifie si l'email est déjà utilisé
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        // Crée le nouvel utilisateur avec le mot de passe encodé
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Hash du mot de passe avant stockage en base
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Rôle par défaut pour tout nouvel utilisateur
        user.setRole(Role.ROLE_USER);

        // Sauvegarde en base de données
        userRepository.save(user);

        // Génère le token JWT pour l'utilisateur inscrit
        String token = jwtTokenProvider.generateToken(user.getEmail());

        return UserMapper.toAuthResponse(user, token);
    }

    // Connecte un utilisateur existant après vérification des credentials
    @Override
    public AuthResponse login(LoginRequest request) {

        // Spring Security vérifie l'email et le mot de passe
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Récupère l'email de l'utilisateur authentifié
        String email = authentication.getName();

        // Charge l'utilisateur depuis la base de données
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Génère le token JWT pour l'utilisateur connecté
        String token = jwtTokenProvider.generateToken(email);

        return UserMapper.toAuthResponse(user, token);
    }
}
