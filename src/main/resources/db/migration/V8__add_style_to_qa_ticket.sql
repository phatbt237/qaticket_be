-- ==========================================================================
-- Add STYLE_ID to QA_TICKET: style defaults from the selected PO's style but
-- is independently editable (a ticket's actual style can differ from its PO).
-- ==========================================================================

ALTER TABLE qa_ticket ADD COLUMN style_id BIGINT REFERENCES style (id);
CREATE INDEX idx_qa_ticket_style_id ON qa_ticket (style_id);

-- Backfill existing tickets from their PO's style, matching the previous
-- (PO-derived) behavior for rows created before this column existed.
UPDATE qa_ticket t
SET style_id = po.style_id
FROM purchase_order po
WHERE t.po_id = po.id
  AND po.style_id IS NOT NULL;
