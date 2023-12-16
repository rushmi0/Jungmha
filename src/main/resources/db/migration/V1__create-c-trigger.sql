


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


-- //////////////////////////////////////////////////////////////////////////////////////////////////////


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


-- //////////////////////////////////////////////////////////////////////////////////////////////////////


ALTER TABLE DogWalkBookings
    ADD COLUMN total INTEGER;

CREATE OR REPLACE FUNCTION calculate_total()
    RETURNS TRIGGER AS
$$
DECLARE
    dog_size_enum VARCHAR(6);
    price         INTEGER;
    total_minutes INTEGER;
    hours         INTEGER;
    minutes       INTEGER;
BEGIN
    -- ดึงขนาดของหมา
    SELECT size INTO dog_size_enum FROM Dogs WHERE dog_id = NEW.dog_id;

-- ดึงราคาตามขนาดจาก DogWalkers
    CASE dog_size_enum
        WHEN 'Small' THEN SELECT price_small INTO price FROM DogWalkers WHERE walker_id = NEW.walker_id;
        WHEN 'Medium' THEN SELECT price_medium INTO price FROM DogWalkers WHERE walker_id = NEW.walker_id;
        WHEN 'Big' THEN SELECT price_big INTO price FROM DogWalkers WHERE walker_id = NEW.walker_id;
        ELSE price := 0; -- หากรูปแบบขนาดไม่ถูกต้อง
        END CASE;

    -- คำนวณราคาทั้งหมด
    total_minutes := EXTRACT(EPOCH FROM (NEW.time_end - NEW.time_start)) / 60;
    hours := FLOOR(total_minutes / 60);
    minutes := total_minutes % 60;

    -- คำนวณราคาตามชั่วโมงและชั่วโมงครึ่ง
    NEW.total := (hours + (CASE WHEN minutes > 0 THEN 0.5 ELSE 0 END)) * price;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- สร้างทริกเกอร์
CREATE TRIGGER tr_dogwalkbookings_calculate_total
    BEFORE INSERT OR UPDATE OF time_start, time_end
    ON DogWalkBookings
    FOR EACH ROW
EXECUTE FUNCTION calculate_total();
