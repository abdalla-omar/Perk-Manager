# Perk Manager - Models & Database

## UML Class Diagram

**View diagram:** [uml-class-diagram.mmd](./uml-class-diagram.mmd)

```mermaid
classDiagram
    class AppUser {
        -Long id
        -String email
        -String password
        -Profile profile
        +AppUser()
        +AppUser(String email, String password)
        +Long getId()
        +String getEmail()
        +void setEmail(String email)
        +String getPassword()
        +void setPassword(String password)
        +Profile getProfile()
        +void setProfile(Profile profile)
    }

    class Profile {
        -Long id
        -Set~String~ memberships
        +Profile()
        +boolean hasMembership(String membership)
        +void addMembership(String membership)
        +void removeMembership(String membership)
        +Long getId()
        +Set~String~ getMemberships()
    }

    class Perk {
        -Long id
        -String description
        -MembershipType membership
        -ProductType product
        -int upvotes
        -int downvotes
        -LocalDate startDate
        -LocalDate endDate
        -AppUser postedBy
        +Perk()
        +Perk(String, MembershipType, ProductType, LocalDate, LocalDate, AppUser)
        +void upvote()
        +void downvote()
        +Long getId()
        +String getDescription()
        +MembershipType getMembership()
        +ProductType getProduct()
        +int getUpvotes()
        +int getDownvotes()
        +LocalDate getStartDate()
        +LocalDate getEndDate()
        +AppUser getPostedBy()
    }

    class MembershipType {
        <<enumeration>>
        CAA
        VISA
        MASTERCARD
        AMEX
        AIRMILES
    }

    class ProductType {
        <<enumeration>>
        MOVIES
        HOTELS
        FLIGHTS
        CARS
        DINING
    }

    AppUser "1" -- "1" Profile : has
    AppUser "1" -- "0..*" Perk : posts
    Perk "*" -- "1" MembershipType : uses
    Perk "*" -- "1" ProductType : categorizes
```

## Entity Relationship Diagram

**View diagram:** [er-diagram.mmd](./er-diagram.mmd)

```mermaid
erDiagram
    APP_USER ||--|| PROFILE : "has"
    APP_USER ||--o{ PERK : "posts"
    PROFILE ||--o{ PROFILE_MEMBERSHIP : "contains"

    APP_USER {
        BIGINT id PK
        VARCHAR email UK
        VARCHAR password
        BIGINT profile_id FK
    }

    PROFILE {
        BIGINT id PK
    }

    PROFILE_MEMBERSHIP {
        BIGINT profile_id FK
        VARCHAR membership
    }

    PERK {
        BIGINT id PK
        TEXT description
        VARCHAR membership
        VARCHAR product
        INT upvotes
        INT downvotes
        DATE start_date
        DATE end_date
        BIGINT posted_by_id FK
    }
```

## SQL Schema

```sql
-- TABLE: profile
CREATE TABLE profile (
  id BIGSERIAL PRIMARY KEY
);

-- TABLE: app_user
CREATE TABLE app_user (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(320) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  profile_id BIGINT UNIQUE,
  CONSTRAINT fk_app_user_profile
    FOREIGN KEY (profile_id) REFERENCES profile(id)
      ON UPDATE CASCADE ON DELETE SET NULL
);

-- TABLE: profile_membership (ElementCollection for Profile.memberships)
CREATE TABLE profile_membership (
  profile_id BIGINT NOT NULL,
  membership VARCHAR(64) NOT NULL,
  PRIMARY KEY (profile_id, membership),
  CONSTRAINT fk_profile_membership_profile
    FOREIGN KEY (profile_id) REFERENCES profile(id)
      ON UPDATE CASCADE ON DELETE CASCADE
);

-- TABLE: perk
CREATE TABLE perk (
  id BIGSERIAL PRIMARY KEY,
  description TEXT NOT NULL,
  membership VARCHAR(32),  -- MembershipType enum as string
  product    VARCHAR(32),  -- ProductType enum as string
  upvotes    INT NOT NULL DEFAULT 0,
  downvotes  INT NOT NULL DEFAULT 0,
  start_date DATE,
  end_date   DATE,
  posted_by_id BIGINT,
  CONSTRAINT fk_perk_user
    FOREIGN KEY (posted_by_id) REFERENCES app_user(id)
      ON UPDATE CASCADE ON DELETE SET NULL
);

-- Helpful indexes
CREATE INDEX idx_perk_membership    ON perk (membership);
CREATE INDEX idx_perk_product       ON perk (product);
CREATE INDEX idx_perk_posted_by     ON perk (posted_by_id);
CREATE INDEX idx_perk_upvotes_desc  ON perk (upvotes DESC);
```
