-- ==========================================================================
-- Add QA_TICKET_SPEC_IMAGE: optional measurement/spec reference images
-- attached directly to a ticket (not tied to a specific defect/location).
-- ==========================================================================

CREATE TABLE qa_ticket_spec_image (
    id           BIGSERIAL PRIMARY KEY,
    qa_ticket_id BIGINT       NOT NULL REFERENCES qa_ticket (id) ON DELETE CASCADE,
    image_url    VARCHAR(500) NOT NULL,
    uploaded_at  TIMESTAMP    NOT NULL DEFAULT now()
);
CREATE INDEX idx_qa_ticket_spec_image_qa_ticket_id ON qa_ticket_spec_image (qa_ticket_id);
