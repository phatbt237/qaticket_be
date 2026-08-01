-- ==========================================================================
-- PO, style, and customer are now typed directly on the ticket instead of
-- picked from master data. The purchase_order/style/customer tables and
-- their data are kept as-is; only qa_ticket's links to them are replaced
-- with free-text columns.
-- ==========================================================================

ALTER TABLE qa_ticket ADD COLUMN po_number VARCHAR(50);
ALTER TABLE qa_ticket ADD COLUMN style VARCHAR(150);
ALTER TABLE qa_ticket ADD COLUMN customer_name VARCHAR(150);

UPDATE qa_ticket t
SET po_number = po.po_code
FROM purchase_order po
WHERE t.po_id = po.id;

UPDATE qa_ticket t
SET style = COALESCE(s.code, s.name)
FROM style s
WHERE t.style_id = s.id;

UPDATE qa_ticket t
SET customer_name = c.name
FROM customer c
WHERE t.customer_id = c.id;

ALTER TABLE qa_ticket ALTER COLUMN customer_name SET NOT NULL;

DROP INDEX IF EXISTS idx_qa_ticket_style_id;
DROP INDEX IF EXISTS idx_qa_ticket_customer_id;
ALTER TABLE qa_ticket DROP COLUMN po_id;
ALTER TABLE qa_ticket DROP COLUMN style_id;
ALTER TABLE qa_ticket DROP COLUMN customer_id;

CREATE INDEX idx_qa_ticket_customer_name ON qa_ticket (customer_name);
