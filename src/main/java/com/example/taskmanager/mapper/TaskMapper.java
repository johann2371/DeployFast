package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.request.TaskRequest;
import com.example.taskmanager.dto.response.TaskResponse;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.model.enums.TaskStatus;

// Convertit les entités Task en objets DTO et vice versa
// Sépare la couche de présentation de la couche de données
public class TaskMapper {

    // Convertit une entité Task en TaskResponse pour le client
    public static TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();

        // Identifiant unique de la tâche
        response.setId(task.getId());

        // Titre de la tâche
        response.setTitle(task.getTitle());

        // Description de la tâche
        response.setDescription(task.getDescription());

        // Statut actuel de la tâche
        response.setStatus(task.getStatus());

        // Date de création
        response.setCreatedAt(task.getCreatedAt());

        // Date de dernière modification
        response.setUpdatedAt(task.getUpdatedAt());

        // Nom du propriétaire de la tâche
        response.setOwnerName(task.getOwner().getName());

        return response;
    }

    // Convertit un TaskRequest en entité Task pour la création
    public static Task toEntity(TaskRequest request, User owner) {
        Task task = new Task();

        // Titre envoyé par le client
        task.setTitle(request.getTitle());

        // Description envoyée par le client
        task.setDescription(request.getDescription());

        // Statut envoyé par le client, par défaut TODO si null
        task.setStatus(request.getStatus() != null
                ? request.getStatus()
                : TaskStatus.TODO);

        // Propriétaire de la tâche (utilisateur connecté)
        task.setOwner(owner);

        return task;
    }

    // Met à jour une entité Task existante avec les données du TaskRequest
    public static Task updateEntity(Task task, TaskRequest request) {

        // Met à jour le titre si fourni
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }

        // Met à jour la description si fournie
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        // Met à jour le statut si fourni
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        return task;
    }
}

