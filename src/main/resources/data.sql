-- Criar tabela de eventos se não existir
CREATE TABLE IF NOT EXISTS events (
    event_id VARCHAR(50) PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    change_type VARCHAR(10) NOT NULL,
    aggregate_id VARCHAR(50) NOT NULL,
    source VARCHAR(50) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    store_id VARCHAR(50) NOT NULL,
    quantity INTEGER,
    available_quantity INTEGER,
    reserved_quantity INTEGER,
    processed BOOLEAN NOT NULL DEFAULT FALSE,
    processed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL
);

-- Script para gerar eventos baseados nos registros de estoque existentes
-- Este script cria eventos CREATED para cada registro de estoque inicial

-- Inserir eventos correspondentes aos registros de estoque (apenas se não existirem)
INSERT INTO events (event_id, event_type, change_type, aggregate_id, source, created_at, product_id, store_id, quantity, available_quantity, reserved_quantity, processed, processed_at)
SELECT * FROM (VALUES
                   -- EVENTOS PARA TECNOLOGIA
                   -- iPhone 15 Pro (TECH001) - 3 lojas
                   ('evt-tech001-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-tech001-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440001', 15, 13, 2, true, CURRENT_TIMESTAMP),
                   ('evt-tech001-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-tech001-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440002', 12, 11, 1, true, CURRENT_TIMESTAMP),
                   ('evt-tech001-store003-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-tech001-store003', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440003', 8, 8, 0, true, CURRENT_TIMESTAMP),

                   -- Samsung Galaxy S24 (TECH002) - 2 lojas
                   ('evt-tech002-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-tech002-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440001', 18, 15, 3, true, CURRENT_TIMESTAMP),
                   ('evt-tech002-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-tech002-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440002', 14, 13, 1, true, CURRENT_TIMESTAMP),

                   -- MacBook Air M2 (TECH003) - 2 lojas
                   ('evt-tech003-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-tech003-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440001', 10, 9, 1, true, CURRENT_TIMESTAMP),
                   ('evt-tech003-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-tech003-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440002', 8, 8, 0, true, CURRENT_TIMESTAMP),

                   -- PlayStation 5 (TECH004) - 2 lojas
                   ('evt-tech004-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-tech004-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440001', 12, 10, 2, true, CURRENT_TIMESTAMP),
                   ('evt-tech004-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-tech004-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440002', 8, 7, 1, true, CURRENT_TIMESTAMP),

                   -- Smart TV Samsung (TECH005) - 2 lojas
                   ('evt-tech005-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-tech005-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544000a', '550e8400-e29b-41d4-a716-446655440001', 25, 22, 3, true, CURRENT_TIMESTAMP),
                   ('evt-tech005-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-tech005-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544000a', '550e8400-e29b-41d4-a716-446655440002', 20, 18, 2, true, CURRENT_TIMESTAMP),

                   -- EVENTOS PARA SUPERMERCADO
                   -- Arroz Tio João (SUPER001) - 3 lojas
                   ('evt-super001-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-super001-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', 200, 180, 20, true, CURRENT_TIMESTAMP),
                   ('evt-super001-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-super001-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440002', 150, 135, 15, true, CURRENT_TIMESTAMP),
                   ('evt-super001-store003-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-super001-store003', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', 180, 170, 10, true, CURRENT_TIMESTAMP),

                   -- Óleo de Soja (SUPER002) - 2 lojas
                   ('evt-super002-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-super002-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', 120, 110, 10, true, CURRENT_TIMESTAMP),
                   ('evt-super002-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-super002-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440002', 100, 92, 8, true, CURRENT_TIMESTAMP),

                   -- EVENTOS PARA CASA E MÓVEIS
                   -- Sofá Retrátil (HOME001) - 2 lojas
                   ('evt-home001-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-home001-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544000b', '550e8400-e29b-41d4-a716-446655440001', 5, 4, 1, true, CURRENT_TIMESTAMP),
                   ('evt-home001-store003-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-home001-store003', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544000b', '550e8400-e29b-41d4-a716-446655440003', 3, 3, 0, true, CURRENT_TIMESTAMP),

                   -- Mesa de Jantar (HOME002) - 2 lojas
                   ('evt-home002-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-home002-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544000c', '550e8400-e29b-41d4-a716-446655440001', 8, 7, 1, true, CURRENT_TIMESTAMP),
                   ('evt-home002-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-home002-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544000c', '550e8400-e29b-41d4-a716-446655440002', 6, 6, 0, true, CURRENT_TIMESTAMP),

                   -- EVENTOS PARA ELETRODOMÉSTICOS
                   -- Geladeira Brastemp (ELETRO001) - 2 lojas
                   ('evt-eletro001-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-eletro001-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544000e', '550e8400-e29b-41d4-a716-446655440001', 15, 13, 2, true, CURRENT_TIMESTAMP),
                   ('evt-eletro001-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-eletro001-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544000e', '550e8400-e29b-41d4-a716-446655440002', 12, 11, 1, true, CURRENT_TIMESTAMP),

                   -- Air Fryer (ELETRO003) - 2 lojas
                   ('evt-eletro003-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-eletro003-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440001', 45, 40, 5, true, CURRENT_TIMESTAMP),
                   ('evt-eletro003-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-eletro003-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440002', 35, 32, 3, true, CURRENT_TIMESTAMP),

                   -- EVENTOS PARA MODA
                   -- Tênis Nike (FASHION002) - 2 lojas
                   ('evt-fashion002-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-fashion002-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440023', '550e8400-e29b-41d4-a716-446655440001', 60, 52, 8, true, CURRENT_TIMESTAMP),
                   ('evt-fashion002-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-fashion002-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440023', '550e8400-e29b-41d4-a716-446655440002', 45, 40, 5, true, CURRENT_TIMESTAMP),

                   -- Camiseta Polo Lacoste (FASHION001) - 2 lojas
                   ('evt-fashion001-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-fashion001-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440022', '550e8400-e29b-41d4-a716-446655440001', 30, 27, 3, true, CURRENT_TIMESTAMP),
                   ('evt-fashion001-store003-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-fashion001-store003', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440022', '550e8400-e29b-41d4-a716-446655440003', 25, 23, 2, true, CURRENT_TIMESTAMP),

                   -- EVENTOS PARA PET SHOP
                   -- Ração Golden (PET001) - 2 lojas
                   ('evt-pet001-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-pet001-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544001a', '550e8400-e29b-41d4-a716-446655440001', 80, 72, 8, true, CURRENT_TIMESTAMP),
                   ('evt-pet001-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-pet001-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544001a', '550e8400-e29b-41d4-a716-446655440002', 65, 60, 5, true, CURRENT_TIMESTAMP),

                   -- EVENTOS PARA MAIS VENDIDOS
                   -- Carregador iPhone (BEST001) - 3 lojas
                   ('evt-best001-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-best001-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544002a', '550e8400-e29b-41d4-a716-446655440001', 150, 135, 15, true, CURRENT_TIMESTAMP),
                   ('evt-best001-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-best001-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544002a', '550e8400-e29b-41d4-a716-446655440002', 120, 108, 12, true, CURRENT_TIMESTAMP),
                   ('evt-best001-store003-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-best001-store003', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544002a', '550e8400-e29b-41d4-a716-446655440003', 100, 90, 10, true, CURRENT_TIMESTAMP),

                   -- Película iPhone (BEST002) - 2 lojas
                   ('evt-best002-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-best002-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544002b', '550e8400-e29b-41d4-a716-446655440001', 200, 180, 20, true, CURRENT_TIMESTAMP),
                   ('evt-best002-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'INCREASE', 'inventory-best002-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-44665544002b', '550e8400-e29b-41d4-a716-446655440002', 180, 162, 18, true, CURRENT_TIMESTAMP),

                   -- EVENTOS PARA PRODUTOS COM ESTOQUE ZERADO
                   -- Yamaha MT-07 (VEHICLE002) na loja de SP - Estoque zerado
                   ('evt-vehicle002-store001-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'DECREASE', 'inventory-vehicle002-store001', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 0, 0, 0, true, CURRENT_TIMESTAMP),

                   -- Vestido Farm Rio (FASHION003) na loja do RJ - Estoque zerado
                   ('evt-fashion003-store002-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'DECREASE', 'inventory-fashion003-store002', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440024', '550e8400-e29b-41d4-a716-446655440002', 0, 0, 0, true, CURRENT_TIMESTAMP),

                   -- Furadeira Bosch (TOOL001) na loja de BH - Estoque zerado
                   ('evt-tool001-store003-' || EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)::TEXT, 'CREATED', 'DECREASE', 'inventory-tool001-store003', 'stock-migration-service', CURRENT_TIMESTAMP, '660e8400-e29b-41d4-a716-446655440014', '550e8400-e29b-41d4-a716-446655440003', 0, 0, 0, true, CURRENT_TIMESTAMP)

                  ) AS new_events(event_id, event_type, change_type, aggregate_id, source, created_at, product_id, store_id, quantity, available_quantity, reserved_quantity, processed, processed_at)
WHERE NOT EXISTS (
    SELECT 1 FROM events
    WHERE events.aggregate_id = new_events.aggregate_id
);

-- Verificar quantos eventos foram inseridos
SELECT
    COUNT(*) as total_events_created,
    source,
    event_type,
    change_type
FROM events
WHERE source = 'stock-migration-service'
GROUP BY source, event_type, change_type
ORDER BY event_type, change_type;
