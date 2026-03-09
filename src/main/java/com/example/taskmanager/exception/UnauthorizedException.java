package com.example.taskmanager.exception;

// Exception levée quand un utilisateur tente d'accéder à une ressource
// qui ne lui appartient pas
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
