package com.example.taskmanager.model;

import com.example.taskmanager.model.enums.TaskStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

// Entité représentant une tâche appartenant à un utilisateur
@Entity
@Table(name = "tasks")
public class Task {

    // Identifiant unique généré automatiquement
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titre court et obligatoire de la tâche
    @Column(nullable = false)
    private String title;

    // Description optionnelle pour plus de contexte
    private String description;

    // Statut actuel de la tâche (TODO, IN_PROGRESS, DONE)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    // Date de création, initialisée automatiquement à la persistance
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Date de dernière modification, mise à jour automatiquement
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Propriétaire de la tâche (clé étrangère vers users)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // Constructeur vide requis par JPA
    public Task() {}

    // Constructeur complet
    public Task(Long id, String title, String description, TaskStatus status,
                LocalDateTime createdAt, LocalDateTime updatedAt, User owner) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    // Initialise les dates à la première sauvegarde
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // Met à jour la date de modification à chaque changement
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
