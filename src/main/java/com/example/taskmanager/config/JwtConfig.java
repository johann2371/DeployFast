package com.example.taskmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuration permettant de charger les propriétés
 * liées aux tokens JWT depuis le fichier de configuration de l'application
 * (application.yml ou application.properties).
 *
 * <p>Cette classe utilise l’annotation {@code @ConfigurationProperties}
 * avec le préfixe "jwt". Cela signifie que toutes les propriétés
 * commençant par "jwt" dans le fichier de configuration seront automatiquement
 * associées aux attributs de cette classe.
 *
 * <p>Exemple de configuration dans un fichier application.yml :
 *
 * <pre>
 * jwt:
 *   secret: maCleSecrete
 *   expiration: 86400000
 * </pre>
 *
 * <p>Ces informations sont utilisées dans les composants de sécurité
 * de l'application pour générer et valider les tokens JWT.
 *
 * <p>L'utilisation d'une classe de configuration dédiée permet de
 * centraliser les paramètres de sécurité et de faciliter la maintenance
 * et la gestion des configurations.
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * Clé secrète utilisée pour signer et vérifier les tokens JWT.
     * Cette clé doit rester confidentielle.
     * En production, il est recommandé de la stocker dans une
     * variable d’environnement plutôt que directement dans le code.
     */
    private String secret;

    /**
     * Durée de validité du token JWT exprimée en millisecondes.
     *
     * Exemple :
     * 86400000 = 24 heures
     */
    private long expiration;

    /**
     * Constructeur par défaut requis par Spring pour instancier
     * automatiquement cette classe de configuration.
     */
    public JwtConfig() {}

    /**
     * Retourne la clé secrète utilisée pour signer les tokens JWT.
     *
     * @return la clé secrète JWT
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Définit la clé secrète JWT provenant du fichier de configuration.
     *
     * @param secret la clé secrète utilisée pour signer les tokens
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Retourne la durée de validité du token JWT.
     *
     * @return durée d'expiration en millisecondes
     */
    public long getExpiration() {
        return expiration;
    }

    /**
     * Définit la durée de validité du token JWT.
     *
     * @param expiration durée d'expiration en millisecondes
     */
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}