ALTER TABLE qa_ticket
    ADD COLUMN exported    BOOLEAN   NOT NULL DEFAULT FALSE,
    ADD COLUMN exported_at TIMESTAMP NULL;

CREATE INDEX idx_qa_ticket_exported ON qa_ticket (exported);
