-- การสร้างประเภทข้อมูล ENUM
CREATE TYPE USER_TYPE AS ENUM ('Normal', 'DogWalkers');
CREATE TYPE DOG_SIZE AS ENUM ('Small', 'Medium', 'Big');
CREATE TYPE STATE AS ENUM ('Confirm', 'Cancel', 'Pending');
CREATE TYPE VERIFY AS ENUM ('true', 'false');

-- //////////////////////////////////////////////////////////////////////////////////////////////////////

-- สร้างตาราง UserProfiles
CREATE TABLE IF NOT EXISTS UserProfiles
(
    user_id       SERIAL PRIMARY KEY,
    image_profile VARCHAR(255) DEFAULT 'N/A',
    username      VARCHAR(255) UNIQUE,                                     -- ชื่อของผู้ใช้ที่ไม่ซ้ำกัน
    first_name    VARCHAR(255),
    last_name     VARCHAR(255),                                            -- bank_number ต้องมีความยาวที่เท่ากับ 10 ตัวตัวเลข.
    email         VARCHAR(255) CHECK (email LIKE '%_@_%._%') UNIQUE,
    phone_number  VARCHAR(10) UNIQUE,
    authen_key    VARCHAR(255),                                            -- public key : d0bc0c7b1675aa204412c9024d6de56aafccd78fb0d0cb9433e39ee0fda7f048
    created_at    TIMESTAMPTZ  DEFAULT now(),
    user_type     USER_TYPE CHECK (user_type IN ('Normal', 'DogWalkers') ) -- Normal, DogWalkers
);

-- //////////////////////////////////////////////////////////////////////////////////////////////////////

-- สร้างตาราง DogWalkers
CREATE TABLE IF NOT EXISTS DogWalkers
(
    walker_id      SERIAL PRIMARY KEY,
    user_id        INTEGER UNIQUE REFERENCES UserProfiles (user_id),                                                                       -- ทำการเชื่อมและระบุให้ user_id เป็น unique เพื่อป้องกันความซ้ำซ้อน
    location_name  VARCHAR(255) NOT NULL                                       DEFAULT 'N/A',
    id_card_number BIGINT CHECK (LENGTH(CAST(id_card_number AS VARCHAR)) = 10) DEFAULT 0,
    verification   VERIFY                                                      DEFAULT 'false' CHECK ( verification IN ('true', 'false')), -- กำหนดค่าเริ่มต้นเป็น false
    price_small    INTEGER      NOT NULL                                       DEFAULT 0,
    price_medium   INTEGER      NOT NULL                                       DEFAULT 0,
    price_big      INTEGER      NOT NULL                                       DEFAULT 0
);

-- //////////////////////////////////////////////////////////////////////////////////////////////////////

-- สร้างตาราง Dogs และ DogWalkBookings
CREATE TABLE IF NOT EXISTS Dogs
(
    dog_id     SERIAL PRIMARY KEY,
    dog_image  VARCHAR(255) NOT NULL DEFAULT 'N/A',
    breed_name VARCHAR(255) NOT NULL DEFAULT 'N/A',
    size       DOG_SIZE     NOT NULL CHECK (size IN ('Small', 'Medium', 'Big') ) -- small, medium, big
);

-- //////////////////////////////////////////////////////////////////////////////////////////////////////

CREATE TABLE IF NOT EXISTS DogWalkBookings
(
    booking_id SERIAL PRIMARY KEY,
    walker_id  INTEGER REFERENCES DogWalkers (walker_id),
    user_id    INTEGER REFERENCES UserProfiles (user_id),
    dog_id     INTEGER REFERENCES Dogs (dog_id),
    status     STATE NOT NULL DEFAULT 'Pending' CHECK ( status IN ('Confirm', 'Cancel', 'Pending') ),
    time_start TIME,                        -- เวลาเริ่มจอง HH:MM 10:00
    time_end   TIME,                        -- เวลาสิ้นสุด  HH:MM 11:30
    duration   TIME,
    timestamp  TIMESTAMPTZ    DEFAULT now() -- ช่วงเวลาที่ทำการจอง
);

-- //////////////////////////////////////////////////////////////////////////////////////////////////////

-- สร้างตาราง DogWalkerReviews
CREATE TABLE IF NOT EXISTS DogWalkerReviews
(
    review_id   SERIAL PRIMARY KEY,
    walker_id   INTEGER REFERENCES DogWalkers (walker_id),
    user_id     INTEGER REFERENCES UserProfiles (user_id),
    rating      INTEGER CHECK (rating >= 1 AND rating <= 5),
    review_text VARCHAR(500) -- ข้อความรีวิวที่ผู้ใช้ให้
);

-- //////////////////////////////////////////////////////////////////////////////////////////////////////


CREATE OR REPLACE FUNCTION update_verification()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id_card_number IS NOT NULL AND NEW.verification = 'false' THEN
        NEW.verification := 'true';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_dogwalkers_update_verification
    BEFORE INSERT OR UPDATE
    ON DogWalkers
    FOR EACH ROW
EXECUTE FUNCTION update_verification();


-- //////////////////////////////////////////////////////////////////////////////////////////////////////


-- สร้างฟังก์ชันเพื่อคำนวณระยะเวลาที่จอง
CREATE OR REPLACE FUNCTION calculate_duration() RETURNS TRIGGER AS
$$
BEGIN
    -- คำนวณระยะเวลาระหว่างเวลาสิ้นสุดและเวลาเริ่มต้นแล้วเก็บผลลัพธ์ในฟิลด์ duration
    NEW.duration = (NEW.time_end::time - NEW.time_start::time)::TIME;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- สร้างทริกเกอร์เพื่อเรียกใช้ฟังก์ชัน calculate_duration() เมื่อมีการเพิ่มหรืออัพเดตข้อมูล
CREATE TRIGGER tr_dogwalkbookings_calculate_duration
    BEFORE INSERT OR UPDATE OF time_start, time_end
    ON DogWalkBookings
    FOR EACH ROW
EXECUTE FUNCTION calculate_duration();


-- //////////////////////////////////////////////////////////////////////////////////////////////////////


ALTER TABLE DogWalkBookings
    ADD COLUMN total INTEGER;

CREATE OR REPLACE FUNCTION calculate_total()
    RETURNS TRIGGER AS
$$
DECLARE
    dog_size_enum DOG_SIZE;
    price         INTEGER;
    total_minutes INTEGER;
    hours         INTEGER;
    minutes       INTEGER;
BEGIN
    -- ดึงขนาดของหมา
    SELECT size INTO dog_size_enum FROM Dogs WHERE dog_id = NEW.dog_id;

-- ดึงราคาตามขนาดจาก DogWalkers
    CASE dog_size_enum
        WHEN 'Small' THEN SELECT price_small INTO price FROM DogWalkers WHERE walker_id = NEW.walker_id;
        WHEN 'Medium' THEN SELECT price_medium INTO price FROM DogWalkers WHERE walker_id = NEW.walker_id;
        WHEN 'Big' THEN SELECT price_big INTO price FROM DogWalkers WHERE walker_id = NEW.walker_id;
        ELSE price := 0; -- หากรูปแบบขนาดไม่ถูกต้อง
        END CASE;

    -- คำนวณราคาทั้งหมด
    total_minutes := EXTRACT(EPOCH FROM (NEW.time_end - NEW.time_start)) / 60;
    hours := FLOOR(total_minutes / 60);
    minutes := total_minutes % 60;

    -- คำนวณราคาตามชั่วโมงและชั่วโมงครึ่ง
    NEW.total := (hours + (CASE WHEN minutes > 0 THEN 0.5 ELSE 0 END)) * price;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- สร้างทริกเกอร์
CREATE TRIGGER tr_dogwalkbookings_calculate_total
    BEFORE INSERT OR UPDATE OF time_start, time_end
    ON DogWalkBookings
    FOR EACH ROW
EXECUTE FUNCTION calculate_total();


-- //////////////////////////////////////////////////////////////////////////////////////////////////////

