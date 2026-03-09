package com.example.taskmanager.repository;

import com.example.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// Accès aux données de l'entité User via Spring Data JPA
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Recherche un utilisateur par son email (utilisé lors de la connexion)
    Optional<User> findByEmail(String email);

    // Vérifie si un email est déjà utilisé (utilisé lors de l'inscription)
    boolean existsByEmail(String email);
}
