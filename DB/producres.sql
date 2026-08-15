CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS posts (
    post_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL
);



DROP PROCEDURE IF EXISTS sp_register;

DELIMITER //
CREATE PROCEDURE sp_register(IN p_phone VARCHAR(20), IN p_password VARCHAR(255))
BEGIN
    INSERT INTO users (user_name, password) VALUES (p_phone, p_password);
END //
DELIMITER ;