package com.example.taskmanager.dto.response;

import com.example.taskmanager.model.enums.TaskStatus;
import java.time.LocalDateTime;

// Données d'une tâche retournées au client
public class TaskResponse {

    // Identifiant unique de la tâche
    private Long id;

    // Titre de la tâche
    private String title;

    // Description de la tâche
    private String description;

    // Statut actuel de la tâche
    private TaskStatus status;

    // Date de création de la tâche
    private LocalDateTime createdAt;

    // Date de dernière modification
    private LocalDateTime updatedAt;

    // Nom du propriétaire de la tâche
    private String ownerName;

    public TaskResponse() {}

    public TaskResponse(Long id, String title, String description, TaskStatus status,
                        LocalDateTime createdAt, LocalDateTime updatedAt, String ownerName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.ownerName = ownerName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}
