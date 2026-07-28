-- ==========================================================================
-- Add TYPE to QA_TICKET_SPEC_IMAGE: classifies each spec image into one of
-- 4 categories (approved sample / size spec / packing / hangtag & label).
-- Nullable: existing rows predate this classification and aren't backfilled
-- (no reliable source to infer their type from); the API requires `type` on
-- every new/updated spec image going forward.
-- ==========================================================================

ALTER TABLE qa_ticket_spec_image ADD COLUMN type VARCHAR(30);
