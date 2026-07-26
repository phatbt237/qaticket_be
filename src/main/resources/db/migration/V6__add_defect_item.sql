-- ==========================================================================
-- Add DEFECT_ITEM: a sub-level under DEFECT (category) representing the
-- specific error, each carrying its allowed severity (minor/major).
-- ==========================================================================

-- ------------------------------------------------------------ DEFECT_ITEM ---
CREATE TABLE defect_item (
    id           BIGSERIAL PRIMARY KEY,
    defect_id    BIGINT      NOT NULL REFERENCES defect (id) ON DELETE CASCADE,
    code         VARCHAR(50) UNIQUE,
    name_en      VARCHAR(150),
    name_vi      VARCHAR(150) NOT NULL,
    allow_minor  BOOLEAN     NOT NULL DEFAULT TRUE,
    allow_major  BOOLEAN     NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_defect_item_severity CHECK (allow_minor OR allow_major)
);
CREATE INDEX idx_defect_item_defect_id ON defect_item (defect_id);

-- Backfill: one default (both-severity) DefectItem per existing Defect category,
-- so existing categories remain selectable without manual re-seeding.
INSERT INTO defect_item (defect_id, code, name_en, name_vi, allow_minor, allow_major)
SELECT id, code, name_en, name_vi, TRUE, TRUE FROM defect;

-- A few extra sample sub-items to demonstrate fixed vs. user-selectable severity.
INSERT INTO defect_item (defect_id, code, name_en, name_vi, allow_minor, allow_major) VALUES
  ((SELECT id FROM defect WHERE code = 'DF001'), 'DF001-01', 'Open seam < 3cm', 'Bung đường may dưới 3cm', TRUE, FALSE),
  ((SELECT id FROM defect WHERE code = 'DF001'), 'DF001-02', 'Open seam >= 3cm', 'Bung đường may từ 3cm trở lên', FALSE, TRUE),
  ((SELECT id FROM defect WHERE code = 'DF003'), 'DF003-01', 'Broken stitch (multiple spots)', 'Đứt chỉ nhiều vị trí', TRUE, TRUE);

-- ---------------------------------------------------- QA_TICKET_DEFECT alter ---
ALTER TABLE qa_ticket_defect ADD COLUMN defect_item_id BIGINT REFERENCES defect_item (id);
ALTER TABLE qa_ticket_defect ADD COLUMN severity VARCHAR(10);

-- Point existing rows at the backfilled 1:1 DefectItem for their original Defect,
-- defaulting severity to MAJOR (both allowed on the backfilled items).
UPDATE qa_ticket_defect qtd
SET defect_item_id = di.id,
    severity = 'MAJOR'
FROM defect_item di
WHERE di.defect_id = qtd.defect_id;

ALTER TABLE qa_ticket_defect ALTER COLUMN defect_item_id SET NOT NULL;
ALTER TABLE qa_ticket_defect ALTER COLUMN severity SET NOT NULL;
ALTER TABLE qa_ticket_defect DROP COLUMN defect_id;

CREATE INDEX idx_qa_ticket_defect_defect_item_id ON qa_ticket_defect (defect_item_id);
