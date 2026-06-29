# BadWallet - Examen Design Pattern L3 S2 2026

Système de portefeuille mobile avec deux microservices Spring Boot.

## Architecture

| Service | Port | Description |
|---|---|---|
| `badwallet-api` | 8080 | API principale - gestion des wallets et transactions |
| `payment-service` | 8081 | Service externe - gestion des factures ISM et WOYAFAL |

## Stack technique

- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

## Prérequis

- Java 17
- Maven 3.9+
- PostgreSQL

## Installation

### 1. Cloner le projet
```bash
git clone https://github.com/yacine004/badwallet.git
cd badwallet
```

### 2. Créer les bases de données PostgreSQL
```sql
CREATE DATABASE badwallet_db;
CREATE DATABASE payment_db;
```

### 3. Configurer les credentials PostgreSQL

Dans `badwallet-api/src/main/resources/application.properties` :
```properties
spring.datasource.username=VOTRE_USER
spring.datasource.password=VOTRE_PASSWORD
```

Dans `payment-service/src/main/resources/application.properties` :
```properties
spring.datasource.username=VOTRE_USER
spring.datasource.password=VOTRE_PASSWORD
```

### 4. Lancer les services

Terminal 1 :
```bash
cd badwallet-api
mvn spring-boot:run
```

Terminal 2 :
```bash
cd payment-service
mvn spring-boot:run
```

### 5. Seeder les données
```bash
# Seeder les wallets
POST http://localhost:8080/api/wallets/seed?numWallets=10&eventsPerWallet=100

# Seeder les factures
POST http://localhost:8081/api/factures/seed?numWallets=10
```

## Endpoints

### Wallets (port 8080)

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/wallets/seed` | Seeder la base de données |
| POST | `/api/wallets` | Créer un portefeuille |
| GET | `/api/wallets?page=0&size=10` | Lister les portefeuilles |
| GET | `/api/wallets/{phone}` | Consulter un portefeuille |
| GET | `/api/wallets/{phone}/balance` | Consulter le solde |
| POST | `/api/wallets/{id}/deposit` | Effectuer un dépôt |
| POST | `/api/wallets/withdraw` | Effectuer un retrait |
| POST | `/api/wallets/transfer` | Effectuer un transfert |
| POST | `/api/wallets/pay` | Payer une facture du mois |
| POST | `/api/wallets/pay-factures` | Payer des factures spécifiques |
| GET | `/api/wallets/{phone}/transactions` | Historique des transactions |

### Proxy Factures (port 8080)

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/external/factures/{code}/current` | Factures impayées du mois |
| GET | `/api/external/factures/{code}/current?unite=WOYAFAL` | Filtrées par service |
| GET | `/api/external/factures/{code}/periode?debut=&fin=` | Par période |

## Stratégie Git

- `main` : code stable en production
- `develop` : branche d'intégration
- `feature/*` : une branche par endpoint

## Tests

Utiliser le fichier `test.http` à la racine du projet avec l'extension **REST Client** de VS Code.