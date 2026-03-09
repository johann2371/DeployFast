package com.example.taskmanager.unit;

import com.example.taskmanager.model.User;
import com.example.taskmanager.model.enums.Role;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Tests unitaires du service UserServiceImpl
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        // Crée un utilisateur fictif pour les tests
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@test.com");
        mockUser.setName("Test User");
        mockUser.setRole(Role.ROLE_USER);
    }

    // Vérifie que la recherche par email retourne l'utilisateur correct
    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {

        // Simule un utilisateur trouvé en base
        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(mockUser));

        // Appelle la méthode à tester
        User result = userService.findByEmail("test@test.com");

        // Vérifie que l'utilisateur retourné est le bon
        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
        assertEquals("Test User", result.getName());
    }

    // Vérifie qu'une exception est levée si l'email n'existe pas
    @Test
    void findByEmail_ShouldThrowException_WhenEmailNotFound() {

        // Simule un utilisateur introuvable
        when(userRepository.findByEmail("inconnu@test.com"))
                .thenReturn(Optional.empty());

        // Vérifie que l'exception correcte est levée
        assertThrows(UsernameNotFoundException.class,
                () -> userService.findByEmail("inconnu@test.com"));
    }

    // Vérifie que existsByEmail retourne true si l'email existe
    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {

        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertTrue(userService.existsByEmail("test@test.com"));
    }

    // Vérifie que existsByEmail retourne false si l'email n'existe pas
    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailNotFound() {

        when(userRepository.existsByEmail("inconnu@test.com")).thenReturn(false);

        assertFalse(userService.existsByEmail("inconnu@test.com"));
    }
}
