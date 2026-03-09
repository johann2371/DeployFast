package com.example.taskmanager.config;

import com.example.taskmanager.security.JwtAuthFilter;
import com.example.taskmanager.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Classe principale de configuration de la sécurité de l'application.
 *
 * <p>Cette classe configure les règles de sécurité de l’API REST en utilisant
 * Spring Security. Elle définit notamment :
 *
 * <ul>
 *     <li>Les routes accessibles sans authentification</li>
 *     <li>Les routes protégées nécessitant un token JWT</li>
 *     <li>Le filtre JWT chargé d'intercepter les requêtes</li>
 *     <li>La gestion des mots de passe avec BCrypt</li>
 *     <li>La configuration de l'AuthenticationManager</li>
 * </ul>
 *
 * <p>L'application utilise une authentification basée sur les tokens JWT,
 * ce qui signifie que le serveur ne conserve pas de session HTTP.
 * Chaque requête doit contenir un token valide pour accéder aux ressources protégées.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Filtre chargé d'intercepter les requêtes HTTP afin de vérifier
     * la présence et la validité du token JWT.
     */
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Service permettant de charger les informations d’un utilisateur
     * à partir de la base de données.
     */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Constructeur permettant l'injection des dépendances nécessaires
     * à la configuration de la sécurité.
     *
     * @param jwtAuthFilter filtre de validation des tokens JWT
     * @param userDetailsService service de récupération des utilisateurs
     */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          UserDetailsServiceImpl userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configure la chaîne de filtres de sécurité de l'application.
     *
     * <p>Cette méthode définit :
     * <ul>
     *     <li>Les routes accessibles sans authentification</li>
     *     <li>Les routes nécessitant une authentification</li>
     *     <li>La désactivation de CSRF pour les API REST</li>
     *     <li>La gestion stateless des sessions</li>
     *     <li>L'ajout du filtre JWT dans la chaîne de sécurité</li>
     * </ul>
     *
     * @param http objet de configuration de sécurité HTTP
     * @return la chaîne de filtres de sécurité configurée
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactive la protection CSRF car l'application utilise des tokens JWT
                .csrf(csrf -> csrf.disable())

                // Configuration des règles d'accès aux différentes routes
                .authorizeHttpRequests(auth -> auth
                        // Routes publiques pour l'inscription et la connexion
                        .requestMatchers("/api/auth/**").permitAll()

                        // Accès public à la documentation Swagger de l'API
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()

                        // Toutes les autres requêtes nécessitent une authentification
                        .anyRequest().authenticated()
                )

                // Configure l'application en mode stateless (sans session)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Ajoute le filtre JWT avant le filtre standard d'authentification
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean utilisé pour encoder les mots de passe des utilisateurs.
     *
     * <p>BCrypt est recommandé car il applique un algorithme de hachage sécurisé
     * avec salage automatique pour protéger les mots de passe stockés
     * dans la base de données.
     *
     * @return encodeur de mot de passe BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure l'AuthenticationManager utilisé par Spring Security
     * pour authentifier les utilisateurs.
     *
     * <p>Cette méthode indique à Spring Security :
     * <ul>
     *     <li>Quel service utiliser pour charger les utilisateurs</li>
     *     <li>Quel encodeur utiliser pour vérifier les mots de passe</li>
     * </ul>
     *
     * @param http configuration de sécurité HTTP
     * @return instance de l'AuthenticationManager
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws Exception {

        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        // Configuration du service utilisateur et de l'encodeur de mot de passe
        builder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        return builder.build();
    }
}