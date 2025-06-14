#9th to execute
use collegemanagement;
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


