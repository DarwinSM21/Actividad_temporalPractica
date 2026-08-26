-- =====================================================================
--  AgroTrace Quevedo -- esquema base
--  Practica GA-U4 de Aplicaciones Web -- UTEQ
-- =====================================================================

CREATE TABLE finca (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(120)  NOT NULL,
    productor       VARCHAR(120)  NOT NULL,
    canton          VARCHAR(60)   NOT NULL,
    hectareas       NUMERIC(6,2)  NOT NULL CHECK (hectareas > 0),
    codigo_registro VARCHAR(20)   NOT NULL UNIQUE
);

CREATE TABLE lote (
    id                      BIGSERIAL PRIMARY KEY,
    codigo                  VARCHAR(12)  NOT NULL UNIQUE,
    finca_id                BIGINT       NOT NULL REFERENCES finca(id),
    fecha_recepcion         DATE         NOT NULL,
    peso_kg                 NUMERIC(8,2) NOT NULL CHECK (peso_kg > 0),
    humedad_porcentaje      NUMERIC(4,2) NOT NULL
                            CHECK (humedad_porcentaje BETWEEN 0 AND 100),
    fermentacion_porcentaje NUMERIC(4,2) NOT NULL
                            CHECK (fermentacion_porcentaje BETWEEN 0 AND 100),
    estado                  VARCHAR(20)  NOT NULL
                            CHECK (estado IN ('ACEPTADO','SECADO_ADICIONAL','RECHAZADO')),
    creado_en               TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_lote_estado ON lote (estado);
CREATE INDEX idx_lote_finca  ON lote (finca_id, fecha_recepcion DESC);

CREATE TABLE certificado (
    id             BIGSERIAL PRIMARY KEY,
    numero         VARCHAR(24) NOT NULL UNIQUE,
    lote_id        BIGINT      NOT NULL REFERENCES lote(id),
    cedula_tecnico VARCHAR(10) NOT NULL,
    fecha_emision  DATE        NOT NULL,
    vigente        BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_certificado_lote ON certificado (lote_id);

COMMENT ON TABLE  finca       IS 'Finca productora afiliada a APROCAFA';
COMMENT ON TABLE  lote        IS 'Lote de cacao recibido en el centro de acopio';
COMMENT ON TABLE  certificado IS 'Certificado oficial emitido via servicio SOAP';
COMMENT ON COLUMN lote.estado IS 'Resultado de la politica de recepcion de APROCAFA';
