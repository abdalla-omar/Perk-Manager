# Perk-Manager

A Spring Boot application for managing perks and memberships.

### Team (Group 3)
- Ohiorenua Ajayi-Isuku
- Ethan Ashworth
- Abdallah Omar
- Mithushan Ravichandramohan
- Quentin Weir

### Core Features
- Profiles with memberships/cards
- Post perks tied to a membership + product
- Voting (surface useful, bury incorrect)
- Search & sort (votes or expiry)

## Requirements

- **Java 21** or higher
- Maven 3.9+ (included via Maven wrapper)

## Setup

1. Ensure you have Java 21 installed on your system
2. Run the application using Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   
   On Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

## Building

To build the project:
```bash
./mvnw clean package
```