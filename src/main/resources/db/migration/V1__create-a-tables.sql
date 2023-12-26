CREATE TABLE IF NOT EXISTS ServerKey
(
    key_id      SERIAL PRIMARY KEY,
    private_key VARCHAR(70) DEFAULT 'N/A',
    tag         VARCHAR(50) DEFAULT 'N/A'
);

-- สร้างตาราง UserProfiles
CREATE TABLE IF NOT EXISTS UserProfiles
(
    user_id       SERIAL PRIMARY KEY,
    authen_key    VARCHAR(70)  DEFAULT 'N/A',
    share_key     VARCHAR(70)  DEFAULT 'N/A',
    image_profile VARCHAR(300) DEFAULT 'N/A',
    username      VARCHAR(50)  DEFAULT 'N/A',
    first_name    VARCHAR(50)  DEFAULT 'N/A',
    last_name     VARCHAR(50)  DEFAULT 'N/A',
    email         VARCHAR(50)  DEFAULT 'N/A',
    phone_number  VARCHAR(10)  DEFAULT 'N/A',
    created_at    TIMESTAMPTZ  DEFAULT now(),
    user_type     VARCHAR(10)  DEFAULT 'N/A' CHECK (user_type IN ('Normal', 'DogWalkers', 'N/A'))
);

-- สร้างตาราง DogWalkers
CREATE TABLE IF NOT EXISTS DogWalkers
(
    walker_id      SERIAL PRIMARY KEY,
    user_id        INTEGER UNIQUE REFERENCES UserProfiles (user_id),
    count_review   INTEGER     NOT NULL DEFAULT 0,
    total_review   INTEGER     NOT NULL DEFAULT 0,
    location_name  VARCHAR(50) NOT NULL DEFAULT 'N/A',
    id_card_number VARCHAR(60) NOT NULL DEFAULT 'N/A',
    verification   VARCHAR(6)           DEFAULT 'false' CHECK (verification IN ('true', 'false')),
    price_small    INTEGER     NOT NULL DEFAULT 0,
    price_medium   INTEGER     NOT NULL DEFAULT 0,
    price_big      INTEGER     NOT NULL DEFAULT 0
);


-- ////////////////////////////////////////////////////////////////////////////////////////

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

-- ////////////////////////////////////////////////////////////////////////////////////////

-- สร้างตาราง Dogs และ DogWalkBookings
CREATE TABLE IF NOT EXISTS Dogs
(
    dog_id     SERIAL PRIMARY KEY,
    dog_image  VARCHAR(300) NOT NULL DEFAULT 'N/A',
    breed_name VARCHAR(30)  NOT NULL DEFAULT 'N/A',
    size       VARCHAR(10)  NOT NULL DEFAULT 'N/A' CHECK (size IN ('Small', 'Medium', 'Big', 'N/A'))
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
    total      INTEGER              DEFAULT 0,
    timestamp  TIMESTAMPTZ          DEFAULT now()
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
    review_text VARCHAR(500) DEFAULT 'N/A'
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



-- b1bd351d555e1781134d0b406e58145277a67696d3ad2511c98e4627dafcf5b2
INSERT INTO ServerKey (private_key, tag)
VALUES ('b1bd351d555e1781134d0b406e58145277a67696d3ad2511c98e4627dafcf5b2', 'root');

-- เพิ่มข้อมูลในตาราง UserProfiles
INSERT INTO UserProfiles (authen_key, share_key, image_profile, username, first_name, last_name, email, phone_number,
                          created_at, user_type)
VALUES ('authen_key_1', 'share_key_1', 'N/A', 'user1', 'John', 'Doe', 'john.doe@email.com', '1234567890', NOW(),
        'Normal'),
       ('authen_key_2', 'share_key_2', 'N/A', 'user2', 'Jane', 'Smith', 'jane.smith@email.com', '9876543210', NOW(),
        'DogWalkers'),
       ('authen_key_3', 'share_key_3', 'N/A', 'user3', 'Bob', 'Johnson', 'bob.johnson@email.com', '5556667777', NOW(),
        'Normal'),
       ('authen_key_4', 'share_key_4', 'N/A', 'user4', 'Alice', 'Williams', 'alice.williams@email.com', '1112223333',
        NOW(), 'DogWalkers'),
       ('authen_key_5', 'share_key_5', 'N/A', 'user5', 'Charlie', 'Brown', 'charlie.brown@email.com', '9998887777',
        NOW(), 'Normal'),
       ('authen_key_6', 'share_key_6', 'N/A', 'user6', 'Eva', 'Davis', 'eva.davis@email.com', '4443332222', NOW(),
        'DogWalkers');

-- เพิ่มข้อมูลในตาราง DogWalkers
INSERT INTO DogWalkers (user_id, location_name, id_card_number, verification, price_small, price_medium, price_big)
VALUES (1, 'Park1', 'ID123456', 'true', 20, 30, 40),
       (2, 'Park2', 'ID654321', 'true', 25, 35, 45),
       (3, 'Park3', 'ID987654', 'false', 18, 28, 38),
       (4, 'Park4', 'ID111222', 'true', 22, 32, 42),
       (5, 'Park5', 'ID333444', 'false', 15, 25, 35),
       (6, 'Park6', 'ID555666', 'true', 30, 40, 50);

-- เพิ่มข้อมูลในตาราง Dogs
INSERT INTO Dogs (dog_image, breed_name, size)
VALUES ('dog_image_1.jpg', 'Bulldog', 'Medium'),
       ('dog_image_2.jpg', 'Golden Retriever', 'Big'),
       ('dog_image_3.jpg', 'Poodle', 'Small'),
       ('dog_image_4.jpg', 'Labrador Retriever', 'Big'),
       ('dog_image_5.jpg', 'Beagle', 'Small'),
       ('dog_image_6.jpg', 'German Shepherd', 'Medium');

-- เพิ่มข้อมูลในตาราง DogWalkBookings
INSERT INTO DogWalkBookings (walker_id, user_id, dog_id, status, time_start, time_end)
VALUES (1, 2, 3, 'Confirm', '09:00:00', '10:30:00'),
       (2, 1, 2, 'Pending', '14:00:00', '16:00:00'),
       (3, 4, 1, 'Confirm', '11:30:00', '13:00:00'),
       (4, 3, 5, 'Pending', '08:00:00', '09:30:00'),
       (5, 6, 4, 'Confirm', '16:30:00', '18:00:00'),
       (6, 5, 6, 'Pending', '10:00:00', '11:30:00');

-- เพิ่มข้อมูลในตาราง DogWalkerReviews
INSERT INTO DogWalkerReviews (walker_id, user_id, rating, review_text)
VALUES (1, 2, 5, 'Great service!'),
       (2, 1, 4, 'Friendly and professional'),
       (3, 4, 3, 'Could improve on punctuality'),
       (4, 3, 5, 'Very satisfied with the walk'),
       (5, 6, 4, 'Excellent dog walker'),
       (6, 5, 2, 'Not happy with the service'),
       (1, 3, 4, 'Impressed with the dog handling'),
       (2, 4, 5, 'Always on time and reliable'),
       (3, 5, 3, 'Good service overall'),
       (4, 6, 4, 'Great communication'),
       (5, 1, 5, 'Highly recommended'),
       (6, 2, 2, 'Not satisfied with the service'),
       (1, 4, 4, 'Professional and caring'),
       (2, 3, 3, 'Average service'),
       (3, 6, 5, 'Exceptional dog walker'),
       (4, 5, 2, 'Needs improvement in communication'),
       (5, 2, 4, 'Very pleased with the service'),
       (6, 1, 3, 'Could be more punctual');

-- เพิ่มข้อมูลในตาราง DogWalkerReviews อีก 3 แถว
INSERT INTO DogWalkerReviews (walker_id, user_id, rating)
VALUES   (1, 3, 4),
         (2, 4, 5),
         (3, 5, 3),
         (4, 6, 4),
         (5, 1, 5),
         (6, 2, 2),
         (1, 4, 4),
         (2, 3, 3),
         (3, 6, 5),
         (4, 5, 2);

