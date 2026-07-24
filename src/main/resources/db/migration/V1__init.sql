-- ==========================================================================
-- Garment QA Checking (QMS) - initial schema
-- ==========================================================================

-- ---------------------------------------------------------------- STAFF ---
CREATE TABLE staff (
    id        BIGSERIAL PRIMARY KEY,
    code      VARCHAR(50)  UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    role      VARCHAR(30)  NOT NULL,
    active    BOOLEAN      NOT NULL DEFAULT TRUE
);

-- -------------------------------------------------------------- FACTORY ---
CREATE TABLE factory (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(50)  UNIQUE,
    name    VARCHAR(150) NOT NULL,
    address VARCHAR(255)
);

-- ----------------------------------------------------------------- LINE ---
CREATE TABLE line (
    id         BIGSERIAL PRIMARY KEY,
    factory_id BIGINT      NOT NULL REFERENCES factory (id),
    code       VARCHAR(50),
    name       VARCHAR(100) NOT NULL
);
CREATE INDEX idx_line_factory_id ON line (factory_id);

-- ------------------------------------------------------ PRODUCTION_GROUP ---
CREATE TABLE production_group (
    id      BIGSERIAL PRIMARY KEY,
    line_id BIGINT       NOT NULL REFERENCES line (id),
    name    VARCHAR(100) NOT NULL
);
CREATE INDEX idx_production_group_line_id ON production_group (line_id);

-- ------------------------------------------------------------- CUSTOMER ---
CREATE TABLE customer (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE
);

-- ---------------------------------------------------------------- STYLE ---
CREATE TABLE style (
    id   BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    name VARCHAR(150)
);

-- --------------------------------------------------------- GARMENT_TYPE ---
CREATE TABLE garment_type (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- ----------------------------------------------------- GARMENT_LOCATION ---
CREATE TABLE garment_location (
    id               BIGSERIAL PRIMARY KEY,
    garment_type_id  BIGINT       NOT NULL REFERENCES garment_type (id),
    name             VARCHAR(100) NOT NULL
);
CREATE INDEX idx_garment_location_garment_type_id ON garment_location (garment_type_id);

-- ---------------------------------------------------------------- DEFECT ---
CREATE TABLE defect (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(50) UNIQUE,
    name_en VARCHAR(150),
    name_vi VARCHAR(150) NOT NULL
);

-- --------------------------------------------------------- PURCHASE_ORDER ---
CREATE TABLE purchase_order (
    id             BIGSERIAL PRIMARY KEY,
    style_id       BIGINT REFERENCES style (id),
    customer_id    BIGINT      NOT NULL REFERENCES customer (id),
    po_code        VARCHAR(50) NOT NULL UNIQUE,
    po_quantity    INT,
    date_start     DATE,
    date_shipment  DATE
);
CREATE INDEX idx_purchase_order_customer_id ON purchase_order (customer_id);
CREATE INDEX idx_purchase_order_style_id ON purchase_order (style_id);

-- ---------------------------------------------------------------------
-- QA_TICKET (aggregate root) + dedicated sequence for the ticket_code
-- ---------------------------------------------------------------------
CREATE SEQUENCE qa_ticket_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE qa_ticket (
    id               BIGSERIAL PRIMARY KEY,
    ticket_code      VARCHAR(20)  NOT NULL UNIQUE,
    staff_id         BIGINT       NOT NULL REFERENCES staff (id),
    factory_id       BIGINT       NOT NULL REFERENCES factory (id),
    line_id          BIGINT       NOT NULL REFERENCES line (id),
    group_id         BIGINT REFERENCES production_group (id),
    po_id            BIGINT REFERENCES purchase_order (id),
    customer_id      BIGINT       NOT NULL REFERENCES customer (id),
    garment_type_id  BIGINT       NOT NULL REFERENCES garment_type (id),
    inspection_stage VARCHAR(30)  NOT NULL,
    inspected_qty    INT          NOT NULL DEFAULT 0,
    status           VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    created_at       TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT now()
);
CREATE INDEX idx_qa_ticket_factory_id ON qa_ticket (factory_id);
CREATE INDEX idx_qa_ticket_line_id ON qa_ticket (line_id);
CREATE INDEX idx_qa_ticket_staff_id ON qa_ticket (staff_id);
CREATE INDEX idx_qa_ticket_status ON qa_ticket (status);
CREATE INDEX idx_qa_ticket_created_at ON qa_ticket (created_at);

-- ------------------------------------------------------- QA_TICKET_DEFECT ---
CREATE TABLE qa_ticket_defect (
    id           BIGSERIAL PRIMARY KEY,
    qa_ticket_id BIGINT NOT NULL REFERENCES qa_ticket (id) ON DELETE CASCADE,
    defect_id    BIGINT NOT NULL REFERENCES defect (id),
    note         VARCHAR(255)
);
CREATE INDEX idx_qa_ticket_defect_qa_ticket_id ON qa_ticket_defect (qa_ticket_id);
CREATE INDEX idx_qa_ticket_defect_defect_id ON qa_ticket_defect (defect_id);

-- ---------------------------------------------- QA_TICKET_DEFECT_LOCATION ---
CREATE TABLE qa_ticket_defect_location (
    id                   BIGSERIAL PRIMARY KEY,
    qa_ticket_defect_id  BIGINT       NOT NULL REFERENCES qa_ticket_defect (id) ON DELETE CASCADE,
    garment_location_id  BIGINT REFERENCES garment_location (id),
    location_text        VARCHAR(150) NOT NULL,
    quantity             INT          NOT NULL DEFAULT 1
);
CREATE INDEX idx_qtdl_qa_ticket_defect_id ON qa_ticket_defect_location (qa_ticket_defect_id);

-- ------------------------------------------------- QA_TICKET_DEFECT_IMAGE ---
CREATE TABLE qa_ticket_defect_image (
    id                            BIGSERIAL PRIMARY KEY,
    qa_ticket_defect_location_id  BIGINT       NOT NULL REFERENCES qa_ticket_defect_location (id) ON DELETE CASCADE,
    image_url                     VARCHAR(500) NOT NULL,
    uploaded_at                   TIMESTAMP    NOT NULL DEFAULT now()
);
CREATE INDEX idx_qtdi_qa_ticket_defect_location_id ON qa_ticket_defect_image (qa_ticket_defect_location_id);
