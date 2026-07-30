-- ==========================================================================
-- GET /api/qa-tickets now filters by customer_id instead of line_id, so add
-- the index that filter needs (line_id keeps its own index for other uses).
-- ==========================================================================

CREATE INDEX idx_qa_ticket_customer_id ON qa_ticket (customer_id);
