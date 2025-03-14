USE my_notes;

CREATE TABLE users (
    user_id INT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
);


CREATE VIEW student_attendance_summary AS
SELECT
    a.student_id,
    a.class_id,
    a.subject_id,
    CONCAT(u.first_name, ' ', u.last_name) AS name,  -- Use CONCAT() for MySQL
    COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) AS total_present,
    COUNT(CASE WHEN a.status = 'ABSENT' THEN 1 END) AS total_absent,
    COUNT(*) AS total_classes
FROM attendance a
JOIN users u ON a.student_id = u.uucms_id  -- Join with users table to get names
GROUP BY a.student_id, a.class_id, a.subject_id, u.first_name, u.last_name;
