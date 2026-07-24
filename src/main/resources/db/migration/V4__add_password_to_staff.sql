ALTER TABLE staff
    ADD COLUMN password VARCHAR(100) NOT NULL DEFAULT '';

-- BCrypt hash of "123456" for all existing seed staff accounts.
UPDATE staff SET password = '$2a$10$PHIyNdL09MU1NOvpEZ0hvezkzO1Oc8VdwfwJiXkOldxNoxU5PNPR6';

ALTER TABLE staff ALTER COLUMN password DROP DEFAULT;
