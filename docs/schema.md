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
