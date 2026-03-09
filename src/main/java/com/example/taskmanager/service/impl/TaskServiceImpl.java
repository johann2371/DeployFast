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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

// Implémentation du service de gestion des tâches
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskServiceImpl(TaskRepository taskRepository,
                           UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    // Crée une nouvelle tâche pour l'utilisateur connecté
    @Override
    public TaskResponse createTask(TaskRequest request, String email) {

        // Charge l'utilisateur connecté depuis la base
        User owner = userService.findByEmail(email);

        // Convertit le DTO en entité Task
        Task task = TaskMapper.toEntity(request, owner);

        // Sauvegarde la tâche en base de données
        Task savedTask = taskRepository.save(task);

        return TaskMapper.toResponse(savedTask);
    }

    // Récupère toutes les tâches de l'utilisateur avec pagination
    @Override
    public Page<TaskResponse> getAllTasks(String email, Pageable pageable) {

        // Charge l'utilisateur connecté
        User owner = userService.findByEmail(email);

        // Récupère les tâches paginées de cet utilisateur
        return taskRepository.findByOwnerId(owner.getId(), pageable)
                .map(TaskMapper::toResponse);
    }

    // Récupère les tâches filtrées par statut avec pagination
    @Override
    public Page<TaskResponse> getTasksByStatus(String email,
                                               TaskStatus status,
                                               Pageable pageable) {
        // Charge l'utilisateur connecté
        User owner = userService.findByEmail(email);

        // Récupère les tâches filtrées par statut
        return taskRepository.findByOwnerIdAndStatus(
                        owner.getId(), status, pageable)
                .map(TaskMapper::toResponse);
    }

    // Récupère une tâche par son id en vérifiant qu'elle appartient à l'utilisateur
    @Override
    public TaskResponse getTaskById(Long taskId, String email) {

        // Charge la tâche ou lève une exception si introuvable
        Task task = findTaskOrThrow(taskId);

        // Vérifie que la tâche appartient bien à l'utilisateur connecté
        checkOwnership(task, email);

        return TaskMapper.toResponse(task);
    }

    // Met à jour une tâche existante
    @Override
    public TaskResponse updateTask(Long taskId, TaskRequest request, String email) {

        // Charge la tâche ou lève une exception si introuvable
        Task task = findTaskOrThrow(taskId);

        // Vérifie que la tâche appartient bien à l'utilisateur connecté
        checkOwnership(task, email);

        // Met à jour les champs de la tâche avec les nouvelles données
        Task updatedTask = TaskMapper.updateEntity(task, request);

        // Sauvegarde les modifications en base
        return TaskMapper.toResponse(taskRepository.save(updatedTask));
    }

    // Supprime une tâche par son identifiant
    @Override
    public void deleteTask(Long taskId, String email) {

        // Charge la tâche ou lève une exception si introuvable
        Task task = findTaskOrThrow(taskId);

        // Vérifie que la tâche appartient bien à l'utilisateur connecté
        checkOwnership(task, email);

        // Supprime la tâche de la base de données
        taskRepository.delete(task);
    }

    // Méthode privée : charge une tâche ou lève TaskNotFoundException
    private Task findTaskOrThrow(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    // Méthode privée : vérifie que la tâche appartient à l'utilisateur connecté
    private void checkOwnership(Task task, String email) {
        if (!task.getOwner().getEmail().equals(email)) {
            throw new UnauthorizedException(
                    "Vous n'êtes pas autorisé à accéder à cette tâche");
        }
    }
}

