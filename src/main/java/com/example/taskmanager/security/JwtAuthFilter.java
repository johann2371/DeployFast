package com.example.taskmanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

// Filtre exécuté une seule fois par requête pour valider le token JWT
// Intercepte chaque requête HTTP avant qu'elle n'atteigne le controller
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider,
                         UserDetailsServiceImpl userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Récupère le token depuis l'en-tête Authorization
        String token = extractTokenFromRequest(request);

        // Valide le token et authentifie l'utilisateur si valide
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // Extrait l'email depuis le token
            String email = jwtTokenProvider.getEmailFromToken(token);

            // Charge les détails de l'utilisateur depuis la base de données
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Crée l'objet d'authentification Spring Security
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            // Enregistre l'authentification dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Passe la requête au filtre suivant
        filterChain.doFilter(request, response);
    }

    // Extrait le token JWT depuis l'en-tête "Authorization: Bearer <token>"
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
