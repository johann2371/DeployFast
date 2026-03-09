package com.example.taskmanager.security;

import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

// Implémentation de UserDetailsService pour charger les utilisateurs
// depuis la base de données lors de l'authentification Spring Security
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Charge un utilisateur par son email pour Spring Security
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // Recherche l'utilisateur en base, lève une exception s'il n'existe pas
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur introuvable avec l'email : " + email));

        // Convertit le rôle de l'utilisateur en autorité Spring Security
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(user.getRole().name());

        // Retourne l'objet UserDetails utilisé par Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}
