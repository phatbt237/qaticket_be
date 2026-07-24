CREATE TABLE refresh_token (
    id         BIGSERIAL PRIMARY KEY,
    staff_id   BIGINT       NOT NULL REFERENCES staff (id),
    token      VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP    NOT NULL,
    revoked    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT now()
);
CREATE INDEX idx_refresh_token_staff_id ON refresh_token (staff_id);
CREATE INDEX idx_refresh_token_token ON refresh_token (token);
