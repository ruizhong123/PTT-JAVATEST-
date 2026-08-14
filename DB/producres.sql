
DELIMITER //
CREATE PROCEDURE sp_register(IN p_phone VARCHAR(20), IN p_password VARCHAR(255))
BEGIN
    INSERT INTO users (user_name, password) VALUES (p_phone, p_password);
END //
DELIMITER ;