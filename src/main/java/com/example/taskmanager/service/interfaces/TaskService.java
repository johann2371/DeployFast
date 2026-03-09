package com.example.taskmanager.service.interfaces;

import com.example.taskmanager.dto.request.TaskRequest;
import com.example.taskmanager.dto.response.TaskResponse;
import com.example.taskmanager.model.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Contrat du service de gestion des tâches
// Définit toutes les opérations CRUD disponibles sur les tâches
public interface TaskService {

    // Crée une nouvelle tâche pour l'utilisateur connecté
    TaskResponse createTask(TaskRequest request, String email);

    // Récupère toutes les tâches de l'utilisateur connecté avec pagination
    Page<TaskResponse> getAllTasks(String email, Pageable pageable);

    // Récupère les tâches filtrées par statut avec pagination
    Page<TaskResponse> getTasksByStatus(String email, TaskStatus status, Pageable pageable);

    // Récupère une tâche par son identifiant
    TaskResponse getTaskById(Long taskId, String email);

    // Met à jour une tâche existante
    TaskResponse updateTask(Long taskId, TaskRequest request, String email);

    // Supprime une tâche par son identifiant
    void deleteTask(Long taskId, String email);
}
