-- =====================================================================
--  Datos de ejemplo: 6 fincas y 12 lotes que cubren los tres estados
-- =====================================================================

INSERT INTO finca (nombre, productor, canton, hectareas, codigo_registro) VALUES
    ('La Envidia',        'Segundo Chavez Mora',      'Quevedo',   4.50,  'APR-QV-001'),
    ('San Jacinto',       'Maria Vera Zambrano',      'Quevedo',   7.20,  'APR-QV-002'),
    ('El Recuerdo',       'Jose Andrade Loor',        'Buena Fe',  3.10,  'APR-BF-003'),
    ('Palmar Adentro',    'Luz Cedeno Bravo',         'Valencia',  9.80,  'APR-VL-004'),
    ('Los Tres Rios',     'Angel Macias Pincay',      'Quevedo',   5.60,  'APR-QV-005'),
    ('Santa Martha',      'Rosa Intriago Solorzano',  'Buena Fe',  2.40,  'APR-BF-006');

-- Lotes ACEPTADOS: humedad <= 7,50 y fermentacion >= 60,00
INSERT INTO lote (codigo, finca_id, fecha_recepcion, peso_kg,
                  humedad_porcentaje, fermentacion_porcentaje, estado) VALUES
    ('LT-000001', 1, DATE '2026-08-18', 145.50, 6.80, 72.00, 'ACEPTADO'),
    ('LT-000002', 2, DATE '2026-08-18', 210.00, 7.10, 68.50, 'ACEPTADO'),
    ('LT-000003', 4, DATE '2026-08-19', 320.75, 6.20, 81.00, 'ACEPTADO'),
    ('LT-000004', 1, DATE '2026-08-20',  98.30, 7.50, 65.00, 'ACEPTADO'),
    ('LT-000005', 5, DATE '2026-08-21', 175.00, 6.95, 77.25, 'ACEPTADO');

-- Lotes en SECADO_ADICIONAL: humedad > 7,50
INSERT INTO lote (codigo, finca_id, fecha_recepcion, peso_kg,
                  humedad_porcentaje, fermentacion_porcentaje, estado) VALUES
    ('LT-000006', 3, DATE '2026-08-19', 122.40, 8.20, 70.00, 'SECADO_ADICIONAL'),
    ('LT-000007', 6, DATE '2026-08-20',  64.10, 9.05, 66.40, 'SECADO_ADICIONAL'),
    ('LT-000008', 2, DATE '2026-08-21', 188.90, 7.80, 74.10, 'SECADO_ADICIONAL');

-- Lotes RECHAZADOS: humedad valida pero fermentacion < 60,00
INSERT INTO lote (codigo, finca_id, fecha_recepcion, peso_kg,
                  humedad_porcentaje, fermentacion_porcentaje, estado) VALUES
    ('LT-000009', 5, DATE '2026-08-19', 133.20, 7.00, 54.50, 'RECHAZADO'),
    ('LT-000010', 3, DATE '2026-08-20',  91.60, 6.40, 48.00, 'RECHAZADO'),
    ('LT-000011', 6, DATE '2026-08-21', 156.30, 7.25, 59.90, 'RECHAZADO'),
    ('LT-000012', 4, DATE '2026-08-22', 240.00, 6.75, 58.20, 'RECHAZADO');
