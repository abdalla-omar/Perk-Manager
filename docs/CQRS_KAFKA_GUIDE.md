# CQRS + Kafka Integration Guide

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Setup Instructions](#setup-instructions)
3. [API Endpoints](#api-endpoints)
4. [Testing](#testing)
5. [Deployment](#deployment)
6. [Demo Walkthrough](#demo-walkthrough)

---

## Architecture Overview

### What is CQRS?

**Command Query Responsibility Segregation (CQRS)** separates read and write operations into different models:

- **Commands** - Modify state (Create, Update, Delete)
- **Queries** - Read state (no side effects)

### Why CQRS + Kafka?

1. **Scalability** - Read and write sides scale independently
2. **Performance** - Optimize read models for queries
3. **Eventual Consistency** - Async event processing via Kafka
4. **Audit Trail** - All domain events captured
5. **Loose Coupling** - Components communicate via events

### System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                       CLIENT REQUEST                         │
└───────────────────────┬─────────────────────────────────────┘
                        │
            ┌───────────▼───────────┐
            │   CqrsController      │
            └───────────┬───────────┘
                        │
         ┌──────────────▼──────────────┐
         │      IS IT A QUERY?         │
         └──────┬──────────────┬───────┘
                │              │
           NO   │              │  YES
                │              │
    ┌───────────▼────┐    ┌───▼────────────┐
    │ CommandHandler │    │ QueryHandler   │
    │                │    │                │
    │ • Validate     │    │ • Read from DB │
    │ • Execute      │    │ • Transform to │
    │ • Save to DB   │    │   ReadModel    │
    │ • Publish Event│    │ • Return       │
    └───────┬────────┘    └────────────────┘
            │
            │ Events
            │
    ┌───────▼─────────┐
    │  Kafka Topics   │
    │                 │
    │ • perk.created  │
    │ • perk.upvoted  │
    │ • user.reg...   │
    └───────┬─────────┘
            │
            │ Subscribe
            │
    ┌───────▼──────────┐
    │ Event Consumers  │
    │                  │
    │ • Update caches  │
    │ • Update search  │
    │ • Send notifs    │
    │ • Analytics      │
    └──────────────────┘
```

---

## Project Structure

```
src/main/java/com/example/perkmanager/
├── command/              # Command DTOs (write operations)
│   ├── CreateUserCommand.java
│   ├── CreatePerkCommand.java
│   ├── UpvotePerkCommand.java
│   ├── DownvotePerkCommand.java
│   └── AddMembershipCommand.java
│
├── query/                # Query DTOs (read operations)
│   ├── GetAllPerksQuery.java
│   ├── GetPerksByVotesQuery.java
│   ├── GetPerksByMembershipQuery.java
│   ├── GetPerksMatchingProfileQuery.java
│   └── GetUserProfileQuery.java
│
├── event/                # Domain Events (published to Kafka)
│   ├── PerkCreatedEvent.java
│   ├── PerkUpvotedEvent.java
│   ├── PerkDownvotedEvent.java
│   ├── UserRegisteredEvent.java
│   ├── MembershipAddedEvent.java
│   └── MembershipRemovedEvent.java
│
├── dto/                  # Read Models (optimized for queries)
│   ├── PerkReadModel.java
│   └── UserProfileReadModel.java
│
├── service/              # Command & Query Handlers
│   ├── PerkCommandHandler.java
│   ├── UserCommandHandler.java
│   ├── PerkQueryHandler.java
│   ├── UserQueryHandler.java
│   └── EventPublisher.java
│
├── consumer/             # Kafka Event Consumers
│   ├── PerkEventConsumer.java
│   └── UserEventConsumer.java
│
├── controller/           # REST Controllers
│   ├── CqrsController.java       # NEW: CQRS endpoints
│   └── AppController.java        # OLD: Legacy endpoints
│
├── config/               # Configuration
│   └── KafkaConfig.java
│
├── model/                # JPA Entities (unchanged)
├── repository/           # Data Access (unchanged)
└── enumerations/         # Enums (unchanged)
```

---

## Setup Instructions

### Prerequisites

1. **Java 21**
2. **Maven 3.8+**
3. **Apache Kafka 3.x** (see installation below)
4. **H2 Database** (in-memory, included)

### Step 1: Install and Start Kafka

#### Option A: Docker (Recommended for Demo)

```bash
# Start Kafka with Zookeeper
docker-compose up -d

# Or manually:
docker run -d --name zookeeper -p 2181:2181 zookeeper:3.8
docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:7.4.0
```

#### Option B: Local Installation

```bash
# Download Kafka
wget https://downloads.apache.org/kafka/3.6.0/kafka_2.13-3.6.0.tgz
tar -xzf kafka_2.13-3.6.0.tgz
cd kafka_2.13-3.6.0

# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka (in new terminal)
bin/kafka-server-start.sh config/server.properties
```

### Step 2: Build and Run Application

```bash
# Clone repo
git clone <repo>
cd Perk-Manager

# Build
mvn clean install

# Run
mvn spring-boot:run

# Or run JAR
java -jar target/PerkManager-0.0.1-SNAPSHOT.jar
```

### Step 3: Verify Kafka Topics

```bash
# List topics (should see perk.created, perk.upvoted, etc.)
kafka-topics.sh --bootstrap-server localhost:9092 --list

# Monitor events in real-time
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic perk.created --from-beginning
```

---

## API Endpoints

### Base URL: `/api/cqrs`

### Commands (Write Operations)

#### 1. Create User
```bash
POST /api/cqrs/users
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}

Response: 201 Created
{
  "userId": 1,
  "email": "john@example.com",
  "profileId": 1,
  "memberships": []
}
```

#### 2. Create Perk
```bash
POST /api/cqrs/perks
Content-Type: application/json

{
  "userId": 1,
  "description": "50% off movies with Aeroplan",
  "membership": "AIRMILES",
  "product": "MOVIES",
  "startDate": "2025-01-01",
  "endDate": "2025-12-31"
}

Response: 201 Created
{
  "id": 1,
  "description": "50% off movies with Aeroplan",
  "membership": "AIRMILES",
  "product": "MOVIES",
  "upvotes": 0,
  "downvotes": 0,
  "netScore": 0,
  "active": true
}
```

#### 3. Upvote Perk
```bash
POST /api/cqrs/perks/1/upvote

Response: 200 OK
{
  "id": 1,
  "upvotes": 1,
  "netScore": 1,
  ...
}
```

#### 4. Downvote Perk
```bash
POST /api/cqrs/perks/1/downvote

Response: 200 OK
```

#### 5. Add Membership
```bash
POST /api/cqrs/users/1/memberships
Content-Type: application/json

{
  "membership": "AIRMILES"
}

Response: 200 OK
```

### Queries (Read Operations)

#### 1. Get All Perks
```bash
GET /api/cqrs/perks

Response: 200 OK
[
  {
    "id": 1,
    "description": "...",
    "upvotes": 5,
    "netScore": 3,
    "active": true
  }
]
```

#### 2. Get Perks Sorted by Votes
```bash
GET /api/cqrs/perks/by-votes

Response: Perks sorted by upvote count (descending)
```

#### 3. Get Perks by Membership
```bash
GET /api/cqrs/perks/by-membership/AIRMILES

Response: Perks filtered by AIRMILES membership
```

#### 4. Get Personalized Perks
```bash
GET /api/cqrs/users/1/matching-perks

Response: Perks matching user's memberships
```

#### 5. Get User Profile
```bash
GET /api/cqrs/users/1/profile

Response: 200 OK
{
  "userId": 1,
  "email": "john@example.com",
  "profileId": 1,
  "memberships": ["AIRMILES", "VISA"]
}
```

---

## Testing

### Run All Tests
```bash
mvn test
```

### Run CQRS Integration Tests
```bash
mvn test -Dtest=CqrsIntegrationTest
```

### Manual Testing with cURL

```bash
# 1. Create user
curl -X POST http://localhost:8080/api/cqrs/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"pass123"}'

# 2. Create perk (use userId from step 1)
curl -X POST http://localhost:8080/api/cqrs/perks \
  -H "Content-Type: application/json" \
  -d '{
    "userId":1,
    "description":"Test perk",
    "membership":"VISA",
    "product":"DINING",
    "startDate":"2025-01-01",
    "endDate":"2025-12-31"
  }'

# 3. Upvote perk
curl -X POST http://localhost:8080/api/cqrs/perks/1/upvote

# 4. Get all perks
curl http://localhost:8080/api/cqrs/perks

# 5. Get perks by votes
curl http://localhost:8080/api/cqrs/perks/by-votes
```

### Monitor Kafka Events

```bash
# Terminal 1: Watch perk.created events
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic perk.created --from-beginning

# Terminal 2: Watch perk.upvoted events
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic perk.upvoted --from-beginning

# Now create a perk via API - you'll see events in real-time!
```

---

## Deployment

### Local Development
- Kafka: `localhost:9092`
- App: `localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`



---

## Demo Walkthrough (12 Minutes)

### Part 1: Architecture Explanation (2 min)
- Show CQRS diagram
- Explain Command vs Query separation
- Highlight Kafka's role in event streaming

### Part 2: Code Walkthrough (4 min)
- **Commands**: `CreatePerkCommand.java` → `PerkCommandHandler.java`
- **Events**: `PerkCreatedEvent.java` → `EventPublisher.java`
- **Consumers**: `PerkEventConsumer.java`
- **Queries**: `GetAllPerksQuery.java` → `PerkQueryHandler.java`
- **Controller**: `CqrsController.java` routes to handlers

### Part 3: Live Demo (5 min)

```bash
# 1. Show Kafka is running
kafka-topics.sh --list --bootstrap-server localhost:9092

# 2. Start monitoring events
kafka-console-consumer.sh --topic perk.created --from-beginning

# 3. Create user via API (Postman/curl)
# Show event in Kafka consumer

# 4. Create perk
# Show event in Kafka consumer

# 5. Upvote perk multiple times
# Show upvote events streaming

# 6. Query perks by votes
# Show sorted results

# 7. Show application logs
# Highlight: "Published PerkCreatedEvent", "Consumed PerkCreatedEvent"
```

### Part 4: Testing (1 min)
- Run integration tests: `mvn test -Dtest=CqrsIntegrationTest`
- Show test passing with embedded Kafka

---

## Key Benefits Demonstrated

1. **Separation of Concerns**: Commands and queries are independent
2. **Scalability**: Read and write sides can scale separately
3. **Async Processing**: Events processed independently via Kafka
4. **Audit Trail**: All events captured in Kafka topics
5. **Testability**: Easy to test with embedded Kafka
6. **Cloud-Ready**: Works with Azure Event Hubs

---

## Troubleshooting

### Kafka Connection Issues
```bash
# Check if Kafka is running
nc -zv localhost 9092

# Check Kafka logs
docker logs kafka
```

### Topic Not Created
```bash
# Manually create topic
kafka-topics.sh --create --topic perk.created \
  --bootstrap-server localhost:9092 \
  --partitions 3 --replication-factor 1
```

### Application Won't Start
```bash
# Check application.properties
# Ensure Kafka bootstrap server is correct
spring.kafka.bootstrap-servers=localhost:9092
```

---

## References

- [Spring Kafka Documentation](https://spring.io/projects/spring-kafka)
- [CQRS Pattern](https://martinfowler.com/bliki/CQRS.html)
- [Apache Kafka Docs](https://kafka.apache.org/documentation/)
- [Azure Event Hubs](https://docs.microsoft.com/azure/event-hubs/)

---

**Prepared for Milestone 2 Demo**
*Perk Manager - CQRS + Kafka Integration*
