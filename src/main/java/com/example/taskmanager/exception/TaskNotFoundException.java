package com.example.taskmanager.exception;

// Exception levée quand une tâche n'est pas trouvée en base de données
public class TaskNotFoundException extends RuntimeException {

    // Identifiant de la tâche introuvable
    private Long taskId;

    public TaskNotFoundException(Long taskId) {
        super("Tâche introuvable avec l'id : " + taskId);
        this.taskId = taskId;
    }

    public Long getTaskId() {
        return taskId;
    }
}
