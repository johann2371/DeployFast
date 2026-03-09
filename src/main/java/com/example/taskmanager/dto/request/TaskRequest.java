package com.example.taskmanager.dto.request;


import com.example.taskmanager.model.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Données envoyées par le client pour créer ou modifier une tâche
public class TaskRequest {

    // Titre obligatoire de la tâche
    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    // Description optionnelle de la tâche
    private String description;

    // Statut obligatoire (TODO, IN_PROGRESS, DONE)
    @NotNull(message = "Le statut est obligatoire")
    private TaskStatus status;

    public TaskRequest() {}

    public TaskRequest(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
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
}
