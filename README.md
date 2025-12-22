# Car Booking Microservices

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023-green?style=flat-square&logo=spring)](https://spring.io/projects/spring-cloud)
[![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-Event_Driven-black?style=flat-square&logo=apachekafka)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=flat-square&logo=docker)](https://www.docker.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Relational-336791?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![MongoDB](https://img.shields.io/badge/MongoDB-NoSQL-47A248?style=flat-square&logo=mongodb)](https://www.mongodb.com/)

A scalable, fault-tolerant **distributed car rental platform** implementing Microservices Architecture.

The project moves away from monolithic design to loosely coupled services that communicate asynchronously via **Apache Kafka** and synchronously via REST. It features a complete infrastructure suite including **Service Discovery (Eureka)**, **API Gateway**, and **Centralized Secret Management (Vault)**.

## Key Features

+ **Event-Driven Architecture (EDA)**: Services are decoupled using Apache Kafka.
  * *Example*: When a `Booking` is created, an event is fired. The `CarService` listens to this event to lock the car availability without direct HTTP dependency.


+ **API Gateway (Spring Cloud Gateway)**:
  * Single entry point for all client requests.
  * Centralized **JWT Authentication** filter.
  * Request routing and load balancing.


+ **Service Discovery**:
  * **Netflix Eureka** allows services to find each other dynamically without hardcoded hostnames.


+ **Polyglot Persistence**:
  * **PostgreSQL** for transactional data (Users, Bookings, Inventory).
  * **MongoDB** for unstructured car metadata (Reviews, Descriptions).


+ **Security First**:
  * **HashiCorp Vault** manages database credentials and JWT secrets, injecting them into containers at runtime.



## Tech Stack

| Component | Technology |
| --- | --- |
| **Languages** | Java 21 |
| **Framework** | Spring Boot 3.3, Spring Cloud 2023 |
| **Messaging** | Apache Kafka, Zookeeper |
| **Databases** | PostgreSQL 16, MongoDB 7.0 |
| **Infrastructure** | Docker, Docker Compose, HashiCorp Vault |
| **Build Tool** | Maven (Multi-module project) |

## Services Breakdown

| Service | Port | Database | Description |
| --- | --- | --- | --- |
| **Gateway Service** | `8888` | - | Routes requests, validates JWT, serves static UI. |
| **Eureka Server** | `8761` | - | Service registry. |
| **User Service** | `8081` | Postgres | User registration, authentication logic. |
| **Car Service** | `8082` | Postgres | Manages car inventory and basic info. |
| **Booking Service** | `8083` | Postgres | Handles reservation lifecycle. |
| **Car Details** | `8084` | MongoDB | Manages extended info (reviews, images). |

## Installation & Running

The entire infrastructure is containerized. You do not need to install Java or Databases locally.

### Prerequisites

* Docker & Docker Compose

### Steps

1. **Clone the repository**
```bash
git clone [https://github.com/your-username/car-booking-microservices.git](https://github.com/your-username/car-booking-microservices.git)
cd car-booking-microservices
```


2. **Build the artifacts**
This uses the Maven Wrapper to build JARs for all services.
```bash
./mvnw clean package -DskipTests
```


3. **Start the System**
```bash
docker-compose up -d --build
```


> **Note:** his will spin up ~11 containers (Kafka, Vault, Postgres, Mongo, Redis, Eureka, Gateway, and 4 Microservices). Ensure you have allocated enough RAM to Docker (rec: 4GB+).


4. **Verify Deployment**
* **Eureka Dashboard**: [http://localhost:8761](https://www.google.com/search?q=http://localhost:8761) — Wait until all services appear here.
* **Main Application**: [http://localhost:8888](https://www.google.com/search?q=http://localhost:8888)



## 🧪 Testing

Each microservice has its own test suite. To run tests for a specific module:

```bash
./mvnw test -pl booking-service
```
