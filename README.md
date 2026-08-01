# Olympic Management System

Plateforme de gestion des Jeux Olympiques de Dakar — API REST + Web Service SOAP pour la gestion des athlètes, disciplines, épreuves, résultats, médailles et statistiques.

## Stack technique

**Backend** (`backend/`)
- Java 21, Spring Boot 3.3, Maven
- Spring Data JPA / Hibernate
- MySQL 8 (utilisé en local : MariaDB/MySQL via XAMPP/WAMP, cf. `.env.example`)
- Spring Web (REST) + Spring Web Services (SOAP, contract-first)
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito, Spring-WS Test

**Base de données**
- `database/olympic_management.sql` : schéma complet, source de vérité (le backend utilise `hibernate.ddl-auto=validate`, il ne modifie jamais le schéma)
- `docker-compose.yml` : MySQL 8 + phpMyAdmin

## Démarrage rapide

```bash
# 1. Base de données (Docker)
cp .env.example .env
docker compose up -d
# MySQL      -> localhost:3306
# phpMyAdmin -> http://localhost:8081

# 2. Backend
cd backend
mvn spring-boot:run
# API REST  -> http://localhost:8080/api/v1/...
# Swagger   -> http://localhost:8080/swagger-ui.html
# SOAP      -> http://localhost:8080/ws
```

## Architecture backend

Organisation par fonctionnalité (feature packages), pas par couche technique globale :

```
backend/src/main/java/com/olympic/dakar/
  athlete/       # CRUD athlètes + recherche multicritère (Specification)
  discipline/    # CRUD disciplines + athlètes d'une discipline
  event/         # CRUD épreuves + recherche par date/discipline
  result/        # Résultats, attribution automatique des médailles
  medal/         # Tableau des médailles (classement olympique)
  dashboard/     # Statistiques globales (comptages, classement par points)
  soap/          # Web service SOAP (voir section dédiée ci-dessous)
  common/        # Exceptions globales (RFC 7807), pagination
  config/        # Swagger, CORS
```

Chaque module REST suit : `Controller → Service → Repository`, avec DTO dédiés (jamais d'entité JPA exposée directement), validation Bean Validation, et gestion d'erreurs centralisée (`400`/`404`/`409`/`422`).

## API REST — aperçu

| Ressource | Endpoints |
|---|---|
| Athlètes | `GET/POST /api/v1/athletes`, `GET/PUT/PATCH/DELETE /api/v1/athletes/{id}` (recherche multicritère + pagination) |
| Disciplines | `GET/POST /api/v1/disciplines`, `GET/PUT/PATCH/DELETE /api/v1/disciplines/{id}`, `GET /api/v1/disciplines/{id}/athletes` |
| Épreuves | `GET/POST /api/v1/events`, `GET/PUT/PATCH/DELETE /api/v1/events/{id}` (recherche par date/discipline) |
| Résultats | `POST /api/v1/results`, `GET/PUT/DELETE /api/v1/results/{id}`, `GET /api/v1/events/{id}/results`, `GET /api/v1/events/{id}/podium` |
| Médailles | `GET /api/v1/medals/medal-table` |
| Dashboard | `GET /api/v1/dashboard/athletes/count`, `/countries/count`, `/medals`, `/countries/ranking`, `/countries/medalists` |

Documentation interactive complète : `http://localhost:8080/swagger-ui.html`.

## Web Service SOAP

Le sujet impose deux types de consommateurs : des applications REST, et **un système d'information historique qui ne consomme que du SOAP**. Ce second besoin est couvert par un web service SOAP **contract-first** (Spring Web Services), strictement en **consultation** (le système historique lit des données, il n'en écrit pas — l'écriture reste réservée à l'API REST).

### Contrat

- XSD : [`backend/src/main/resources/xsd/olympic-management.xsd`](backend/src/main/resources/xsd/olympic-management.xsd) — source de vérité du contrat
- Namespace : `http://olympic.dakar.com/soap/olympic-management`
- Endpoint : `http://localhost:8080/ws`
- WSDL (généré automatiquement à partir du XSD, vérifié accessible en HTTP 200) : `http://localhost:8080/ws/olympic-management.wsdl`

### Opérations exposées

| Opération | Description |
|---|---|
| `getAthlete` | Consulter un athlète par id |
| `getAthleteResults` | Résultats d'un athlète (toutes épreuves confondues) |
| `getEventResults` | Résultats d'une épreuve |
| `getNationMedalHistory` | Historique des médailles d'une nation |

Détails, exemples de requêtes/réponses XML testés en réel (SoapUI/Postman)

### Choix d'implémentation

- **XSD → Java hand-maintenu** plutôt que génération XJC via plugin Maven : le XSD reste la source de vérité du contrat, mais les classes JAXB (`com.olympic.dakar.soap.jaxb`) sont écrites à la main pour fiabiliser la compilation (évite les aléas de configuration d'un plugin de génération de code sur Java 21) tout en restant strictement alignées sur le schéma.
- Le endpoint SOAP (`OlympicManagementEndpoint`) réutilise directement les services métier existants (`AthleteService`, `ResultService`) — aucune logique dupliquée entre REST et SOAP, seule la couche de sérialisation XML (`SoapMapper`) est spécifique.
- Les erreurs métier (ex. athlète introuvable) sont traduites en **SOAP Fault** standard (`SoapFaultMappingExceptionResolver`), pas en exception non gérée.

### Tester le SOAP

```bash
curl http://localhost:8080/ws/olympic-management.wsdl
```

Ou importer directement l'URL du WSDL dans SoapUI (génère les requêtes automatiquement), ou utiliser les exemples XML prêts à l'emploi avec Postman (POST vers `/ws`, `Content-Type: text/xml`).

## Tests

```bash
cd backend
mvn test
```

- Tests unitaires (JUnit 5 + Mockito) : logique métier isolée (attribution des médailles, tri du tableau des médailles, calcul du classement par points, patch partiel).
- Tests d'intégration (MockMvc + H2) : CRUD complet, validation, codes d'erreur, recherche, pagination pour chaque module REST.
- Tests SOAP (Spring-WS Test / `MockWebServiceClient`) : les 4 opérations + cas de SOAP Fault.

## Base de données

Le schéma (`database/olympic_management.sql`) est la source de vérité — le backend est configuré en `hibernate.ddl-auto=validate` : il vérifie la cohérence entre les entités JPA et les tables, mais ne les modifie jamais. Toute évolution du modèle doit être répercutée dans ce fichier SQL.

Identifiants de connexion configurables via variables d'environnement (`.env`, voir `.env.example`) : `DB_USERNAME` / `DB_PASSWORD` pour le backend, `MYSQL_ROOT_PASSWORD` / `MYSQL_DATABASE` / `MYSQL_USER` / `MYSQL_PASSWORD` pour le conteneur MySQL.

## État du projet

- ✅ Backend REST complet (Athlètes, Disciplines, Épreuves, Résultats, Médailles, Dashboard)
- ✅ Web Service SOAP (consultation)
- ✅ Base de données (schéma + Docker Compose)
- ⏳ Frontend (React/Vite) — à venir
- ⏳ Collection Postman consolidée — à venir
