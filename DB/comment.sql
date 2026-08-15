-- 1. 建立留言資料表
CREATE TABLE IF NOT EXISTS comments (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL
);

-- 2. 建立新增留言的預儲程序
DROP PROCEDURE IF EXISTS sp_create_comment;

DELIMITER //

CREATE PROCEDURE sp_create_comment(
    IN p_post_id INT,
    IN p_user_id VARCHAR(50), -- 留言者的 ID 或手機號碼
    IN p_content TEXT
)
BEGIN
    INSERT INTO comments (post_id, user_id, content, created_at) 
    VALUES (p_post_id, p_user_id, p_content, NOW());
END //

DELIMITER ;