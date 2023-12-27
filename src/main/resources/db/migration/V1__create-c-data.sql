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
VALUES (1, 3, 4),
       (2, 4, 5),
       (3, 5, 3),
       (4, 6, 4),
       (5, 1, 5),
       (6, 2, 2),
       (1, 4, 4),
       (2, 3, 3),
       (3, 6, 5),
       (4, 5, 2);

