package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.TaskRequest;
import com.example.taskmanager.dto.response.ApiResponse;
import com.example.taskmanager.dto.response.TaskResponse;
import com.example.taskmanager.model.enums.TaskStatus;
import com.example.taskmanager.service.interfaces.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

// Gère les requêtes HTTP liées aux tâches
// Toutes les routes sont protégées par JWT via SecurityConfig
@Tag(name = "Tâches", description = "Gestion complète des tâches (CRUD)")
@SecurityRequirement(name = "Bearer Token")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // POST /api/tasks → Crée une nouvelle tâche pour l'utilisateur connecté
    @Operation(
            summary = "Créer une nouvelle tâche",
            description = "Crée une tâche pour l'utilisateur connecté"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Tâche créée avec succès",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Données invalides"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token JWT manquant ou invalide"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Récupère l'email de l'utilisateur connecté depuis le token JWT
        String email = userDetails.getUsername();

        TaskResponse task = taskService.createTask(request, email);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tâche créée avec succès", task));
    }

    // GET /api/tasks → Récupère toutes les tâches de l'utilisateur connecté
    @Operation(
            summary = "Lister toutes les tâches",
            description = "Retourne les tâches de l'utilisateur connecté avec pagination"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tâches récupérées avec succès"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token JWT manquant ou invalide"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getAllTasks(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Numéro de page (commence à 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre de tâches par page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri")
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        String email = userDetails.getUsername();

        // Crée l'objet de pagination trié par date de création décroissante
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(sortBy).descending());

        Page<TaskResponse> tasks = taskService.getAllTasks(email, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Tâches récupérées avec succès", tasks));
    }

    // GET /api/tasks/status/{status} → Filtre les tâches par statut
    @Operation(
            summary = "Filtrer les tâches par statut",
            description = "Retourne les tâches filtrées par statut (TODO, IN_PROGRESS, DONE)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tâches filtrées récupérées"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token JWT manquant ou invalide"
            )
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getTasksByStatus(
            @Parameter(description = "Statut de la tâche : TODO, IN_PROGRESS, DONE")
            @PathVariable TaskStatus status,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String email = userDetails.getUsername();

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        Page<TaskResponse> tasks = taskService.getTasksByStatus(
                email, status, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Tâches filtrées récupérées", tasks));
    }

    // GET /api/tasks/{id} → Récupère une tâche par son identifiant
    @Operation(
            summary = "Récupérer une tâche par ID",
            description = "Retourne les détails d'une tâche spécifique"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tâche récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Tâche introuvable"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Accès refusé"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @Parameter(description = "Identifiant de la tâche")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        TaskResponse task = taskService.getTaskById(id, email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Tâche récupérée avec succès", task));
    }

    // PUT /api/tasks/{id} → Met à jour une tâche existante
    @Operation(
            summary = "Modifier une tâche",
            description = "Met à jour les informations d'une tâche existante"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tâche mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Tâche introuvable"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Accès refusé"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @Parameter(description = "Identifiant de la tâche à modifier")
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        TaskResponse updatedTask = taskService.updateTask(id, request, email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Tâche mise à jour avec succès", updatedTask));
    }

    // DELETE /api/tasks/{id} → Supprime une tâche par son identifiant
    @Operation(
            summary = "Supprimer une tâche",
            description = "Supprime définitivement une tâche par son identifiant"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tâche supprimée avec succès"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Tâche introuvable"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Accès refusé"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @Parameter(description = "Identifiant de la tâche à supprimer")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        taskService.deleteTask(id, email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Tâche supprimée avec succès", null));
    }
}