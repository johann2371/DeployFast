package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.LoginRequest;
import com.example.taskmanager.dto.request.RegisterRequest;
import com.example.taskmanager.dto.response.ApiResponse;
import com.example.taskmanager.dto.response.AuthResponse;
import com.example.taskmanager.service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST responsable de la gestion de l'authentification des utilisateurs.
 *
 * <p>Cette classe expose les endpoints permettant :
 * <ul>
 *     <li>L'inscription d'un nouvel utilisateur</li>
 *     <li>La connexion d'un utilisateur existant</li>
 * </ul>
 *
 * <p>Les routes définies ici sont publiques et ne nécessitent pas
 * d'authentification préalable. Elles permettent de générer un token JWT
 * qui sera ensuite utilisé pour accéder aux endpoints protégés de l'API.
 *
 * <p>La logique métier est déléguée au service {@link AuthService}.
 *
 * <p>Les annotations Swagger permettent de générer automatiquement
 * la documentation interactive de l'API.
 */
@Tag(name = "Authentification", description = "Inscription et connexion des utilisateurs")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * Service chargé de gérer la logique métier liée à l'authentification.
     */
    private final AuthService authService;

    /**
     * Constructeur permettant l'injection du service d'authentification.
     *
     * @param authService service responsable de la gestion des utilisateurs
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint permettant l'inscription d'un nouvel utilisateur.
     *
     * <p>Cette méthode :
     * <ul>
     *     <li>Reçoit les informations d'inscription de l'utilisateur</li>
     *     <li>Valide les données reçues</li>
     *     <li>Délègue la création du compte au service d'authentification</li>
     *     <li>Retourne un token JWT après la création du compte</li>
     * </ul>
     *
     * @param request données d'inscription de l'utilisateur
     * @return réponse HTTP contenant le token JWT et les informations associées
     */
    @Operation(
            summary = "Inscrire un nouvel utilisateur",
            description = "Crée un compte utilisateur et retourne un token JWT"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Inscription réussie",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Données invalides"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Email déjà utilisé"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        // Appelle le service pour créer l'utilisateur et générer un token JWT
        AuthResponse authResponse = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Inscription réussie", authResponse));
    }

    /**
     * Endpoint permettant à un utilisateur existant de se connecter.
     *
     * <p>Cette méthode :
     * <ul>
     *     <li>Reçoit les identifiants de connexion</li>
     *     <li>Valide les données envoyées</li>
     *     <li>Authentifie l'utilisateur via le service d'authentification</li>
     *     <li>Retourne un token JWT si l'authentification est réussie</li>
     * </ul>
     *
     * @param request identifiants de connexion de l'utilisateur
     * @return réponse HTTP contenant le token JWT
     */
    @Operation(
            summary = "Connecter un utilisateur",
            description = "Authentifie l'utilisateur et retourne un token JWT"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Connexion réussie",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Données invalides"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Identifiants incorrects"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        // Appelle le service pour authentifier l'utilisateur et générer un token JWT
        AuthResponse authResponse = authService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Connexion réussie", authResponse));
    }
}