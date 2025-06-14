#7th to execute
use collegemanagement;
CREATE TABLE assigned_teacher (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id INT NOT NULL,
    class_id INT NOT NULL,
    subject_id INT NOT NULL,

    FOREIGN KEY (teacher_id) REFERENCES teacher_details(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subject(id) ON DELETE CASCADE
);
