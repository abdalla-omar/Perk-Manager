# Quick Start Guide - CQRS + Kafka Demo

## 5-Minute Setup

### Step 1: Start Kafka (1 min)
```bash
# Using Docker Compose (recommended)
docker-compose up -d

# Wait for Kafka to be ready
docker-compose logs -f kafka | grep "started"
```

### Step 2: Start Application (1 min)
```bash
# Build and run
mvn clean install
mvn spring-boot:run

# Or use your IDE to run ApplicationClass.java
```

### Step 3: Verify Setup (30 seconds)
```bash
# Check health
curl http://localhost:8080/api/cqrs/health
# Expected: "CQRS System is operational"

# Check Kafka topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
# Expected: perk.created, perk.upvoted, etc.
```

### Step 4: Run Demo Scenario (2.5 min)

#### Scenario: Create user, post perk, upvote, and query

```bash
# 1. Create User
curl -X POST http://localhost:8080/api/cqrs/users \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@example.com","password":"demo123"}'

# Response: {"userId":1,"email":"demo@example.com",...}

# 2. Create Perk (replace userId with ID from step 1)
curl -X POST http://localhost:8080/api/cqrs/perks \
  -H "Content-Type: application/json" \
  -d '{
    "userId":1,
    "description":"50% off movies with Aeroplan",
    "membership":"AIRMILES",
    "product":"MOVIES",
    "startDate":"2025-01-01",
    "endDate":"2025-12-31"
  }'

# Response: {"id":1,"upvotes":0,...}

# 3. Upvote the perk 3 times
curl -X POST http://localhost:8080/api/cqrs/perks/1/upvote
curl -X POST http://localhost:8080/api/cqrs/perks/1/upvote
curl -X POST http://localhost:8080/api/cqrs/perks/1/upvote

# 4. Query perks sorted by votes
curl http://localhost:8080/api/cqrs/perks/by-votes

# Response: [{"id":1,"upvotes":3,"netScore":3,...}]

# 5. Add membership to profile
curl -X POST http://localhost:8080/api/cqrs/users/1/memberships \
  -H "Content-Type: application/json" \
  -d '{"membership":"AIRMILES"}'

# 6. Get personalized perks
curl http://localhost:8080/api/cqrs/users/1/matching-perks

# Response: Perks matching user's AIRMILES membership
```

## Monitor Events in Real-Time

```bash
# Terminal 1: Watch all perk events
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic perk.created \
  --from-beginning

# Terminal 2: Watch vote events
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic perk.upvoted \
  --from-beginning

# Now run the demo scenario above - events will appear in real-time!
```

## Access Kafka UI (Optional)
```bash
# Open browser
http://localhost:8090

# View:
# - Topics
# - Messages
# - Consumer groups
# - Brokers
```

## Run Tests
```bash
# Unit + Integration tests (includes embedded Kafka)
mvn test

# Specific CQRS test
mvn test -Dtest=CqrsIntegrationTest
```

## Cleanup
```bash
# Stop application: Ctrl+C

# Stop Kafka
docker-compose down

# Remove volumes (optional)
docker-compose down -v
```

---

## Troubleshooting

**Problem**: Kafka connection refused
```bash
# Solution: Ensure Kafka is running
docker ps | grep kafka
docker-compose restart kafka
```

**Problem**: Topics not created
```bash
# Solution: Manually create topics
docker exec -it kafka kafka-topics --create \
  --topic perk.created \
  --bootstrap-server localhost:9092 \
  --partitions 3 --replication-factor 1
```

**Problem**: Application won't start
```bash
# Check: Is port 8080 available?
lsof -i :8080

# Check: Application logs
mvn spring-boot:run -X
```
