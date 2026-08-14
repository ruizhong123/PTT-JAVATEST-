package social.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private JdbcTemplate jdbcTemplate;

    // 透過建構子注入 JdbcTemplate
    public PostController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. 發文
    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody Map<String, String> request) {
    String userId = request.get("userId"); // 從前端獲取使用者 ID
    String content = request.get("content");
    
    // 呼叫預儲程序：將 userId, content 存入資料庫，時間讓資料庫自動處理 (NOW())
    String sql = "CALL sp_create_post(?, ?)";
    jdbcTemplate.update(sql, userId, content);
    
    return ResponseEntity.status(201).body("發文成功");
    }

    // 2. 編輯發文
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(@PathVariable int id, @RequestBody Map<String, String> request) {
        String content = request.get("content");
        
        if (content == null || content.isEmpty()) { 
            return ResponseEntity.badRequest().body("內容不得為空");
        }

        try {
            // 呼叫預儲程序：傳入貼文 ID 與新內容
            String sql = "CALL sp_update_post(?, ?)";
            
            // jdbcTemplate.update 會回傳受影響的資料筆數
            int affectedRows = jdbcTemplate.update(sql, id, content);

            if (affectedRows > 0) {
                return ResponseEntity.ok("發文更新成功");
            } else {
                return ResponseEntity.status(404).body("找不到該貼文，更新失敗");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("系統錯誤，更新失敗");
        }    
    }

    // 3. 刪除發文
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable int id) {
        try {
            // 呼叫預儲程序：傳入貼文 ID
            String sql = "CALL sp_delete_post(?)";
            int affectedRows = jdbcTemplate.update(sql, id);

            if (affectedRows > 0) {
                return ResponseEntity.ok("發文刪除成功");
            } else {
                return ResponseEntity.status(404).body("找不到該貼文，刪除失敗");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("系統錯誤，刪除失敗");
        }
    }

    // 4. 針對特定貼文新增留言
    @PostMapping("/{postId}/comments")
    public ResponseEntity<String> createComment(@PathVariable int postId, @RequestBody Map<String, String> request) {
        String commentContent = request.get("content");
        return ResponseEntity.status(201).body("留言成功");
    }
}