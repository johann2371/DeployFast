package com.example.taskmanager.dto.response;

// Format uniforme de toutes les réponses de l'API
public class ApiResponse<T> {

    // Indique si la requête a réussi ou échoué
    private boolean success;

    // Message décrivant le résultat de l'opération
    private String message;

    // Données retournées (null en cas d'erreur)
    private T data;

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Méthode utilitaire pour une réponse de succès
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // Méthode utilitaire pour une réponse d'erreur
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
