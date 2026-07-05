-- ============================================================
--  Hotel Management System Pro  –  PostgreSQL Database Setup
--  Database name: hms_pro_db
--
--  Run once:
--    psql -U postgres -c "CREATE DATABASE hms_pro_db;"
--    psql -U postgres -d hms_pro_db -f hms_pro_db_setup.sql
-- ============================================================

\c hms_pro_db

-- ============================================================
-- 1. ROOMS
-- ============================================================
CREATE TABLE IF NOT EXISTS rooms (
    room_number   INT PRIMARY KEY,
    room_type     VARCHAR(50)   NOT NULL,
    price_per_day NUMERIC(10,2) NOT NULL CHECK (price_per_day > 0),
    is_available  BOOLEAN NOT NULL DEFAULT TRUE
);

-- ============================================================
-- 2. CUSTOMERS  — contact: exactly 10 digits enforced at DB level
-- ============================================================
CREATE TABLE IF NOT EXISTS customers (
    customer_id  SERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    contact      VARCHAR(10)  NOT NULL
                     CHECK (contact ~ '^\d{10}$'),
    email        VARCHAR(100),
    address      TEXT
);

-- ============================================================
-- 3. BOOKINGS
--    • num_days >= 1  (same check-in == check-out date → 1 day)
--    • check_out must be >= check_in  (same day allowed; app sets num_days=1)
-- ============================================================
CREATE TABLE IF NOT EXISTS bookings (
    booking_id   SERIAL PRIMARY KEY,
    customer_id  INT  NOT NULL REFERENCES customers(customer_id) ON DELETE CASCADE,
    room_number  INT  NOT NULL REFERENCES rooms(room_number)     ON DELETE RESTRICT,
    check_in     DATE NOT NULL DEFAULT CURRENT_DATE,
    check_out    DATE,
    num_days     INT  NOT NULL DEFAULT 1 CHECK (num_days >= 1),
    status       VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                     CHECK (status IN ('ACTIVE','CHECKED_OUT','CANCELLED')),
    -- same-day allowed: check_out >= check_in
    CONSTRAINT chk_checkout_not_before_checkin
        CHECK (check_out IS NULL OR check_out >= check_in)
);

-- ============================================================
-- 4. BILLS
-- ============================================================
CREATE TABLE IF NOT EXISTS bills (
    bill_id       SERIAL PRIMARY KEY,
    booking_id    INT  NOT NULL REFERENCES bookings(booking_id) ON DELETE RESTRICT,
    customer_id   INT  NOT NULL REFERENCES customers(customer_id),
    room_number   INT  NOT NULL,
    num_days      INT  NOT NULL CHECK (num_days >= 1),
    room_charges  NUMERIC(10,2) NOT NULL CHECK (room_charges >= 0),
    service_tax   NUMERIC(10,2) NOT NULL CHECK (service_tax  >= 0),
    total_amount  NUMERIC(10,2) NOT NULL CHECK (total_amount >= 0),
    bill_date     DATE NOT NULL DEFAULT CURRENT_DATE
);

-- ============================================================
-- 5. Seed rooms
-- ============================================================
INSERT INTO rooms (room_number, room_type, price_per_day, is_available) VALUES
    (101, 'Standard',  1200.00, TRUE),
    (102, 'Standard',  1200.00, TRUE),
    (103, 'Standard',  1200.00, TRUE),
    (201, 'Double',    1800.00, TRUE),
    (202, 'Double',    1800.00, TRUE),
    (301, 'Deluxe',    2500.00, TRUE),
    (302, 'Deluxe',    2500.00, TRUE),
    (401, 'Suite',     4500.00, TRUE),
    (402, 'Suite',     4500.00, TRUE),
    (501, 'Executive', 7000.00, TRUE)
ON CONFLICT (room_number) DO NOTHING;

-- ============================================================
-- 6. Indexes
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_bookings_customer ON bookings(customer_id);
CREATE INDEX IF NOT EXISTS idx_bookings_room     ON bookings(room_number);
CREATE INDEX IF NOT EXISTS idx_bookings_status   ON bookings(status);
CREATE INDEX IF NOT EXISTS idx_bills_booking     ON bills(booking_id);

\echo '✅  hms_pro_db setup complete.'
