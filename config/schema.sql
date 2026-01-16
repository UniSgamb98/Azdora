------------------------------------------------------------
-- TABLE: ota
------------------------------------------------------------
CREATE TABLE ota (
    id INTEGER GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uq_ota_name UNIQUE (name)
);

------------------------------------------------------------
-- TABLE: guest
------------------------------------------------------------
CREATE TABLE guest (
    id INTEGER GENERATED ALWAYS AS IDENTITY,

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,

    notes VARCHAR(500),

    PRIMARY KEY (id)
);

------------------------------------------------------------
-- TABLE: guest_contact
------------------------------------------------------------
CREATE TABLE guest_contact (
    id INTEGER GENERATED ALWAYS AS IDENTITY,

    guest_id INTEGER NOT NULL,
    contact_type VARCHAR(30) NOT NULL,  -- PHONE, EMAIL, WHATSAPP, etc.
    contact_value VARCHAR(100) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_gc_guest
        FOREIGN KEY (guest_id)
        REFERENCES guest(id),

    CONSTRAINT uq_guest_contact
        UNIQUE (guest_id, contact_type, contact_value)
);

------------------------------------------------------------
-- TABLE: reservation
------------------------------------------------------------
CREATE TABLE reservation (
    id INTEGER GENERATED ALWAYS AS IDENTITY,

    ota_id INTEGER NOT NULL,
    guest_id INTEGER NOT NULL,
    provenance VARCHAR(50) NOT NULL,

    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    nights INTEGER NOT NULL,

    adult_guests_count INTEGER NOT NULL DEFAULT 1,
    child_guests_count INTEGER NOT NULL DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes VARCHAR(500),

    PRIMARY KEY (id),

    CONSTRAINT fk_res_ota
        FOREIGN KEY (ota_id) REFERENCES ota(id),

    CONSTRAINT fk_res_guest
        FOREIGN KEY (guest_id) REFERENCES guest(id),

    CONSTRAINT ck_res_dates
        CHECK (check_out > check_in)
);

------------------------------------------------------------
-- INDEXES
------------------------------------------------------------

-- Evita prenotazioni con le stesse date identiche
CREATE UNIQUE INDEX uq_reservation_dates
ON reservation (check_in, check_out);

------------------------------------------------------------
-- INITIAL DATA
------------------------------------------------------------

INSERT INTO ota (name) VALUES ('Booking');
INSERT INTO ota (name) VALUES ('Airbnb');
INSERT INTO ota (name) VALUES ('Sito Diretto');
