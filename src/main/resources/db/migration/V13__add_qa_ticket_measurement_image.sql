-- ==========================================================================
-- Add QA_TICKET_MEASUREMENT_IMAGE: a separate, flat (no type) set of images
-- attached directly to a ticket, distinct from QA_TICKET_SPEC_IMAGE and not
-- restricted to any inspection_stage.
-- ==========================================================================

CREATE TABLE qa_ticket_measurement_image (
    id           BIGSERIAL PRIMARY KEY,
    qa_ticket_id BIGINT       NOT NULL REFERENCES qa_ticket (id) ON DELETE CASCADE,
    image_url    VARCHAR(500) NOT NULL,
    uploaded_at  TIMESTAMP    NOT NULL DEFAULT now()
);
CREATE INDEX idx_qa_ticket_measurement_image_qa_ticket_id ON qa_ticket_measurement_image (qa_ticket_id);
