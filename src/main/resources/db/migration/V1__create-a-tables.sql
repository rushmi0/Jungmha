-- สร้างตาราง UserProfiles
CREATE TABLE IF NOT EXISTS UserProfiles
(
    user_id       SERIAL PRIMARY KEY,
    image_profile VARCHAR(255) DEFAULT 'N/A',
    username      VARCHAR(255) UNIQUE,
    first_name    VARCHAR(255),
    last_name     VARCHAR(255),
    email         VARCHAR(255) CHECK (email LIKE '%_@_%._%') UNIQUE,
    phone_number  VARCHAR(10) UNIQUE,
    authen_key    VARCHAR(70),
    created_at    TIMESTAMPTZ  DEFAULT now(),
    user_type     VARCHAR(255) CHECK (user_type IN ('Normal', 'DogWalkers'))
);

-- สร้างตาราง DogWalkers
CREATE TABLE IF NOT EXISTS DogWalkers
(
    walker_id      SERIAL PRIMARY KEY,
    user_id        INTEGER UNIQUE REFERENCES UserProfiles (user_id),
    location_name  VARCHAR(255) NOT NULL DEFAULT 'N/A',
    id_card_number VARCHAR(255)   NOT NULL DEFAULT 'N/A',
    verification   VARCHAR(6)          DEFAULT 'false' CHECK (verification IN ('true', 'false')),
    price_small    INTEGER      NOT NULL DEFAULT 0,
    price_medium   INTEGER      NOT NULL DEFAULT 0,
    price_big      INTEGER      NOT NULL DEFAULT 0
);

-- สร้างตาราง Dogs และ DogWalkBookings
CREATE TABLE IF NOT EXISTS Dogs
(
    dog_id     SERIAL PRIMARY KEY,
    dog_image  VARCHAR(255) NOT NULL DEFAULT 'N/A',
    breed_name VARCHAR(255) NOT NULL DEFAULT 'N/A',
    size       VARCHAR(255) NOT NULL CHECK (size IN ('Small', 'Medium', 'Big'))
);

-- สร้างตาราง DogWalkBookings
CREATE TABLE IF NOT EXISTS DogWalkBookings
(
    booking_id SERIAL PRIMARY KEY,
    walker_id  INTEGER REFERENCES DogWalkers (walker_id),
    user_id    INTEGER REFERENCES UserProfiles (user_id),
    dog_id     INTEGER REFERENCES Dogs (dog_id),
    status     VARCHAR(255) NOT NULL DEFAULT 'Pending' CHECK (status IN ('Confirm', 'Cancel', 'Pending')),
    time_start TIME,
    time_end   TIME,
    duration   TIME,
    total      INTEGER,
    timestamp  TIMESTAMPTZ           DEFAULT now()
);

-- สร้างตาราง DogWalkerReviews
CREATE TABLE IF NOT EXISTS DogWalkerReviews
(
    review_id   SERIAL PRIMARY KEY,
    walker_id   INTEGER REFERENCES DogWalkers (walker_id),
    user_id     INTEGER REFERENCES UserProfiles (user_id),
    rating      INTEGER CHECK (rating >= 1 AND rating <= 5),
    review_text VARCHAR(500)
);


-- ข้อมูลจำลองสำหรับ UserProfiles
INSERT INTO UserProfiles (image_profile, username, first_name, last_name, email, phone_number, authen_key, user_type)
VALUES ('profile1.jpg', 'user1', 'John', 'Doe', 'john.doe@email.com', '1234567890',
        'd0bc0c7b1675aa204412c9024d6de56aafccd78fb0d0cb9433e39ee0fda7f048', 'Normal'),
       ('profile2.jpg', 'user2', 'Jane', 'Smith', 'jane.smith@email.com', '9876543210',
        'eba194a2eb10e0aba531b559548584bce753de0100148bfd29f7265b76e595b2', 'DogWalkers'),
       ('profile3.jpg', 'user3', 'Alice', 'Johnson', 'alice.johnson@email.com', '5551112233',
        '1c4648fbdb46ccc9391d75a41e824cca80fd742cdeccddb3da24887c5c200457', 'Normal');

-- ข้อมูลจำลองสำหรับ DogWalkers
INSERT INTO DogWalkers (user_id, location_name, id_card_number, price_small, price_medium, price_big)
VALUES (1, 'Park A', 'c5234b2313bc9e7bdbf18810e12b41f636588c0eeef8ce4bc31de57c120a7d73', 50, 60, 70),
       (2, 'Park B', '889157fbd2ddf84740fc465765c51f5e3ada347676afce181b2db9c786e6417a', 45, 55, 65),
       (3, 'Park C', '1188e354a2f302b38ad68ed27c6ca3c89b559f7dd6b204b8caae0f13cc723720', 40, 50, 60);

-- ข้อมูลจำลองสำหรับ Dogs
INSERT INTO Dogs (dog_image, breed_name, size)
VALUES ('dog1.jpg', 'Labrador Retriever', 'Big'),
       ('dog2.jpg', 'Beagle', 'Small'),
       ('dog3.jpg', 'German Shepherd', 'Medium');

-- ข้อมูลจำลองสำหรับ DogWalkBookings
INSERT INTO DogWalkBookings (walker_id, user_id, dog_id, status, time_start, time_end)
VALUES (1, 2, 3, 'Confirm', '10:00', '11:30'),
       (2, 3, 1, 'Pending', '14:00', '15:00'),
       (3, 1, 2, 'Confirm', '16:00', '17:30');

-- ข้อมูลจำลองสำหรับ DogWalkerReviews
INSERT INTO DogWalkerReviews (walker_id, user_id, rating, review_text)
VALUES (1, 2, 4, 'Great service!'),
       (2, 3, 5, 'Excellent dog walker!'),
       (3, 2, 3, 'Good experience overall');


