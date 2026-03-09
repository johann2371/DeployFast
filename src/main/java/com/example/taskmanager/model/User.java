package com.example.taskmanager.model;

import com.example.taskmanager.model.enums.Role;
import jakarta.persistence.*;
import java.util.List;

// Entité représentant un utilisateur de l'application
@Entity
@Table(name = "users")
public class User {

    // Identifiant unique généré automatiquement
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Email unique utilisé comme identifiant de connexion
    @Column(nullable = false, unique = true)
    private String email;

    // Mot de passe encodé en BCrypt, jamais stocké en clair
    @Column(nullable = false)
    private String password;

    // Nom complet de l'utilisateur
    @Column(nullable = false)
    private String name;

    // Rôle déterminant les permissions de l'utilisateur
    @Enumerated(EnumType.STRING)
    private Role role;

    // Liste des tâches de l'utilisateur, supprimées en cascade
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Task> tasks;

    // Constructeur vide requis par JPA
    public User() {}

    // Constructeur complet
    public User(Long id, String email, String password, String name, Role role, List<Task> tasks) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.tasks = tasks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
