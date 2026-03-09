package com.example.taskmanager.service.impl;

import com.example.taskmanager.dto.request.TaskRequest;
import com.example.taskmanager.dto.response.TaskResponse;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.exception.UnauthorizedException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.model.enums.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.interfaces.TaskService;
import com.example.taskmanager.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service de gestion des tâches.
 *
 * Ce service gère les opérations CRUD sur les tâches de l'utilisateur authentifié.
 * Il vérifie la propriété des tâches avant toute modification ou suppression.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskServiceImpl(TaskRepository taskRepository,
                           UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    @Override
    public TaskResponse createTask(TaskRequest request, String email) {
        logger.info("Création d'une nouvelle tâche pour l'utilisateur {}", email);

        User owner = userService.findByEmail(email);
        Task task = TaskMapper.toEntity(request, owner);

        Task savedTask = taskRepository.save(task);

        logger.info("Tâche '{}' créée avec succès pour l'utilisateur {}", savedTask.getTitle(), email);

        return TaskMapper.toResponse(savedTask);
    }

    @Override
    public Page<TaskResponse> getAllTasks(String email, Pageable pageable) {
        logger.info("Récupération de toutes les tâches pour l'utilisateur {}", email);

        User owner = userService.findByEmail(email);

        Page<TaskResponse> tasks = taskRepository.findByOwnerId(owner.getId(), pageable)
                .map(TaskMapper::toResponse);

        logger.debug("Nombre de tâches récupérées pour {} : {}", email, tasks.getTotalElements());

        return tasks;
    }

    @Override
    public Page<TaskResponse> getTasksByStatus(String email, TaskStatus status, Pageable pageable) {
        logger.info("Récupération des tâches avec le statut '{}' pour l'utilisateur {}", status, email);

        User owner = userService.findByEmail(email);

        Page<TaskResponse> tasks = taskRepository.findByOwnerIdAndStatus(owner.getId(), status, pageable)
                .map(TaskMapper::toResponse);

        logger.debug("Nombre de tâches filtrées récupérées pour {} : {}", email, tasks.getTotalElements());

        return tasks;
    }

    @Override
    public TaskResponse getTaskById(Long taskId, String email) {
        logger.info("Récupération de la tâche {} pour l'utilisateur {}", taskId, email);

        Task task = findTaskOrThrow(taskId);
        checkOwnership(task, email);

        logger.info("Tâche {} récupérée avec succès pour l'utilisateur {}", taskId, email);

        return TaskMapper.toResponse(task);
    }

    @Override
    public TaskResponse updateTask(Long taskId, TaskRequest request, String email) {
        logger.info("Mise à jour de la tâche {} pour l'utilisateur {}", taskId, email);

        Task task = findTaskOrThrow(taskId);
        checkOwnership(task, email);

        Task updatedTask = TaskMapper.updateEntity(task, request);
        Task savedTask = taskRepository.save(updatedTask);

        logger.info("Tâche {} mise à jour avec succès pour l'utilisateur {}", taskId, email);

        return TaskMapper.toResponse(savedTask);
    }

    @Override
    public void deleteTask(Long taskId, String email) {
        logger.info("Suppression de la tâche {} pour l'utilisateur {}", taskId, email);

        Task task = findTaskOrThrow(taskId);
        checkOwnership(task, email);

        taskRepository.delete(task);

        logger.info("Tâche {} supprimée avec succès pour l'utilisateur {}", taskId, email);
    }

    // Méthode privée : charge une tâche ou lève TaskNotFoundException
    private Task findTaskOrThrow(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    logger.error("Tâche introuvable : {}", taskId);
                    return new TaskNotFoundException(taskId);
                });
    }

    // Méthode privée : vérifie que la tâche appartient à l'utilisateur connecté
    private void checkOwnership(Task task, String email) {
        if (!task.getOwner().getEmail().equals(email)) {
            logger.warn("Accès non autorisé à la tâche {} pour l'utilisateur {}", task.getId(), email);
            throw new UnauthorizedException("Vous n'êtes pas autorisé à accéder à cette tâche");
        }
    }
}