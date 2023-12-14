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


