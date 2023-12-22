CREATE TABLE IF NOT EXISTS ServerKey
(
    key_id      SERIAL PRIMARY KEY,
    private_key VARCHAR(255),
    tag         VARCHAR(255)
);

-- สร้างตาราง UserProfiles
CREATE TABLE IF NOT EXISTS UserProfiles
(
    user_id       SERIAL PRIMARY KEY,
    authen_key VARCHAR(70) DEFAULT 'N/A',
    share_key  VARCHAR(70) DEFAULT 'N/A',
    image_profile VARCHAR(300) DEFAULT 'N/A',
    username      VARCHAR(50) UNIQUE,
    first_name    VARCHAR(50) DEFAULT 'N/A',
    last_name     VARCHAR(50) DEFAULT 'N/A',
    email         VARCHAR(50) CHECK (email LIKE '%_@_%._%') UNIQUE,
    phone_number  VARCHAR(10)  DEFAULT 'N/A',
    created_at    TIMESTAMPTZ  DEFAULT now(),
    user_type     VARCHAR(255) CHECK (user_type IN ('Normal', 'DogWalkers'))
);

-- สร้างตาราง DogWalkers
CREATE TABLE IF NOT EXISTS DogWalkers
(
    walker_id      SERIAL PRIMARY KEY,
    user_id        INTEGER UNIQUE REFERENCES UserProfiles (user_id),
    location_name  VARCHAR(50) NOT NULL DEFAULT 'N/A',
    id_card_number VARCHAR(60) NOT NULL DEFAULT 'N/A',
    verification   VARCHAR(6)            DEFAULT 'false' CHECK (verification IN ('true', 'false')),
    price_small    INTEGER      NOT NULL DEFAULT 0,
    price_medium   INTEGER      NOT NULL DEFAULT 0,
    price_big      INTEGER      NOT NULL DEFAULT 0
);

CREATE OR REPLACE FUNCTION update_verification()
    RETURNS TRIGGER AS
$$
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

-- สร้างตาราง Dogs และ DogWalkBookings
CREATE TABLE IF NOT EXISTS Dogs
(
    dog_id     SERIAL PRIMARY KEY,
    dog_image  VARCHAR(300) NOT NULL DEFAULT 'N/A',
    breed_name VARCHAR(30) NOT NULL DEFAULT 'N/A',
    size       VARCHAR(10) NOT NULL DEFAULT 'N/A' CHECK (size IN ('Small', 'Medium', 'Big', 'N/A'))
);

-- สร้างตาราง DogWalkBookings
CREATE TABLE IF NOT EXISTS DogWalkBookings
(
    booking_id SERIAL PRIMARY KEY,
    walker_id  INTEGER REFERENCES DogWalkers (walker_id),
    user_id    INTEGER REFERENCES UserProfiles (user_id),
    dog_id     INTEGER REFERENCES Dogs (dog_id),
    status     VARCHAR(10) NOT NULL DEFAULT 'Pending' CHECK (status IN ('Confirm', 'Cancel', 'Pending')),
    time_start TIME,
    time_end   TIME,
    duration   TIME,
    total      INTEGER               DEFAULT 0,
    timestamp  TIMESTAMPTZ           DEFAULT now()
);


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


-- สร้างตาราง DogWalkerReviews
CREATE TABLE IF NOT EXISTS DogWalkerReviews
(
    review_id   SERIAL PRIMARY KEY,
    walker_id   INTEGER REFERENCES DogWalkers (walker_id),
    user_id     INTEGER REFERENCES UserProfiles (user_id),
    rating      INTEGER CHECK (rating >= 1 AND rating <= 5),
    review_text VARCHAR(500)
);


-- b1bd351d555e1781134d0b406e58145277a67696d3ad2511c98e4627dafcf5b2
INSERT INTO ServerKey (private_key, tag)
VALUES ('b1bd351d555e1781134d0b406e58145277a67696d3ad2511c98e4627dafcf5b2', 'root');

-- เพิ่มข้อมูลตัวอย่างในตาราง UserProfiles
INSERT INTO UserProfiles (image_profile, username, first_name, last_name, email, phone_number, authen_key, user_type)
VALUES ('profile1.jpg', 'user1', 'John', 'Doe', 'john.doe@email.com', '1234567890',
        'd0bc0c7b1675aa204412c9024d6de56aafccd78fb0d0cb9433e39ee0fda7f048', 'Normal'),
       ('profile2.jpg', 'user2', 'Jane', 'Smith', 'jane.smith@email.com', '9876543210',
        'eba194a2eb10e0aba531b559548584bce753de0100148bfd29f7265b76e595b2', 'DogWalkers');

-- เพิ่มข้อมูลตัวอย่างในตาราง DogWalkers
INSERT INTO DogWalkers (user_id, location_name, id_card_number, price_small, price_medium, price_big)
VALUES (1, 'Park A', 'c5234b2313bc9e7bdbf18810e12b41f636588c0eeef8ce4bc31de57c120a7d73', 50, 60, 70),
       (2, 'Park B', '889157fbd2ddf84740fc465765c51f5e3ada347676afce181b2db9c786e6417a', 45, 55, 65);

-- เพิ่มข้อมูลตัวอย่างในตาราง Dogs
INSERT INTO Dogs (dog_image, breed_name, size)
VALUES ('dog1.jpg', 'Labrador Retriever', 'Big'),
       ('dog2.jpg', 'Beagle', 'Small');

-- เพิ่มข้อมูลตัวอย่างในตาราง DogWalkBookings
INSERT INTO DogWalkBookings (walker_id, user_id, dog_id, status, time_start, time_end)
VALUES (1, 2, 1, 'Confirm', '10:00', '11:30'),
       (2, 1, 2, 'Pending', '14:00', '15:00');

-- เพิ่มข้อมูลตัวอย่างในตาราง DogWalkerReviews
INSERT INTO DogWalkerReviews (walker_id, user_id, rating, review_text)
VALUES (1, 2, 4, 'Great service!'),
       (2, 1, 5, 'Excellent dog walker!');
