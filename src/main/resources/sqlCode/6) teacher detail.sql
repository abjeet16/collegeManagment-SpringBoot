#6nd to execute
use collegemanagement;
CREATE TABLE teacher_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    department VARCHAR(50),

    FOREIGN KEY (user_id) REFERENCES users(uucms_id) ON DELETE CASCADE
);
