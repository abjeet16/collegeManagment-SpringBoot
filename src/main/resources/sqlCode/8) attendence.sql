#8 to execute
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
