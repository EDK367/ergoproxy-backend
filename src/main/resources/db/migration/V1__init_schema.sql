CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================
-- ROLES
-- =========================
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- =========================
-- USERS
-- =========================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,

    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,

    password TEXT NOT NULL,

    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    phone_number VARCHAR(20),

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,

    metadata TEXT,

    role_id BIGINT NOT NULL,

    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id)
        REFERENCES roles (id)
        ON DELETE RESTRICT
);

-- =========================
-- NOTES
-- =========================
CREATE TABLE notes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    title VARCHAR(255) NOT NULL,
    content TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,

    enabled BOOLEAN DEFAULT TRUE,
    important BOOLEAN DEFAULT FALSE,

    user_id UUID NOT NULL,

    CONSTRAINT fk_notes_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

-- =========================
-- SHARED NOTES
-- =========================
CREATE TABLE shared_notes (
    id BIGSERIAL PRIMARY KEY,

    note_id UUID NOT NULL,
    user_shared_id UUID NOT NULL,

    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_shared_note
        FOREIGN KEY (note_id)
        REFERENCES notes (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_shared_user
        FOREIGN KEY (user_shared_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

-- =========================
-- LOGS INFO
-- =========================
CREATE TABLE logs_info (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    ip VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    user_id UUID,

    CONSTRAINT fk_logs_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE SET NULL
);