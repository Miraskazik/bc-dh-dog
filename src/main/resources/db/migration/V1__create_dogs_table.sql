CREATE TABLE dogs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    customer_id UUID         NOT NULL,
    breed       VARCHAR(255),
    birth_date  DATE,
    notes       TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMPTZ
);

-- vyhledávání dle jména/plemene (case-insensitive contains)
CREATE INDEX idx_dogs_name  ON dogs (lower(name));
CREATE INDEX idx_dogs_breed ON dogs (lower(breed));
-- filtr na psy daného majitele
CREATE INDEX idx_dogs_customer_id ON dogs (customer_id);
-- běžné dotazy filtrují soft-deleted (deleted_at IS NULL)
CREATE INDEX idx_dogs_active ON dogs (deleted_at);
