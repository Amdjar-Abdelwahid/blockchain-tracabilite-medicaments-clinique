# Tracemed - Traçabilité des Médicaments avec Blockchain Simulator

## Description
Tracemed est une application backend développée avec Spring Boot visant à assurer la traçabilité des colis de médicaments à travers la chaîne logistique (Laboratoire -> Transporteur -> Hôpital/Pharmacie). Elle utilise un mécanisme inspiré de la Blockchain pour garantir l'intégrité des événements.

## Technologies
*   **Java 17**
*   **Spring Boot 3.5.8**
*   **Spring Data JPA** (MySQL/H2)
*   **Spring Security** + **JWT** (JSON Web Tokens)
*   **Lombok**
*   **SpringDoc OpenAPI** (Swagger)

## Fonctionnalités Implémentées

### 1. Sécurité et Gestion des Utilisateurs
*   **Authentification JWT** : `POST /api/auth/login` (Retourne un Bearer Token).
*   **Inscription** : `POST /api/auth/register`.
*   **Rôles** : Gestion stricte via Enum (`ADMIN`, `PHARMACIEN`, `TRANSPORTEUR`, `CLINIQUE`, `LABORATOIRE`).
*   **Sécurité** : Protection des endpoints sensibles via `@PreAuthorize`.

### 2. Gestion des Données de Base (CRUD)
*   **Organisations** : Gestion des acteurs (Hôpitaux, Pharmacies, Labos).
*   **Médicaments** : Catalogue des médicaments (GTIN, Dosage, Forme).
*   **Lots** : Gestion des lots de production (Date péremption, Quantité).

### 3. Validation Chainée (Blockchain Simulator)
*   **AuditService** : Vérification de l'intégrité des colis via hachage chaîné.
*   **Détection d'Altération** : Le système signale si un événement a été modifié post-création.

## Installation et Démarrage
1.  Cloner le projet.
2.  Configurer la base de données dans `application.properties`.
3.  Lancer l'application :
    ```bash
    mvn spring-boot:run
    ```
4.  Un utilisateur administrateur est créé par défaut au démarrage :
    *   **User** : `admin`
    *   **Pass** : `admin123`

## Documentation API
Une fois l'application lancée, la documentation Swagger est disponible sur :
`http://localhost:8080/swagger-ui/index.html`
