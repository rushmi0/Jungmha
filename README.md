# เอกสาร API ของ Jungmha

เอกสารนี้ให้ข้อมูลเกี่ยวกับ API ของ Jungmha ซึ่งเป็นบริการที่เชื่อมโยงเจ้าของสุนัขกับผู้พาสุนัขเดินเล่น (Dog Walkers) ด้านล่างนี้คือรายการของเส้นทาง API ที่พร้อมให้บริการ พร้อมกับความสามารถและสัญลักษณ์ที่บ่งชี้ว่า API พร้อมใช้งานหรือไม่

## สารบัญ
- [Authentication](#authentication)
- [Dog Walker](#dog-walker)
  - ✅ [Private Dog Walker](#private-dog-walker)
  - ✅ [Public Dog Walker](#public-dog-walker)
- [User](#user)
  - ✅ [User Sign-In](#user-sign-in)
  - ✅ [User Sign-Up](#user-sign-up)
  - ✅ [User Booking](#user-booking)
  - ✅ [User Upload Profile image](#user-upload)
- [Dog](#dog)
  - ✅ [Get Dogs](#get-dogs)
  - ✅ [Dog Image](#dog-image)
- [Home](#home)
  - ✅ [Filter](#filter)
    - ✅ [Private Filter](#private-filter)
    - ✅ [Public Filter](#public-filter)
- [etc](#etc)
  - ✅ [Open Image URL](#open-image-url)
  - ✅ [Index](#index)

## Authentication

### Access Token
- **คำอธิบาย:** API นี้ใช้ Access Token เพื่อการยืนยันตัวตน
- **Scope:**
  - `view-only`: การเข้าถึงเฉพาะการดู
  - `full-control`: การเข้าถึงทั้งหมด

## Dog Walker

### Private Dog Walker

#### GET /api/v1/auth/home/filter
- **สรุป:** ดึงข้อมูล Dog Walker จากฐานข้อมูล
- **พารามิเตอร์:**
  - `Access-Token` (Header, จำเป็น): Token สำหรับการยืนยันตัวตน
  - `name`, `verify`, `location`, `pSmall`, `pMedium`, `pBig`, `max` (Query, ไม่บังคับ): พารามิเตอร์สำหรับกรองผลลัพธ์

- ตัวอย่างการใช้งาน:
```http request
GET http://localhost:8080/api/v1/auth/home/filter
Content-Type: application/json
Access-Token: eyJ1c2VyTmFtZSI6IkF1cmEiLCJwZXJtaXNzaW9uIjoidmlldy1vbmx5IiwiZXhwIjo4NjQwMDE3MDM2NzM5NzE1NDUsImlhdCI6MTcwMzY3NDA1Nzk0NSwic2lnbmF0dXJlIjoiMzA0NDAyMjA1NDQ5MDkwMTk4NDllYmY5MDk4OTkzMWVlZWY1YmZjNDdjZDRiOWFjOGIwYjJkZGRhYjZjMjdjNjc0MGFhMDY1MDIyMDE3Y2E4YzY0ZTBkM2Y3MWIwOGZkMmRlNWNmNDczOTI5MDk5M2RmMGI1NjIzZTRlMmUwMzc5MzM4NzIxMGZjMzQifQ==

###

GET http://localhost:8080/api/v1/auth/home/filter?pSmall=50&max=3
Content-Type: application/json
Access-Token: eyJ1c2VyTmFtZSI6IkF1cmEiLCJwZXJtaXNzaW9uIjoidmlldy1vbmx5IiwiZXhwIjo4NjQwMDE3MDM2NzM5NzE1NDUsImlhdCI6MTcwMzY3NDA1Nzk0NSwic2lnbmF0dXJlIjoiMzA0NDAyMjA1NDQ5MDkwMTk4NDllYmY5MDk4OTkzMWVlZWY1YmZjNDdjZDRiOWFjOGIwYjJkZGRhYjZjMjdjNjc0MGFhMDY1MDIyMDE3Y2E4YzY0ZTBkM2Y3MWIwOGZkMmRlNWNmNDczOTI5MDk5M2RmMGI1NjIzZTRlMmUwMzc5MzM4NzIxMGZjMzQifQ==

###

GET http://localhost:8080/api/v1/auth/home/filter?name=user4
Content-Type: application/json
Access-Token: eyJ1c2VyTmFtZSI6IkF1cmEiLCJwZXJtaXNzaW9uIjoidmlldy1vbmx5IiwiZXhwIjo4NjQwMDE3MDM2NzM5NzE1NDUsImlhdCI6MTcwMzY3NDA1Nzk0NSwic2lnbmF0dXJlIjoiMzA0NDAyMjA1NDQ5MDkwMTk4NDllYmY5MDk4OTkzMWVlZWY1YmZjNDdjZDRiOWFjOGIwYjJkZGRhYjZjMjdjNjc0MGFhMDY1MDIyMDE3Y2E4YzY0ZTBkM2Y3MWIwOGZkMmRlNWNmNDczOTI5MDk5M2RmMGI1NjIzZTRlMmUwMzc5MzM4NzIxMGZjMzQifQ==

```

- **Responses:**
  - ตัวอย่าง
    ```json
    [
      {
        "walkerID": 0,
        "detail": {
          "name": "string",
          "profileImage": "string",
          "verify": "string",
          "location": "string",
          "price": {
            "small": 0,
            "medium": 0,
            "big": 0
          }
        },
        "countReview": 0,
        "totalReview": 0,
        "contact": {
          "email": "string",
          "phoneNumber": "string"
        },
        "review": [
          {
            "userID": 0,
            "name": "string",
            "profileImage": "string",
            "rating": 0,
            "reviewText": "string"
          }
        ]
      },
       .....
    ]
    ```

  - `200`: การตอบสนองที่ประสบความสำเร็จพร้อมข้อมูล Dog Walker
  - `401`: การเข้าถึงไม่ได้รับอนุญาต

### Public Dog Walker

#### GET /api/v1/home/filter
- **สรุป:** ดึงข้อมูล Dog Walker สาธารณะตามเงื่อนไขที่ระบุ
- **พารามิเตอร์:**
  - `name`, `verify`, `location`, `pSmall`, `pMedium`, `pBig`, `max` (Query, ไม่บังคับ): พารามิเตอร์สำหรับกรองผลลัพธ์
- **Responses:**
  - ตัวอย่าง
    ```json
    [
      {
        "walkerID": 0,
        "detail": {
          "name": "string",
          "profileImage": "string",
          "verify": "string",
          "location": "string",
          "price": {
            "small": 0,
            "medium": 0,
            "big": 0
          }
        }
      },
       .....
    ]
    ```

  - `200`: ข้อมูลสำหรับผู้ใช้ที่ไม่มีบัญชีตามเงื่อนไขที่ระบุ
  - `401`: การเข้าถึงไม่ได้รับอนุญาต

## User

### User Sign-In

#### GET /api/v1/auth/sign-in/{username}
- **สรุป:** กระทำการเข้าสู่ระบบผู้ใช้
- **พารามิเตอร์:**
  - `Signature` (Header, จำเป็น): ลายเซ็นที่ใช้สำหรับการตรวจสอบความสมบูรณ์ข้อมูล
  - `username` (Path, จำเป็น): ชื่อผู้ใช้
- **Responses:**
  - `200`: การตอบสนองที่ประสบความสำเร็จพร้อมข้อมูล Token
  - `401`: การเข้าถึงไม่ได้รับอนุญาต

### User Sign-Up

#### PATCH /api/v1/auth/sign-up
- **สรุป:** ลงทะเบียนผู้ใช้ใหม่
- **พารามิเตอร์:**
  - `UserName` (Header, จำเป็น): ชื่อผู้ใช้
- **Request Body:**
  - `UserProfileForm` (JSON, จำเป็น): ข้อมูลการลงทะเบียนผู้ใช้ที่ถูกเข้ารหัส
- **Responses:**
  - `200`: การตอบสนองที่ประสบความสำเร็จพร้อมข้อมูล Token
  - `401`: การเข้าถึงไม่ได้รับอนุญาต

### User Booking

#### POST /api/v1/auth/user/booking
- **สรุป:** วิธีทำการจองบริการเดินสุนัข
- **พารามิเตอร์:**
  - `Access-Token` (Header, จำเป็น): Token สำหรับการยืนยันตัวตน
- **Request Body:**
  - `DogWalkBookings` (JSON, จำเป็น): ข้อมูลการจอง
- **Responses:**
  - `200`: การตอบสนองสำหรับผลการจอง
  - `401`: การเข้าถึงไม่ได้รับอนุญาต

### User Upload

#### POST /api/v1/auth/user/upload
- **สรุป:** วิธีการอัปโหลดไฟล์
- **พารามิเตอร์:**
  - `Access-Token` (Header, จำเป็น): Token สำหรับการอัปโหลด
- **Request Body:**
  - `multipart/form-data` (จำเป็น): ไฟล์ที่จะอัปโหลด
- **Responses:**
  - `200`: การตอบสนองสำหรับผลการอัปโหลด
  - `401`: การเข้าถึงไม่ได้รับอนุญาต

## Dog

### Get Dogs

#### GET /api/v1/dogs
- **สรุป:** ดึงข้อมูลเกี่ยวกับสุนัข
- **Responses:**
  - `200`: การตอบสนองที่ประสบความสำเร็จพร้อมข้อมูลสุนัข

### Dog Image

#### GET /api/v1/dog/{name}/image/{fingerprint}/{file}
- **สรุป:** แสดงรูปภาพโปรไฟล์ของสุนัข
- **พารามิเตอร์:**
  - `name`, `fingerprint`, `file` (Path, จำเป็น): ชื่อสุนัข, ลายนิ้วมือของรูปภาพ, และชื่อไฟล์
- **Responses:**
  - `200`: การตอบสนองพร้อมข้อมูลรูปภาพ

## Home

## Filter

### Private Filter

#### GET /api/v1/auth/home/filter
- **สรุป:** ดึงข้อมูล Dog Walker จากฐานข้อมูล
- **พารามิเตอร์:**
  - `Access-Token` (Header, จำเป็น): Token สำหรับการยืนยันตัวตน
  - `name`, `verify`, `location`, `pSmall`, `pMedium`, `pBig`, `max` (Query, ไม่บังคับ): พารามิเตอร์สำหรับกรองผลลัพธ์
- **Responses:**
  - `200`: การตอบสนองที่ประสบความสำเร็จพร้อมข้อมูล Dog Walker
  - `401`: การเข้าถึงไม่ได้รับอนุญาต

### Public Filter

#### GET /api/v1/home/filter
- **สรุป:** ดึงข้อมูล Dog Walker สาธารณะตามเงื่อนไขที่ระบุ
- **พารามิเตอร์:**
  - `name`, `verify`, `location`, `pSmall`, `pMedium`, `pBig`, `max` (Query, ไม่บังคับ): พารามิเตอร์สำหรับกรองผลลัพธ์
- **Responses:**
  - `200`: ข้อมูลสำหรับผู้ใช้ที่ไม่มีบัญชีตามเงื่อนไขที่ระบุ
  - `401`: การเข้าถึงไม่ได้รับอนุญาต

## etc

### Open Image URL

#### GET /api/v1/user/{name}/image/{fingerprint}
- **สรุป:** วิธีการเปิด URL ของรูปภาพผู้ใช้
- **พารามิเตอร์:**
  - `name`, `fingerprint` (Path, จำเป็น): ชื่อผู้ใช้และลายนิ้วมือของรูปภาพ
- **Responses:**
  - `200`: การตอบสนองพร้อมข้อมูลไฟล์รูปภาพ

### Index

#### GET /jungmha
- **สรุป:** เส้นทางดัชนี
- **Responses:**
  - `200`: การตอบสนองดัชนี

---

**Legend:**
- ✔️: API path ready for use.
- ❌: API path not ready for use.
