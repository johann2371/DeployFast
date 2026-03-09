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

// Gère les requêtes HTTP liées au profil utilisateur
@Tag(name = "Utilisateurs", description = "Gestion du profil utilisateur")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users/me → Retourne le profil de l'utilisateur connecté
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

        // Récupère l'email depuis le token JWT
        String email = userDetails.getUsername();

        // Charge le profil complet depuis la base de données
        User user = userService.findByEmail(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Profil récupéré avec succès", user));
    }
}
