package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Accès aux données de l'entité Task via Spring Data JPA
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Récupère toutes les tâches d'un utilisateur avec pagination
    Page<Task> findByOwnerId(Long ownerId, Pageable pageable);

    // Récupère les tâches d'un utilisateur filtrées par statut
    Page<Task> findByOwnerIdAndStatus(Long ownerId, TaskStatus status, Pageable pageable);

    // Vérifie si une tâche appartient bien à un utilisateur donné
    boolean existsByIdAndOwnerId(Long taskId, Long ownerId);
}