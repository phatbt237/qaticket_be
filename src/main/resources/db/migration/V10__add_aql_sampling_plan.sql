-- ==========================================================================
-- AQL (Acceptable Quality Level) sampling plan lookup table, per ANSI/ASQ
-- Z1.4 single sampling, normal inspection, general inspection level II.
-- Ranges are inclusive on both ends (qty_min <= lot size <= qty_max).
-- ==========================================================================

CREATE TABLE aql_sampling_plan (
    id              BIGSERIAL PRIMARY KEY,
    aql_level       VARCHAR(10) NOT NULL,
    qty_min         INTEGER NOT NULL,
    qty_max         INTEGER NOT NULL,
    sampling_size   INTEGER NOT NULL,
    major_accept    INTEGER NOT NULL,
    major_reject    INTEGER NOT NULL,
    minor_accept    INTEGER NOT NULL,
    minor_reject    INTEGER NOT NULL
);

CREATE INDEX idx_aql_sampling_plan_level_qty ON aql_sampling_plan (aql_level, qty_min, qty_max);

INSERT INTO aql_sampling_plan (aql_level, qty_min, qty_max, sampling_size, major_accept, major_reject, minor_accept, minor_reject) VALUES
  ('2.5', 51, 90, 13, 0, 1, 0, 1),
  ('2.5', 91, 150, 20, 1, 2, 1, 2),
  ('2.5', 151, 280, 32, 2, 3, 2, 3),
  ('2.5', 281, 500, 50, 3, 4, 3, 4),
  ('2.5', 501, 1200, 80, 5, 6, 5, 6),
  ('2.5', 1201, 3200, 125, 7, 8, 7, 8),
  ('2.5', 3201, 10000, 200, 10, 11, 10, 11),
  ('2.5', 10001, 35000, 315, 14, 15, 14, 15),
  ('2.5', 35001, 500000, 500, 21, 22, 21, 22),
  ('1.5', 51, 90, 13, 1, 2, 1, 2),
  ('1.5', 91, 150, 20, 1, 2, 1, 2),
  ('1.5', 151, 280, 32, 2, 3, 2, 3),
  ('1.5', 281, 500, 50, 3, 4, 3, 4),
  ('1.5', 501, 1200, 80, 5, 6, 5, 6),
  ('1.5', 1201, 3200, 125, 7, 8, 7, 8),
  ('1.5', 3201, 10000, 200, 10, 11, 10, 11),
  ('1.5', 10001, 35000, 315, 14, 15, 14, 15),
  ('1.5', 35001, 500000, 500, 21, 22, 21, 22);
