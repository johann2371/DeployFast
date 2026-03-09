package com.example.taskmanager.unit;

import com.example.taskmanager.dto.request.TaskRequest;
import com.example.taskmanager.dto.response.TaskResponse;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.exception.UnauthorizedException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.model.enums.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.impl.TaskServiceImpl;
import com.example.taskmanager.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Tests unitaires du service TaskServiceImpl
// Chaque méthode est testée de manière isolée avec Mockito
@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    // Mock du repository pour simuler la base de données
    @Mock
    private TaskRepository taskRepository;

    // Mock du service utilisateur
    @Mock
    private UserService userService;

    // Instance du service à tester avec les mocks injectés
    @InjectMocks
    private TaskServiceImpl taskService;

    // Objets réutilisables dans tous les tests
    private User mockUser;
    private Task mockTask;
    private TaskRequest mockRequest;

    // Initialise les objets avant chaque test
    @BeforeEach
    void setUp() {
        // Crée un utilisateur fictif pour les tests
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@test.com");
        mockUser.setName("Test User");

        // Crée une tâche fictive pour les tests
        mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setTitle("Tâche test");
        mockTask.setDescription("Description test");
        mockTask.setStatus(TaskStatus.TODO);
        mockTask.setOwner(mockUser);

        // Crée une requête fictive pour les tests
        mockRequest = new TaskRequest();
        mockRequest.setTitle("Nouvelle tâche");
        mockRequest.setDescription("Nouvelle description");
        mockRequest.setStatus(TaskStatus.TODO);
    }

    // Vérifie que la création d'une tâche fonctionne correctement
    @Test
    void createTask_ShouldReturnTaskResponse_WhenValidRequest() {

        // Simule le comportement du userService et du repository
        when(userService.findByEmail("test@test.com")).thenReturn(mockUser);
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        // Appelle la méthode à tester
        TaskResponse response = taskService.createTask(mockRequest, "test@test.com");

        // Vérifie que la réponse contient les bonnes données
        assertNotNull(response);
        assertEquals("Tâche test", response.getTitle());
        assertEquals(TaskStatus.TODO, response.getStatus());

        // Vérifie que save a bien été appelé une fois
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // Vérifie que la récupération d'une tâche inexistante lève une exception
    @Test
    void getTaskById_ShouldThrowException_WhenTaskNotFound() {

        // Simule une tâche introuvable en base
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        // Vérifie que l'exception correcte est levée
        assertThrows(TaskNotFoundException.class,
                () -> taskService.getTaskById(99L, "test@test.com"));
    }

    // Vérifie qu'un utilisateur ne peut pas accéder à la tâche d'un autre
    @Test
    void getTaskById_ShouldThrowException_WhenNotOwner() {

        // Simule une tâche appartenant à un autre utilisateur
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        // Appelle avec un email différent du propriétaire
        assertThrows(UnauthorizedException.class,
                () -> taskService.getTaskById(1L, "autre@test.com"));
    }

    // Vérifie que la suppression d'une tâche fonctionne correctement
    @Test
    void deleteTask_ShouldDeleteTask_WhenOwnerRequests() {

        // Simule la tâche trouvée en base
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        // Appelle la suppression avec le bon propriétaire
        taskService.deleteTask(1L, "test@test.com");

        // Vérifie que delete a bien été appelé une fois
        verify(taskRepository, times(1)).delete(mockTask);
    }

    // Vérifie que la pagination fonctionne lors de la récupération des tâches
    @Test
    void getAllTasks_ShouldReturnPagedTasks_WhenUserHasTasks() {

        // Simule une page de tâches
        Page<Task> mockPage = new PageImpl<>(List.of(mockTask));
        when(userService.findByEmail("test@test.com")).thenReturn(mockUser);
        when(taskRepository.findByOwnerId(eq(1L), any(PageRequest.class)))
                .thenReturn(mockPage);

        // Appelle la méthode avec pagination
        Page<TaskResponse> result = taskService.getAllTasks(
                "test@test.com", PageRequest.of(0, 10));

        // Vérifie que la page contient bien une tâche
        assertEquals(1, result.getTotalElements());
    }

    // Vérifie que la mise à jour d'une tâche fonctionne correctement
    @Test
    void updateTask_ShouldReturnUpdatedTask_WhenOwnerRequests() {

        // Simule la tâche trouvée et la sauvegarde
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        // Met à jour la tâche
        mockRequest.setTitle("Titre mis à jour");
        TaskResponse response = taskService.updateTask(
                1L, mockRequest, "test@test.com");

        // Vérifie que save a été appelé
        assertNotNull(response);
        verify(taskRepository, times(1)).save(any(Task.class));
    }
}
