package com.example.taskmanager.exception;

// Exception levée quand un email est déjà utilisé lors de l'inscription
public class UserAlreadyExistsException extends RuntimeException {

    // Email déjà existant en base
    private String email;

    public UserAlreadyExistsException(String email) {
        super("Un compte existe déjà avec l'email : " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}