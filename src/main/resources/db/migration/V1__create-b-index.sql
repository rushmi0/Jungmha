-- โค้ดต่อไปนี้จะเป็นการเพิ่ม Index เพื่อเพิ่มประสิทธิภาพในการค้นหา
CREATE INDEX idx_users_email ON UserProfiles (email);
CREATE INDEX idx_reviews_walkerid ON DogWalkerReviews (walker_id);
CREATE INDEX idx_reviews_userid ON DogWalkerReviews (user_id);
CREATE INDEX idx_users_name ON UserProfiles (username);
CREATE INDEX idx_identity_root_id ON Identity(root_id);
