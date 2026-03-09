package com.example.taskmanager.controller;

import com.example.taskmanager.dto.response.ApiResponse;
import com.example.taskmanager.model.User;
import com.example.taskmanager.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST responsable de la gestion du profil utilisateur.
 *
 * <p>Ce contrôleur expose des endpoints permettant à l'utilisateur
 * authentifié de consulter ses informations personnelles.
 *
 * <p>Toutes les routes de ce contrôleur sont protégées par JWT. L'utilisateur
 * doit donc fournir un token valide dans l'en-tête Authorization.
 */
@Tag(name = "Utilisateurs", description = "Gestion du profil utilisateur")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * Service chargé de la gestion des utilisateurs.
     */
    private final UserService userService;

    /**
     * Constructeur permettant l'injection du service utilisateur.
     *
     * @param userService service pour la gestion des utilisateurs
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retourne le profil complet de l'utilisateur authentifié.
     *
     * <p>Récupère l'utilisateur connecté grâce au token JWT, puis
     * charge les informations depuis la base de données.
     *
     * @param userDetails informations de l'utilisateur connecté extraites du JWT
     * @return réponse HTTP contenant les informations de l'utilisateur
     */
    @Operation(
            summary = "Récupérer le profil connecté",
            description = "Retourne les informations de l'utilisateur authentifié"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profil récupéré avec succès",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token JWT manquant ou invalide"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {

        // Récupération de l'email depuis le JWT
        String email = userDetails.getUsername();

        // Chargement des informations complètes de l'utilisateur
        User user = userService.findByEmail(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Profil récupéré avec succès", user));
    }
}