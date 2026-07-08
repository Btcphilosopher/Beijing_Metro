-- ============================================================================
-- Beijing Metro (北京地铁) Database Schema Design (PostgreSQL + PostGIS)
-- Includes spatial extension for GIS proximity queries (e.g., Finding nearest stations)
-- ============================================================================

-- Enable PostGIS spatial extension
CREATE EXTENSION IF NOT EXISTS postgis;

-- 1. Users table (Supports standard accounts & staff operators)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('USER', 'OPERATOR', 'ADMIN')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Subway Lines table (Official colors and active operational states)
CREATE TABLE metro_lines (
    id VARCHAR(30) PRIMARY KEY,
    name_zh VARCHAR(50) NOT NULL,
    name_en VARCHAR(100) NOT NULL,
    color_hex VARCHAR(10) NOT NULL, -- e.g., #A42422
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Stations table with PostGIS Coordinate Points (GEOMETRY SRID 4326)
CREATE TABLE metro_stations (
    id VARCHAR(50) PRIMARY KEY,
    name_zh VARCHAR(100) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    location GEOMETRY(Point, 4326) NOT NULL, -- Longitude/Latitude Geographic Point
    exits JSONB DEFAULT '[]'::jsonb, -- Array of exits, e.g. ["A", "B", "C"]
    has_elevator BOOLEAN DEFAULT TRUE,
    has_accessible_toilet BOOLEAN DEFAULT TRUE,
    amenities JSONB DEFAULT '[]'::jsonb, -- e.g. ["Restroom", "ATM", "Family Room"]
    bus_transfers JSONB DEFAULT '[]'::jsonb,
    attractions JSONB DEFAULT '[]'::jsonb,
    congestion_index NUMERIC(3, 2) DEFAULT 1.00 CHECK (congestion_index BETWEEN 1.0 AND 3.0),
    is_active BOOLEAN DEFAULT TRUE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Spatial index for sub-second nearby station geographical lookups
CREATE INDEX idx_metro_stations_location ON metro_stations USING GIST(location);

-- 4. Line-to-Station Mapping (Models ordered sequence of stops)
CREATE TABLE line_stations (
    line_id VARCHAR(30) REFERENCES metro_lines(id) ON DELETE CASCADE,
    station_id VARCHAR(50) REFERENCES metro_stations(id) ON DELETE CASCADE,
    sequence_order INT NOT NULL,
    PRIMARY KEY (line_id, station_id)
);

-- 5. Inter-station Transfer links (Optimizes route planning weights)
CREATE TABLE transfer_connections (
    from_station_id VARCHAR(50) REFERENCES metro_stations(id) ON DELETE CASCADE,
    to_station_id VARCHAR(50) REFERENCES metro_stations(id) ON DELETE CASCADE,
    transfer_time_seconds INT DEFAULT 180, -- estimated walking time to transfer
    is_accessible BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (from_station_id, to_station_id)
);

-- 6. User saved favorite stations
CREATE TABLE saved_stations (
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    station_id VARCHAR(50) REFERENCES metro_stations(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, station_id)
);

-- 7. User saved favorite routes
CREATE TABLE saved_routes (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    start_station_id VARCHAR(50) REFERENCES metro_stations(id) ON DELETE CASCADE,
    end_station_id VARCHAR(50) REFERENCES metro_stations(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, start_station_id, end_station_id)
);

-- 8. Commute Trip History logs
CREATE TABLE trip_history (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    start_station_id VARCHAR(50) REFERENCES metro_stations(id) ON DELETE CASCADE,
    end_station_id VARCHAR(50) REFERENCES metro_stations(id) ON DELETE CASCADE,
    price_rmb NUMERIC(5, 2) NOT NULL,
    duration_minutes INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 9. Electronic Tickets and Wallet accounts
CREATE TABLE e_tickets (
    id VARCHAR(100) PRIMARY KEY, -- e.g., Secure barcode signature UUID
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    balance_rmb NUMERIC(8, 2) DEFAULT 100.00,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'SUSPENDED', 'USED')),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 10. Operational bulletins & delay announcements
CREATE TABLE announcements (
    id SERIAL PRIMARY KEY,
    content_zh TEXT NOT NULL,
    content_en TEXT NOT NULL,
    is_urgent BOOLEAN DEFAULT FALSE,
    published_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    publisher_id INT REFERENCES users(id)
);

-- ============================================================================
-- SAMPLE SQL DATA SEED (Provides direct verification datasets)
-- ============================================================================

INSERT INTO metro_lines (id, name_zh, name_en, color_hex) VALUES 
('Line1', '1号线/八通线', 'Line 1 / Batong Line', '#A42422'),
('Line2', '2号线 (环线)', 'Line 2 (Loop)', '#005691'),
('Line4', '4号线/大兴线', 'Line 4 / Daxing Line', '#007E7A');

INSERT INTO metro_stations (id, name_zh, name_en, location, exits, amenities) VALUES 
('xidan', '西单', 'Xidan', ST_SetSRID(ST_MakePoint(116.3732, 39.9073), 4326), '["A", "B", "C", "D"]'::jsonb, '["Toilet", "ATM", "Store"]'::jsonb),
('fuxingmen', '复兴门', 'Fuxingmen', ST_SetSRID(ST_MakePoint(116.3564, 39.9072), 4326), '["A", "B", "C"]'::jsonb, '["ATM"]'::jsonb),
('xizhimen', '西直门', 'Xizhimen', ST_SetSRID(ST_MakePoint(116.3486, 39.9405), 4326), '["A", "B", "C", "D"]'::jsonb, '["Toilet", "Store"]'::jsonb);

INSERT INTO line_stations (line_id, station_id, sequence_order) VALUES
('Line1', 'fuxingmen', 1),
('Line1', 'xidan', 2),
('Line4', 'xizhimen', 1),
('Line4', 'fuxingmen', 2),
('Line4', 'xidan', 3);
