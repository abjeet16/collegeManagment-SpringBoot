#5th to execute
use collegemanagement;
CREATE TABLE subject (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject_id VARCHAR(100) UNIQUE,
    subject_name VARCHAR(255),
    semester INT CHECK (semester BETWEEN 1 AND 8),
    course_id INT,

    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);
