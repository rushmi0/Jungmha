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


-- เพิ่มข้อมูลลงในตาราง Signature
INSERT INTO Signature (user_id, signature, timestamp)
VALUES (1,
        '30450221009aeceef8a4bce7f99493161903c8a2345dbcc773428406838911c39c87f6acbd02202f1adefb1dd27a3bcbb0fe0cbebba2876c43a2ee5d65a1566ae3bb5c3cfeadf0',
        '2023-12-29T12:00:00Z'),
       (2,
        '304402201183d806fe7f4797bb09ca8c2d8142378b9893a6e370623c9dde21802b0e19310220181bb7851221f22426656480b45cfc5f8fae43a5e252117d08cfb2318771a181',
        '2023-12-29T12:15:00Z'),
       (3,
        '304502210097c8bfceb2428a4939fe51fe4c76c7533b42d721d7b9fb12e499ae180b3a2b9a02205cebb99eebb72d9544578e847659c7bdfd0721a1b923f7c9ec8453c97ef79c72',
        '2023-12-29T12:30:00Z'),
       (4,
        '304402206e26d06d1814f54852851dcf4c4d0269300619461731c2b291b6799a0ff6c8e102206ffa20cad231a82176dd9c4f30b277572573335564af55752965e20203697ab1',
        '2023-12-29T12:45:00Z'),
       (5,
        '3045022100c5dde364e1cb44852819023cd0f1fe077930340a129f30a7e13f976b6fb4f51002207a1a7cd2d1912858056a311d998254135b4ef340c244a2987a560f7e12cbb859',
        '2023-12-29T13:00:00Z'),
       (6,
        '3045022100a7a926fbdb452f44b2cc8bc736e16465a114c92bbb6effdfcbfe3d95f140b0c4022050dcf5d95c3f7498f370728f452d1f52b6c8291bf185e5c628162c1c1124234b',
        '2023-12-29T13:15:00Z'),
       (1,
        '3045022100ce47030f90b2a0f6f3fe7278273a1fabebdb764abe506424e1ab18560cb6730c022029e94026db62906b43aa7b9c68492e5d0c2c8fce5c699ff99a8b8b3149fb8d37',
        '2023-12-29T13:00:00Z'),
       (1,
        '304402201439ba63837c45528f19f927c050bfa926724cdb80262d9e87aa0b3edf6427d302206c25a5b6596c86cb0ce741b0356154eb76bc6bddb8c190b5897a88da9c9b95c2',
        '2023-12-29T12:15:00Z');


-- เพิ่มข้อมูลในตาราง DogWalkers
INSERT INTO DogWalkers (user_id, location_name, id_card_number, price_small, price_medium, price_big)
VALUES (2, 'Park1', 'ID123456', 20, 30, 40),
       (4, 'Park2', 'ID654321', 25, 35, 45),
       (6, 'Park3', 'ID987654', 18, 28, 38);


-- เพิ่มข้อมูลในตาราง Dogs
INSERT INTO Dogs (dog_image, breed_name, size)
VALUES ('src/main/resources/images/dogs/dog-big/Americanpitbull-large.jpg', 'American Pitbull', 'Big'),
       ('src/main/resources/images/dogs/dog-big/Germanshepherd-large.jpg', 'German Shepherd', 'Big'),
       ('src/main/resources/images/dogs/dog-big/Siberianhusky-large.jpg', 'Siberian Husky', 'Big'),

       ('src/main/resources/images/dogs/dog-medium/Goldenretriever-medium.jpg', 'Golden Retriever', 'Medium'),
       ('src/main/resources/images/dogs/dog-medium/Jackrussell-medium.jpg', 'Jack Russell', 'Medium'),
       ('src/main/resources/images/dogs/dog-medium/Maltese-medium.jpg', 'Maltese', 'Medium'),

       ('src/main/resources/images/dogs/dog-small/Chihuahua-small.jpg', 'Chihuahua', 'Small'),
       ('src/main/resources/images/dogs/dog-small/Pomeranian-small.jpg', 'Pomeranian', 'Small'),
       ('src/main/resources/images/dogs/dog-small/Shihtzu-small.jpg', 'Shih tzu', 'Small');


-- เพิ่มข้อมูลในตาราง DogWalkBookings
INSERT INTO DogWalkBookings (walker_id, user_id, dog_id, status, time_start, time_end, service_status)
VALUES (1, 1, 3, 'Confirm', '09:00:00', '10:30:00', 'Completed'),
       (2, 3, 2, 'Pending', '14:00:00', '16:00:00', 'In Progress'),
       (3, 5, 1, 'Confirm', '11:30:00', '13:00:00', 'Completed');


-- เพิ่มข้อมูลในตาราง DogWalkerReviews
INSERT INTO DogWalkerReviews (walker_id, user_id, rating, review_text)
VALUES (2, 3, 5, 'Great service!'),
       (2, 1, 4, 'Friendly and professional'),
       (2, 1, 3, 'Could improve on punctuality'),
       (3, 5, 2, 'Not happy with the service'),
       (2, 1, 4, 'Impressed with the dog handling'),
       (2, 1, 5, 'Always on time and reliable'),
       (3, 3, 3, 'Good service overall'),
       (2, 3, 4, 'Great communication'),
       (2, 5, 5, 'Highly recommended'),
       (3, 5, 2, 'Not satisfied with the service'),
       (2, 5, 4, 'Professional and caring'),
       (3, 3, 3, 'Average service'),
       (3, 5, 5, 'Exceptional dog walker'),
       (2, 5, 2, 'Needs improvement in communication'),
       (2, 1, 4, 'Very pleased with the service'),
       (1, 1, 3, 'Could be more punctual');

-- เพิ่มข้อมูลในตาราง DogWalkerReviews อีก 3 แถว
INSERT INTO DogWalkerReviews (walker_id, user_id, rating)
VALUES
    (1, 1, 4),
    (1, 3, 5),
    (2, 3, 3),
    (3, 5, 4);

