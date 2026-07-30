-- ==========================================================================
-- Add AQL sampling plan fields to QA_TICKET, used only when inspection_stage
-- is FINAL: aql_level + qty_size drive a lookup into aql_sampling_plan to
-- derive sampling_size, and actual defect counts are compared against that
-- plan's accept/reject thresholds to derive inspection_result.
-- Nullable: only applicable to FINAL-stage tickets.
-- ==========================================================================

ALTER TABLE qa_ticket ADD COLUMN aql_level VARCHAR(10);
ALTER TABLE qa_ticket ADD COLUMN qty_size INTEGER;
ALTER TABLE qa_ticket ADD COLUMN sampling_size INTEGER;
ALTER TABLE qa_ticket ADD COLUMN actual_major_defects INTEGER;
ALTER TABLE qa_ticket ADD COLUMN actual_minor_defects INTEGER;
ALTER TABLE qa_ticket ADD COLUMN inspection_result VARCHAR(20);
