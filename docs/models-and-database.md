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
        -List~Perk~ perks
        +AppUser()
        +AppUser(String email, String password)
        +Long getId()
        +String getEmail()
        +void setEmail(String email)
        +String getPassword()
        +void setPassword(String password)
        +Profile getProfile()
        +void setProfile(Profile profile)
        +List~Perk~ getPerks()
        +void setPerks(List~Perk~ perks)
        +void addPerk(Perk perk)
        +void removePerk(Perk perk)
    }

    class Profile {
        -Long id
        -Set~String~ memberships
        +Profile()
        +void addMembership(String membership)
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
        +void setDescription(String description)
        +MembershipType getMembership()
        +void setMembership(MembershipType membership)
        +ProductType getProduct()
        +void setProduct(ProductType product)
        +int getUpvotes()
        +void setUpvotes(int upvotes)
        +int getDownvotes()
        +void setDownvotes(int downvotes)
        +LocalDate getStartDate()
        +void setStartDate(LocalDate startDate)
        +LocalDate getEndDate()
        +void setEndDate(LocalDate endDate)
        +AppUser getPostedBy()
        +void setPostedBy(AppUser postedBy)
        +String toString()
    }

    class PerkVote {
        -Long id
        -AppUser user
        -Perk perk
        -VoteType voteType
        +PerkVote()
        +PerkVote(AppUser user, Perk perk, VoteType voteType)
        +Long getId()
        +AppUser getUser()
        +Perk getPerk()
        +VoteType getVoteType()
        +void setVoteType(VoteType voteType)
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

    class VoteType {
        <<enumeration>>
        UPVOTE
        DOWNVOTE
    }

    AppUser "1" -- "1" Profile : has
    AppUser "1" -- "0..*" Perk : posts
    AppUser "0..*" -- "0..*" Perk : saves
    AppUser "1" -- "0..*" PerkVote : casts
    Perk "1" -- "0..*" PerkVote : receives
    Perk "*" -- "1" MembershipType : uses
    Perk "*" -- "1" ProductType : categorizes
    PerkVote "*" -- "1" VoteType : has
```

## Entity Relationship Diagram

**View diagram:** [er-diagram.mmd](./er-diagram.mmd)

```mermaid
erDiagram
    APP_USER ||--|| PROFILE : "has"
    APP_USER ||--o{ PERK : "posts"
    APP_USER }o--o{ PERK : "saves"
    APP_USER ||--o{ PERK_VOTE : "casts"
    PERK ||--o{ PERK_VOTE : "receives"
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

    USER_PERKS {
        BIGINT user_id FK
        BIGINT perk_id FK
    }

    PERK_VOTE {
        BIGINT id PK
        BIGINT user_id FK
        BIGINT perk_id FK
        VARCHAR vote_type
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

-- TABLE: user_perks (Join table for AppUser.perks many-to-many relationship)
CREATE TABLE user_perks (
  user_id BIGINT NOT NULL,
  perk_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, perk_id),
  CONSTRAINT fk_user_perks_user
    FOREIGN KEY (user_id) REFERENCES app_user(id)
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_user_perks_perk
    FOREIGN KEY (perk_id) REFERENCES perk(id)
      ON UPDATE CASCADE ON DELETE CASCADE
);

-- TABLE: perk_vote (Tracks individual user votes on perks)
CREATE TABLE perk_vote (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  perk_id BIGINT NOT NULL,
  vote_type VARCHAR(16) NOT NULL,  -- VoteType enum as string (UPVOTE, DOWNVOTE)
  CONSTRAINT fk_perk_vote_user
    FOREIGN KEY (user_id) REFERENCES app_user(id)
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_perk_vote_perk
    FOREIGN KEY (perk_id) REFERENCES perk(id)
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT uk_perk_vote_user_perk
    UNIQUE (user_id, perk_id)  -- One vote per user per perk
);

-- Helpful indexes
CREATE INDEX idx_perk_membership    ON perk (membership);
CREATE INDEX idx_perk_product       ON perk (product);
CREATE INDEX idx_perk_posted_by     ON perk (posted_by_id);
CREATE INDEX idx_perk_upvotes_desc  ON perk (upvotes DESC);
CREATE INDEX idx_perk_vote_user     ON perk_vote (user_id);
CREATE INDEX idx_perk_vote_perk     ON perk_vote (perk_id);
```
