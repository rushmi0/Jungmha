-- ข้อมูลจำลองสำหรับ UserProfiles
INSERT INTO UserProfiles (image_profile, username, first_name, last_name, email, phone_number, authen_key, user_type)
VALUES ('profile1.jpg', 'user1', 'John', 'Doe', 'john.doe@email.com', '1234567890',
        'd0bc0c7b1675aa204412c9024d6de56aafccd78fb0d0cb9433e39ee0fda7f048', 'Normal'),
       ('profile2.jpg', 'user2', 'Jane', 'Smith', 'jane.smith@email.com', '9876543210',
        'eba194a2eb10e0aba531b559548584bce753de0100148bfd29f7265b76e595b2', 'DogWalkers'),
       ('profile3.jpg', 'user3', 'Alice', 'Johnson', 'alice.johnson@email.com', '5551112233',
        '1c4648fbdb46ccc9391d75a41e824cca80fd742cdeccddb3da24887c5c200457', 'Normal'),
       ('profile4.jpg', 'user4', 'Bob', 'Miller', 'bob.miller@email.com', '9998887777',
        '86311bd86cf43a5dc1818774ceee11210fa5031b9281502617935d9c11811bd2', 'DogWalkers'),
       ('profile5.jpg', 'user5', 'Charlie', 'Brown', 'charlie.brown@email.com', '1231231234',
        'bb787fcc86e4d5fedf3df9587104e88246ead292b34858ef4ef0b0693f7f20e4', 'Normal');

-- ข้อมูลจำลองสำหรับ DogWalkers
INSERT INTO DogWalkers (user_id, location_name, id_card_number, price_small, price_medium, price_big)
VALUES (1, 'Park A', 1234567890, 50, 60, 70),
       (2, 'Park B', 9876543210, 45, 55, 65),
       (3, 'Park C', 5551112233, 40, 50, 60),
       (4, 'Park D', 9998887777, 55, 65, 75),
       (5, 'Park E', 1231231234, 60, 70, 80);


-- ข้อมูลจำลองสำหรับ Dogs
INSERT INTO Dogs (dog_image, breed_name, size)
VALUES ('dog1.jpg', 'Labrador Retriever', 'Big'),
       ('dog2.jpg', 'Beagle', 'Small'),
       ('dog3.jpg', 'German Shepherd', 'Medium'),
       ('dog4.jpg', 'Golden Retriever', 'Big'),
       ('dog5.jpg', 'French Bulldog', 'Small');

-- ข้อมูลจำลองสำหรับ DogWalkBookings
INSERT INTO DogWalkBookings (walker_id, user_id, dog_id, status, time_start, time_end)
VALUES (1, 2, 3, 'Confirm', '10:00', '11:30'),
       (2, 3, 4, 'Pending', '14:00', '15:00'),
       (3, 4, 5, 'Confirm', '16:00', '17:30'),
       (4, 5, 1, 'Pending', '08:00', '10:00'),
       (5, 1, 2, 'Confirm', '12:00', '14:30');

SELECT *
FROM DogWalkBookings;

-- ข้อมูลจำลองสำหรับ DogWalkerReviews
INSERT INTO DogWalkerReviews (walker_id, user_id, rating, review_text)
VALUES (1, 2, 4, 'Great service!'),
       (2, 3, 5, 'Excellent dog walker!'),
       (3, 4, 3, 'Good experience overall.'),
       (4, 5, 2, 'Could improve communication.'),
       (5, 1, 5, 'Very satisfied with the service.');