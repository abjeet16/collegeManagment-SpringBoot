#3th to execute
use collegemanagement;
CREATE TABLE classes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    batch_year INT NOT NULL,
    section VARCHAR(10) NOT NULL,
    current_semester INT NOT NULL,

    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);
