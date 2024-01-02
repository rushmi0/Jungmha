-- สร้างตาราง UserProfiles
CREATE TABLE IF NOT EXISTS UserProfiles
(
    user_id       SERIAL PRIMARY KEY,
    authen_key    VARCHAR(255) DEFAULT 'N/A',
    share_key     VARCHAR(255) DEFAULT 'N/A',
    image_profile VARCHAR(255) DEFAULT 'N/A',
    username      VARCHAR(20)  DEFAULT 'N/A',
    first_name    VARCHAR(20)  DEFAULT 'N/A',
    last_name     VARCHAR(20)  DEFAULT 'N/A',
    email         VARCHAR(30)  DEFAULT 'N/A',
    phone_number  VARCHAR(10)  DEFAULT 'N/A',
    created_at    TIMESTAMPTZ  DEFAULT now(),
    user_type     VARCHAR(10)  DEFAULT 'N/A' CHECK (user_type IN ('Normal', 'DogWalkers', 'N/A'))
);


CREATE TABLE IF NOT EXISTS Signature
(
    sig_id    SERIAL PRIMARY KEY,
    user_id   INTEGER REFERENCES UserProfiles (user_id),
    signature VARCHAR(255) unique,
    timestamp TIMESTAMPTZ DEFAULT now()
);

-- สร้างตาราง DogWalkers
CREATE TABLE IF NOT EXISTS DogWalkers
(
    walker_id      SERIAL PRIMARY KEY,
    user_id        INTEGER UNIQUE REFERENCES UserProfiles (user_id),
    count_used     INTEGER     NOT NULL DEFAULT 0,
    count_review   INTEGER     NOT NULL DEFAULT 0,
    total_review   INTEGER     NOT NULL DEFAULT 0,
    location_name  VARCHAR(50) NOT NULL DEFAULT 'N/A',
    id_card_number VARCHAR(60) NOT NULL DEFAULT 'N/A',
    verification   VARCHAR(10)          DEFAULT 'false' CHECK (verification IN ('true', 'false')),
    price_small    INTEGER     NOT NULL DEFAULT 0,
    price_medium   INTEGER     NOT NULL DEFAULT 0,
    price_big      INTEGER     NOT NULL DEFAULT 0
);

-- ////////////////////////////////////////////////////////////////////////////////////////

CREATE OR REPLACE FUNCTION update_verification()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.id_card_number IS NOT NULL AND NEW.id_card_number != 'N/A' THEN
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

-- ////////////////////////////////////////////////////////////////////////////////////////

-- สร้างตาราง Dogs และ DogWalkBookings
CREATE TABLE IF NOT EXISTS Dogs
(
    dog_id     SERIAL PRIMARY KEY,
    dog_image  VARCHAR(255) NOT NULL DEFAULT 'N/A',
    breed_name VARCHAR(30)  NOT NULL DEFAULT 'N/A',
    size       VARCHAR(10)  NOT NULL DEFAULT 'N/A' CHECK (size IN ('Small', 'Medium', 'Big', 'N/A'))
);

-- สร้างตาราง DogWalkBookings
CREATE TABLE IF NOT EXISTS DogWalkBookings
(
    booking_id     SERIAL PRIMARY KEY,
    walker_id      INTEGER REFERENCES DogWalkers (walker_id),
    user_id        INTEGER REFERENCES UserProfiles (user_id),
    dog_id         INTEGER REFERENCES Dogs (dog_id),
    status         VARCHAR(10) NOT NULL DEFAULT 'Pending' CHECK (status IN ('Confirm', 'Cancel', 'Pending')),
    booking_date   DATE                 DEFAULT CURRENT_DATE,
    time_start     TIME,
    time_end       TIME,
    duration       TIME,
    total          INTEGER              DEFAULT 0,
    timestamp      TIMESTAMPTZ          DEFAULT now(),
    service_status VARCHAR(20)          DEFAULT 'In Progress' CHECK (service_status IN ('In Progress', 'Completed'))
);


-- ///////////////////////////////////////////////////////////////////////////////////////////

-- สร้างฟังก์ชันเพื่อตรวจสอบการจองทับซ้อน
CREATE OR REPLACE FUNCTION check_booking_conflict()
    RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS (SELECT 1
               FROM DogWalkBookings
               WHERE booking_date = NEW.booking_date
                 AND walker_id = NEW.walker_id
                 AND (
                   (time_start, time_end) OVERLAPS (NEW.time_start, NEW.time_end)
                   )) THEN
        RAISE EXCEPTION 'Booking conflict: Walker % is already booked at this time on %', NEW.walker_id, NEW.booking_date;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- สร้างทริกเกอร์เพื่อเรียกใช้ฟังก์ชันเมื่อมีการเพิ่มข้อมูล
CREATE TRIGGER tr_check_booking_conflict
    BEFORE INSERT
    ON DogWalkBookings
    FOR EACH ROW
EXECUTE FUNCTION check_booking_conflict();


-- ///////////////////////////////////////////////////////////////////////////////////////////


CREATE OR REPLACE FUNCTION calculate_total()
    RETURNS TRIGGER AS
$$
DECLARE
    dog_size_enum VARCHAR(10);
    price         INTEGER;
    total_minutes INTEGER;
    hours         INTEGER;
    minutes       INTEGER;
BEGIN
    -- ดึงขนาดของหมา
    SELECT size INTO dog_size_enum FROM Dogs WHERE dog_id = NEW.dog_id;

    -- ดึงราคาตามขนาดจาก DogWalkers
    SELECT CASE
               WHEN dog_size_enum = 'Small' THEN price_small
               WHEN dog_size_enum = 'Medium' THEN price_medium
               WHEN dog_size_enum = 'Big' THEN price_big
               ELSE 0
               END
    INTO price
    FROM DogWalkers
    WHERE walker_id = NEW.walker_id;

    -- คำนวณราคาทั้งหมด
    total_minutes := EXTRACT(EPOCH FROM (NEW.time_end - NEW.time_start)) / 60;
    hours := FLOOR(total_minutes / 60);
    minutes := total_minutes % 60;

    -- คำนวณราคาตามชั่วโมงและชั่วโมงครึ่ง
    NEW.total := (hours + (CASE WHEN minutes > 0 THEN 0.5 ELSE 0 END)) * price;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- เปลี่ยนแปลง Trigger ในตาราง DogWalkBookings
DROP TRIGGER IF EXISTS tr_dogwalkbookings_calculate_total ON DogWalkBookings;

CREATE TRIGGER tr_dogwalkbookings_calculate_total
    BEFORE INSERT OR UPDATE OF time_start, time_end, dog_id, walker_id
    ON DogWalkBookings
    FOR EACH ROW
EXECUTE FUNCTION calculate_total();



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

-- ////////////////////////////////////////////////////////////////////////////////////////

-- สร้างฟังก์ชันเพื่อคำนวณจำนวณการให้บริการและอัปเดตฟิลด์ count_used ในตาราง DogWalkers
CREATE OR REPLACE FUNCTION update_count_used()
    RETURNS TRIGGER AS
$$
DECLARE
    used_count INTEGER;
BEGIN
    -- นับจำนวณการให้บริการทั้งหมดที่มี service_status เป็น 'Completed'
    SELECT COALESCE(COUNT(*), 0)
    INTO used_count
    FROM DogWalkBookings
    WHERE walker_id = NEW.walker_id
      AND service_status = 'Completed';

    -- อัปเดตฟิลด์ count_used ในตาราง DogWalkers
    UPDATE DogWalkers
    SET count_used = used_count
    WHERE walker_id = NEW.walker_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- สร้างทริกเกอร์เพื่อเรียกใช้ฟังก์ชัน update_count_used() เมื่อมีการเพิ่มหรืออัปเดตข้อมูลใน DogWalkBookings
CREATE TRIGGER tr_dogwalkbookings_update_count_used
    AFTER INSERT OR UPDATE
    ON DogWalkBookings
    FOR EACH ROW
EXECUTE FUNCTION update_count_used();



-- สร้างตาราง DogWalkerReviews
CREATE TABLE IF NOT EXISTS DogWalkerReviews
(
    review_id   SERIAL PRIMARY KEY,
    walker_id   INTEGER REFERENCES DogWalkers (walker_id),
    user_id     INTEGER REFERENCES UserProfiles (user_id),
    rating      INTEGER CHECK (rating >= 1 AND rating <= 5),
    review_text VARCHAR(255) DEFAULT 'N/A'
);

-- สร้างฟังก์ชันเพื่อคำนวณและอัปเดตคะแนนรีวิวทั้งหมดของ DogWalkers
CREATE OR REPLACE FUNCTION update_total_review()
    RETURNS TRIGGER AS
$$
DECLARE
    total_rating INTEGER;
BEGIN
    -- คำนวณคะแนนรีวิวทั้งหมด
    SELECT COALESCE(SUM(rating), 0)
    INTO total_rating
    FROM DogWalkerReviews
    WHERE walker_id = NEW.walker_id;

    -- ทำให้คะแนนรีวิวทั้งหมดไม่เกิน 5
    total_rating := LEAST(total_rating, 5);

    -- อัปเดตคะแนนรีวิวทั้งหมดในตาราง DogWalkers
    UPDATE DogWalkers
    SET total_review = total_rating
    WHERE walker_id = NEW.walker_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- สร้างทริกเกอร์เพื่อเรียกใช้ฟังก์ชัน update_total_review() เมื่อมีการเพิ่มหรืออัพเดตข้อมูลใน DogWalkerReviews
CREATE TRIGGER tr_dogwalkerreviews_update_total_review
    AFTER INSERT OR UPDATE
    ON DogWalkerReviews
    FOR EACH ROW
EXECUTE FUNCTION update_total_review();

-- ////////////////////////////////////////////////////////////////////////////////////////////////

-- สร้างฟังก์ชันเพื่อคำนวณจำนวณรีวิวและอัปเดตฟิลด์ count_review ในตาราง DogWalkers
CREATE OR REPLACE FUNCTION update_count_review()
    RETURNS TRIGGER AS
$$
DECLARE
    review_count INTEGER;
BEGIN
    -- นับจำนวณรีวิวทั้งหมด
    SELECT COALESCE(COUNT(*), 0)
    INTO review_count
    FROM DogWalkerReviews
    WHERE walker_id = NEW.walker_id;

    -- อัปเดตฟิลด์ count_review ในตาราง DogWalkers
    UPDATE DogWalkers
    SET count_review = review_count
    WHERE walker_id = NEW.walker_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- สร้างทริกเกอร์เพื่อเรียกใช้ฟังก์ชัน update_count_review() เมื่อมีการเพิ่มหรืออัพเดตข้อมูลใน DogWalkerReviews
CREATE TRIGGER tr_dogwalkerreviews_update_count_review
    AFTER INSERT OR UPDATE
    ON DogWalkerReviews
    FOR EACH ROW
EXECUTE FUNCTION update_count_review();

