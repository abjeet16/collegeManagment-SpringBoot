#4th to execute
use collegemanagement;
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

