-- ==========================================================================
-- Sample seed data for local/demo testing (Swagger UI, etc.)
-- ==========================================================================

INSERT INTO staff (code, full_name, role, active) VALUES
  ('QA001', 'Nguyễn Văn A', 'QA_INSPECTOR', true),
  ('QA002', 'Trần Thị B', 'QA_LEAD', true),
  ('ADM001', 'Lê Văn C', 'ADMIN', true);

INSERT INTO factory (code, name, address) VALUES
  ('FCT01', 'Nhà máy Bình Dương', '123 Đường ABC, Bình Dương'),
  ('FCT02', 'Nhà máy Long An', '456 Đường XYZ, Long An');

INSERT INTO line (factory_id, code, name) VALUES
  ((SELECT id FROM factory WHERE code = 'FCT01'), 'L1', 'Chuyền 1'),
  ((SELECT id FROM factory WHERE code = 'FCT01'), 'L2', 'Chuyền 2'),
  ((SELECT id FROM factory WHERE code = 'FCT02'), 'L1', 'Chuyền 1');

INSERT INTO production_group (line_id, name) VALUES
  ((SELECT id FROM line WHERE code = 'L1' AND factory_id = (SELECT id FROM factory WHERE code = 'FCT01')), 'Cụm A'),
  ((SELECT id FROM line WHERE code = 'L2' AND factory_id = (SELECT id FROM factory WHERE code = 'FCT01')), 'Cụm B');

INSERT INTO customer (name) VALUES
  ('Uniqlo'),
  ('H&M');

INSERT INTO style (code, name) VALUES
  ('ST001', 'T-Shirt Basic'),
  ('ST002', 'Polo Shirt');

INSERT INTO garment_type (name) VALUES
  ('Áo thun'),
  ('Quần');

INSERT INTO garment_location (garment_type_id, name) VALUES
  ((SELECT id FROM garment_type WHERE name = 'Áo thun'), 'Cổ áo'),
  ((SELECT id FROM garment_type WHERE name = 'Áo thun'), 'Tay áo'),
  ((SELECT id FROM garment_type WHERE name = 'Áo thun'), 'Sườn'),
  ((SELECT id FROM garment_type WHERE name = 'Quần'), 'Cạp quần'),
  ((SELECT id FROM garment_type WHERE name = 'Quần'), 'Ống quần');

INSERT INTO defect (code, name_en, name_vi) VALUES
  ('DF001', 'Open seam', 'Bung đường may'),
  ('DF002', 'Stain', 'Vết bẩn'),
  ('DF003', 'Broken stitch', 'Đứt chỉ'),
  ('DF004', 'Skip stitch', 'Bỏ mũi'),
  ('DF005', 'Uneven hem', 'Lai không đều');

INSERT INTO purchase_order (style_id, customer_id, po_code, po_quantity, date_start, date_shipment) VALUES
  ((SELECT id FROM style WHERE code = 'ST001'), (SELECT id FROM customer WHERE name = 'Uniqlo'), 'PO-2026-001', 5000, '2026-06-01', '2026-08-01'),
  ((SELECT id FROM style WHERE code = 'ST002'), (SELECT id FROM customer WHERE name = 'H&M'), 'PO-2026-002', 3000, '2026-06-15', '2026-08-20');

-- Sample QA ticket tree (ticket -> defect -> location -> image) for end-to-end GET testing
INSERT INTO qa_ticket (ticket_code, staff_id, factory_id, line_id, group_id, po_id, customer_id, garment_type_id,
                        inspection_stage, inspected_qty, status)
VALUES (
  'I00001',
  (SELECT id FROM staff WHERE code = 'QA001'),
  (SELECT id FROM factory WHERE code = 'FCT01'),
  (SELECT id FROM line WHERE code = 'L1' AND factory_id = (SELECT id FROM factory WHERE code = 'FCT01')),
  (SELECT id FROM production_group WHERE name = 'Cụm A'),
  (SELECT id FROM purchase_order WHERE po_code = 'PO-2026-001'),
  (SELECT id FROM customer WHERE name = 'Uniqlo'),
  (SELECT id FROM garment_type WHERE name = 'Áo thun'),
  'INLINE', 120, 'SUBMITTED'
);

INSERT INTO qa_ticket_defect (qa_ticket_id, defect_id, note)
VALUES (
  (SELECT id FROM qa_ticket WHERE ticket_code = 'I00001'),
  (SELECT id FROM defect WHERE code = 'DF001'),
  'Phát hiện ở mũi may đầu tay'
);

INSERT INTO qa_ticket_defect_location (qa_ticket_defect_id, garment_location_id, location_text, quantity)
VALUES (
  (SELECT qtd.id FROM qa_ticket_defect qtd
     WHERE qtd.qa_ticket_id = (SELECT id FROM qa_ticket WHERE ticket_code = 'I00001')),
  (SELECT id FROM garment_location WHERE name = 'Cổ áo' AND garment_type_id = (SELECT id FROM garment_type WHERE name = 'Áo thun')),
  'Cổ áo',
  2
);

INSERT INTO qa_ticket_defect_image (qa_ticket_defect_location_id, image_url)
VALUES (
  (SELECT qtdl.id FROM qa_ticket_defect_location qtdl
     JOIN qa_ticket_defect qtd ON qtd.id = qtdl.qa_ticket_defect_id
     WHERE qtd.qa_ticket_id = (SELECT id FROM qa_ticket WHERE ticket_code = 'I00001')),
  'https://picsum.photos/seed/qms-ticket-1/400/300'
);

-- Keep the dedicated ticket-code sequence in sync with the manually seeded ticket,
-- so the next ticket generated through the API becomes I00002.
SELECT setval('qa_ticket_seq', 1, true);
