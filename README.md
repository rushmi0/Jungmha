# Jungmha API Documentation

This document provides information on the Jungmha API, a service for connecting dog owners with dog walkers. Below is a list of available API paths, their functionalities, and symbols indicating whether the API is ready for use.

## Table of Contents
- [Authentication](#authentication)
- [Dog Walker](#dog-walker)
  - [Private Dog Walker](#private-dog-walker)
  - [Public Dog Walker](#public-dog-walker)
- [User](#user)
  - [User Sign-In](#user-sign-in)
  - [User Sign-Up](#user-sign-up)
  - [User Booking](#user-booking)
  - [User Upload](#user-upload)
- [Dog](#dog)
  - [Get Dogs](#get-dogs)
  - [Dog Image](#dog-image)
- [Home](#home)
  - [Filter](#filter)
- [Miscellaneous](#miscellaneous)
  - [Open Image URL](#open-image-url)
  - [Index](#index)

## Authentication

### Access Token
- **Description:** This API uses an access token for authentication.
- **Scopes:**
  - `view-only`: View-only access
  - `full-control`: Full control access

## Dog Walker

### Private Dog Walker

#### GET /api/v1/auth/home/filter
- **Summary:** Retrieve Dog Walker information from the database.
- **Parameters:**
  - `Access-Token` (Header, Required): Token for authentication.
  - `name` (Query, Optional): Name of the Dog Walker.
  - `verify` (Query, Optional): Verification status (can be omitted).
  - `location` (Query, Optional): Location where the Dog Walker works.
  - `pSmall` (Query, Optional): Price for walking small dogs.
  - `pMedium` (Query, Optional): Price for walking medium dogs.
  - `pBig` (Query, Optional): Price for walking big dogs.
  - `max` (Query, Optional): Maximum number of records to display (default is Integer.MAX_VALUE).
- **Responses:**
  - `200`: Successful response with Dog Walker information.
  - `401`: Unauthorized access.

### Public Dog Walker

#### GET /api/v1/home/filter
- **Summary:** Retrieve public Dog Walker information based on specified conditions.
- **Parameters:**
  - `name` (Query, Optional): Name of the Dog Walker.
  - `verify` (Query, Optional): Verification status (can be omitted).
  - `location` (Query, Optional): Location where the Dog Walker works.
  - `pSmall` (Query, Optional): Minimum price for small dogs.
  - `pMedium` (Query, Optional): Minimum price for medium dogs.
  - `pBig` (Query, Optional): Minimum price for big dogs.
  - `max` (Query, Optional): Maximum number of records to display (default is Integer.MAX_VALUE).
- **Responses:**
  - `200`: Data for users without an account based on the specified conditions.
  - `401`: Unauthorized access.

## User

### User Sign-In

#### GET /api/v1/auth/sign-in/{username}
- **Summary:** Perform user sign-in.
- **Parameters:**
  - `Signature` (Header, Required): Signature used for data integrity verification.
  - `username` (Path, Required): User's username.
- **Responses:**
  - `200`: Successful sign-in response with token data.
  - `401`: Unauthorized access.

### User Sign-Up

#### PATCH /api/v1/auth/sign-up
- **Summary:** Register a new user.
- **Parameters:**
  - `UserName` (Header, Required): User's username.
- **Request Body:**
  - `UserProfileForm` (JSON, Required): Encrypted user registration data.
- **Responses:**
  - `200`: Registration successful response with token data.
  - `401`: Unauthorized access.

### User Booking

#### POST /api/v1/auth/user/booking
- **Summary:** Method for making a dog walking service booking.
- **Parameters:**
  - `Access-Token` (Header, Required): Token for authentication.
- **Request Body:**
  - `DogWalkBookings` (JSON, Required): Booking information.
- **Responses:**
  - `200`: Response for the booking result.
  - `401`: Unauthorized access.

### User Upload

#### POST /api/v1/auth/user/upload
- **Summary:** Method for uploading files.
- **Parameters:**
  - `Access-Token` (Header, Required): Token for upload authorization.
- **Request Body:**
  - `multipart/form-data` (Required): File to upload.
- **Responses:**
  - `200`: Upload result response.
  - `401`: Unauthorized access.

## Dog

### Get Dogs

#### GET /api/v1/dogs
- **Summary:** Retrieve information about dogs.
- **Responses:**
  - `200`: Successful response with dog information.

### Dog Image

#### GET /api/v1/dog/{name}/image/{fingerprint}/{file}
- **Summary:** Display profile images of dogs.
- **Parameters:**
  - `name` (Path, Required): Dog's name.
  - `fingerprint` (Path, Required): Image fingerprint.
  - `file` (Path, Required): Image file name.
- **Responses:**
  - `200`: Response with image data.

## Home

### Filter

#### GET /api/v1/home/filter
- **Summary:** Method for retrieving public Dog Walker data based on specified conditions.
- **Parameters:**
  - `name` (Query, Optional): Name of the Dog Walker.
  - `verify` (Query, Optional): Verification status (can be omitted).
  - `location` (Query, Optional): Location where the Dog Walker works.
  - `pSmall` (Query, Optional): Minimum price for small dogs.
  - `pMedium` (Query, Optional): Minimum price for medium dogs.
  - `pBig` (Query, Optional): Minimum price for big dogs.
  - `max` (Query, Optional): Maximum number of records to display (default is Integer.MAX_VALUE).
- **Responses:**
  - `200`: Data for users without an account based on the specified conditions.
  - `401`: Unauthorized access.

## Miscellaneous

### Open Image URL

#### GET /api/v1/user/{name}/image/{fingerprint}
- **Summary:** Method for opening the URL of a user's image.
- **Parameters:**
  - `name` (Path, Required): User's name.
  - `fingerprint` (Path, Required): Image fingerprint.
- **Responses:**
  - `200`: Response with image file data.

### Index

#### GET /jungmha
- **Summary:** Index endpoint.
- **Responses:**
  - `200`: Index response.

---

**Legend:**
- ✔️: API path ready for use.
- ❌: API path not ready for use.
