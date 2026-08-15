DELIMITER //

-- 1. 新增貼文 (對應 createPost)
CREATE PROCEDURE sp_create_post(
    IN p_user_id VARCHAR(50), 
    IN p_content TEXT
)
BEGIN
    INSERT INTO posts (user_id, content, created_at) 
    VALUES (p_user_id, p_content, NOW());
END //


-- 2. 編輯貼文 (對應 updatePost)
CREATE PROCEDURE sp_update_post(
    IN p_post_id INT, 
    IN p_content TEXT
)
BEGIN
    UPDATE posts 
    SET content = p_content 
    WHERE post_id = p_post_id;
END //


-- 3. 刪除貼文 (對應 deletePost)
CREATE PROCEDURE sp_delete_post(
    IN p_post_id INT
)
BEGIN
    DELETE FROM posts 
    WHERE post_id = p_post_id;
END //

-- 4. 取得所有貼文
CREATE PROCEDURE sp_get_posts()
BEGIN
    SELECT post_id AS id, user_id, content, created_at 
    FROM posts 
    ORDER BY created_at DESC;
END //



DELIMITER ;