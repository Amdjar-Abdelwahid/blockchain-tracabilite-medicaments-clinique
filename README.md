
# Tracemed - Système de Traçabilité des Médicaments

## Description
Tracemed est une plateforme backend robuste conçue pour suivre le cycle de vie des médicaments depuis leur production jusqu'à leur consommation. Elle intègre un simulateur de Blockchain pour garantir l'immutabilité et l'auditabilité de chaque événement logistique.

## Architecture Technique
*   **Langage & Framework** : Java 17, Spring Boot 3.
*   **Sécurité** : Spring Security, JWT (Stateless Authentication), BCrypt.
*   **Données** : Spring Data JPA, Hibernate, MySQL.
*   **Documentation** : OpenAPI / Swagger UI.
*   **Outils** : Maven, Lombok.

## Fonctionnalités Complètes du Système

### 1. Sécurité Avancée
*   **Authentification Sécurisée** : Connexion via JWT (JSON Web Token).
*   **Gestion des Rôles** : Contrôle d'accès basé sur les rôles (RBAC) pour `ADMIN`, `PHARMACIEN`, `TRANSPORTEUR`, `CLINIQUE`, `LABORATOIRE`.
*   **Inscription Utilisateurs** : Création de compte liée à une Organisation spécifique.

### 2. Gestion des Données Référentielles
*   **Organisations** : Gestion centralisée des acteurs (Hôpitaux, Pharmacies, Grossistes).
*   **Médicaments** : Catalogue national des produits (Code GTIN, DCI, Forme, Dosage).
*   **Lots de Production** : Suivi des lots (Numéro de lot, Date de fabrication/péremption, Quantité initiale).

### 3. Gestion Opérationnelle des Colis
*   **Création de Colis** : Initialisation de colis physiques associés à des lots de médicaments.
*   **Suivi de Statut** : Gestion des états (`CREE`, `EN_TRANSIT`, `LIVRE`, `STOCKE`, `CONSOMME`).
*   **Transferts** : Enregistrement des mouvements de stock entre organisations (Expédition/Réception).
*   **Historique** : Consultation de la chronologie complète des événements d'un colis.

### 4. Audit et Intégrité (Blockchain)
*   **Chaînage Cryptographique** : Chaque événement est haché (SHA-256) en incluant le hash de l'événement précédent, formant une chaîne inaltérable.
*   **Service d'Audit** : API permettant de vérifier mathématiquement l'intégrité de la chaîne d'un colis.
*   **Détection de Fraude** : Identification immédiate de toute modification rétroactive ("CORRUPTED").

## API Endpoints Principaux
*   **Auth** : `/api/auth/**` (Login, Register).
*   **Utilisateurs** : `/api/users/**` (CRUD Admin).
*   **Données** : `/api/organisations`, `/api/medicaments`, `/api/lots`.
*   **Métier** :
    *   `/api/colis/**` (Gestion des colis).
    *   `/api/transfert/**` (Expédier/Réceptionner).
    *   `/api/historique/**` (Chronologie).
    *   `/api/audit/{idColis}` (Vérification Blockchain).

## Installation
1.  **Pré-requis** : Java 17, Maven.
2.  **Configuration** : Vérifier `application.properties` pour la connexion BDD.
3.  **Lancement** :
    ```bash
    mvn spring-boot:run
    ```
4.  **Admin par défaut** : Username `admin`, Password `admin123`.

## Documentation Swagger
L'interface interactive de l'API est accessible après démarrage sur :
`http://localhost:8080/swagger-ui/index.html`
