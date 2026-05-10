-- Medify Supabase PostgreSQL Schema
-- Run this in Supabase SQL Editor.

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    profile_image_url TEXT,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_token VARCHAR(120),
    verification_token_expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users (LOWER(email));
CREATE INDEX IF NOT EXISTS idx_users_verification_token ON users (verification_token);

CREATE TABLE IF NOT EXISTS medications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    medicine_name VARCHAR(160) NOT NULL,
    brand_name VARCHAR(160),
    dosage VARCHAR(80) NOT NULL,
    purpose VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price NUMERIC(12, 2) NOT NULL CHECK (price > 0),
    purchase_date DATE NOT NULL CHECK (purchase_date <= CURRENT_DATE),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_medications_user_id ON medications (user_id);
CREATE INDEX IF NOT EXISTS idx_medications_purchase_date ON medications (purchase_date DESC);
CREATE INDEX IF NOT EXISTS idx_medications_name ON medications (LOWER(medicine_name));

-- Optional trigger to keep updated_at current.
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_users_updated_at ON users;
CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_medications_updated_at ON medications;
CREATE TRIGGER trg_medications_updated_at
BEFORE UPDATE ON medications
FOR EACH ROW EXECUTE FUNCTION set_updated_at();
