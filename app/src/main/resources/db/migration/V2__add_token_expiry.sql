-- V2: Add per-token expiry to password reset tokens
-- Tokens older than 1 hour are invalid regardless of the nightly sweep.
ALTER TABLE "user"
    ADD COLUMN password_reset_token_expires_at TIMESTAMPTZ;
