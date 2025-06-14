#1st to execute
use collegemanagement;
CREATE TABLE users (
    uucms_id VARCHAR(255) PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    phone BIGINT UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50) DEFAULT 'STUDENT'
);
