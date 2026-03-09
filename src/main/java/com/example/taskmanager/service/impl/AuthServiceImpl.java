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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service d'authentification.
 *
 * Ce service gère :
 * - l'inscription de nouveaux utilisateurs
 * - la connexion d'utilisateurs existants
 *
 * Il utilise Spring Security pour l'authentification et
 * JWT pour générer les tokens d'accès.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

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

    /**
     * Inscrit un nouvel utilisateur après avoir vérifié que l'email n'est pas déjà utilisé.
     *
     * @param request Objet contenant les informations d'inscription (nom, email, mot de passe)
     * @return AuthResponse contenant les informations de l'utilisateur et le token JWT
     * @throws UserAlreadyExistsException si l'email est déjà enregistré
     */
    @Override
    public AuthResponse register(RegisterRequest request) {

        logger.info("Tentative d'inscription pour l'email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Échec de l'inscription : l'email {} existe déjà", request.getEmail());
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);

        logger.info("Utilisateur {} inscrit avec succès", user.getEmail());

        String token = jwtTokenProvider.generateToken(user.getEmail());

        logger.debug("Token JWT généré pour l'utilisateur {} : {}", user.getEmail(), token);

        return UserMapper.toAuthResponse(user, token);
    }

    /**
     * Connecte un utilisateur existant après vérification de ses identifiants.
     *
     * @param request Objet contenant email et mot de passe
     * @return AuthResponse avec les informations de l'utilisateur et le token JWT
     * @throws RuntimeException si l'utilisateur n'existe pas
     */
    @Override
    public AuthResponse login(LoginRequest request) {

        logger.info("Tentative de connexion pour l'email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur introuvable pour l'email {}", email);
                    return new RuntimeException("Utilisateur introuvable");
                });

        logger.info("Utilisateur {} connecté avec succès", email);

        String token = jwtTokenProvider.generateToken(email);

        logger.debug("Token JWT généré pour l'utilisateur {} : {}", email, token);

        return UserMapper.toAuthResponse(user, token);
    }
}