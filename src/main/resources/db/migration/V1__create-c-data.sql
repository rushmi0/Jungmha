-- เพิ่มข้อมูลในตาราง UserProfiles
INSERT INTO UserProfiles (authen_key, share_key,  image_profile, username, first_name, last_name, email, phone_number,
                          created_at, user_type)
VALUES ('028cd65dd43946d52a9ea71622f20930449fcf5ba81f446f420379c4fc2b696109', 'efe904e4f21d5441d42025930481ac3592da6f3ea7409c5deb1e3e3132fa7828', 'src/main/resources/images/preview/3.jpg', 'John', 'John', 'Doe', 'john.doe@email.com', '1234567890', NOW(),
        'Normal'),
       ('02c19f28b92efae5ef8b75863ec103808232770407e6bf4981d138ebe5090989e7', '7308bfd6b11469a36e2ea5d6354a009aa2c361a80ffa3354bd3f5dc3ba27fe6a', 'src/main/resources/images/preview/18.jpg', 'Jane', 'Jane', 'Smith', 'jane.smith@email.com', '9876543210', NOW(),
        'DogWalkers'),
       ('03f3231f8991f84ba1f14fdede115743a882d97e7e03cf958d24f9515a4661d9a6', 'f73edc07ea590993e18c3d875ba3395d115be453e91928a3b71f68e689dca7fb', 'src/main/resources/images/preview/15.jpg', 'Bob', 'Bob', 'Johnson', 'bob.johnson@email.com', '5556667777', NOW(),
        'Normal'),
       ('02aa954033a6f13f244890885e9a26d54ba09292094aa6551f290d15282c69c47f', 'a79a8de22db4c35d181a27927c2d2a3d7a4b7226c65ba46ecf9226c21039c040', 'src/main/resources/images/preview/12.jpg', 'Alice', 'Alice', 'Williams', 'alice.williams@email.com', '1112223333',
        NOW(), 'DogWalkers'),
       ('0258d51ee9dc922450a9558623537d55e460ddd8023e3e1132bb304e867e3e9cf1', '11ecfcf5d14d7044b789e08e608a5078e72f7979188eef1500ef904b26ffc525', 'src/main/resources/images/preview/19.jpg', 'Charlie', 'Charlie', 'Brown', 'charlie.brown@email.com', '9998887777',
        NOW(), 'Normal'),
       ('03da7fbdc9c20c49b0fade91b74de9f6fb481bdd49c5f043276873d6116448655a', '64341e8890b3931feb6f73a6fb8d33a882156258e41dc6d12111eafc82069c61', 'src/main/resources/images/preview/14.jpg', 'Eva', 'Eva', 'Davis', 'eva.davis@email.com', '4443332222', NOW(),
        'DogWalkers'),

       ('0233053c6b3165d4ddfe17a337ba130122aeb5764603c90103d4cea023d35a54d1', '2c1dfdaa3a2d54661df593e1b93acae0475548b2893df5ecd304b62558525ec5', 'src/main/resources/images/preview/11.jpg', 'David', 'David', 'Smith', 'david.smith@email.com', '7778889999', NOW(),
        'DogWalkers'),
       ('02ab5b4e2b1d3c813368e1a885f695c8c5486a4b530d4532a00b424e532ef6b0aa', '4b5ba8d6c75448ad54f8ffa5e65fe51e301454d2b0687a8c9f9ce08d63406eab', 'src/main/resources/images/preview/1.jpg', 'Emma', 'Emma', 'Jones', 'emma.jones@email.com', '5554443333', NOW(),
        'DogWalkers'),
       ('0388d8085a5937663b732c4b7939cb9219ef2803b623f72710d0f38b8cf71d25f8', '779a889b3d9ab5e5078e7f2bd740f63ebbbd13a10002da09298f63bd9a94be5f', 'src/main/resources/images/preview/19.jpg', 'Frank', 'Frank', 'Taylor', 'frank.taylor@email.com', '1119998888', NOW(),
        'DogWalkers'),
       ('03707d8d4142cd8db1933c5e312af817e6dcaf7cb2a82650c56593599ce64845be', 'da7075c8a92a545c13658bd63755b32a935f090acf6936d541ae373385b01658', 'src/main/resources/images/preview/16.jpg', 'Grace', 'Grace', 'Johnson', 'grace.johnson@email.com', '3332221111',
        NOW(), 'DogWalkers'),
       ('0285f7e1e5d7161d4d88131966810912bdfc1d2cd0b50ece9e01cc7f94d4c9bb8a', '72ee35869a735c67acb13c3bc2131c8c9a4206b14dc298c15765dcec1509a5b9', 'src/main/resources/images/preview/4.jpg', 'Henry', 'Henry', 'Miller', 'henry.miller@email.com', '6667778888',
        NOW(), 'DogWalkers');

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
VALUES (2, 'ดินแดง', 'ID123456', 20, 30, 40),
       (4, 'บางรัก', 'ID654321', 25, 35, 45),
       (6, 'บางเขน', 'ID987654', 18, 28, 38),
       (7, 'สวนหลวง', 'ID112233', 22, 32, 42),
       (8, 'สวนหลวง', 'ID445566', 30, 40, 50),
       (9, 'เขตพระโขนง', 'ID778899', 15, 25, 35),
       (10, 'เขตพระโขนง', 'ID990011', 28, 38, 48),
       (11, 'ห้วยขวาง', 'ID223344', 25, 35, 45);


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

