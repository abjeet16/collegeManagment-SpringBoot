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

CREATE TABLE courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE classes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    batch_year INT NOT NULL,
    section VARCHAR(10) NOT NULL,
    current_semester INT NOT NULL CHECK (current_semester between 1 and 8),

    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

CREATE TABLE student_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    class_id INT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(uucms_id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE
);

DELIMITER $$

CREATE TRIGGER delete_user_after_student_details
AFTER DELETE ON student_details
FOR EACH ROW
BEGIN
  DELETE FROM users WHERE uucms_id = OLD.user_id;
END$$

DELIMITER ;


CREATE TABLE subject (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject_id VARCHAR(100) UNIQUE,
    subject_name VARCHAR(255),
    semester INT CHECK (semester BETWEEN 1 AND 8),
    course_id INT,

    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

CREATE TABLE teacher_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    department VARCHAR(50),

    FOREIGN KEY (user_id) REFERENCES users(uucms_id) ON DELETE CASCADE
);

CREATE TABLE assigned_teacher (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id INT NOT NULL,
    class_id INT NOT NULL,
    subject_id INT NOT NULL,

    FOREIGN KEY (teacher_id) REFERENCES teacher_details(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subject(id) ON DELETE CASCADE
);

CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(255) NOT NULL,
    class_id BIGINT NOT NULL,
    semester INT NOT NULL,
    subject_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    schedule_period INT NOT NULL
);

 CREATE VIEW student_attendance_summary AS
SELECT
    a.student_id,
    a.semester,
    a.class_id,
    a.subject_id,
    CONCAT(u.first_name, ' ', u.last_name) AS name,  -- Corrected concatenation
    COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) AS total_present,
    COUNT(CASE WHEN a.status = 'ABSENT' THEN 1 END) AS total_absent,
    COUNT(*) AS total_classes,
    (COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 100.0 / COUNT(*)) AS attendance_percentage  -- Attendance Percentage
FROM attendance a
JOIN users u ON a.student_id = u.uucms_id
GROUP BY a.student_id,a.semester,a.class_id, a.subject_id, u.first_name, u.last_name;
