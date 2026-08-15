package social.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth") //  必須確保這行存在
public class AuthController {
    
    
    private JdbcTemplate jdbcTemplate;
    
    // 透過建構子注入 JdbcTemplate
    public AuthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


// 註冊路由器
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> request) {
        
        String phone = request.get("phone");
        String password = request.get("password");

        if (phone == null || password == null || phone.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest().body("手機號碼與密碼不得為空");
        }

        try {
            // 1. 密碼雜湊加密
            String hashedPassword = Integer.toHexString(password.hashCode());

            // 2. 直接使用標準的 INSERT 語法寫入資料庫（安全且帶有 ? 參數綁定防 SQL Injection）
            String sql = "INSERT INTO users (user_name, password) VALUES (?, ?)";
            jdbcTemplate.update(sql, phone, hashedPassword);

            return ResponseEntity.ok("註冊成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("註冊失敗：" + e.getMessage());
        }
    }

    //  會員驗證路由器
    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody Map<String, String> request) {
        
        // 驗證邏輯
        // 1. 取得前端傳過來的手機與密碼
        String phone = request.get("phone");
        String password = request.get("password");

        if (phone == null || password == null) {
            return ResponseEntity.badRequest().body("手機號碼與密碼不得為空");
        }

        try {
            
            
            String hashedPassword = Integer.toHexString(password.hashCode());
            String sql = "SELECT COUNT(*) FROM users WHERE user_name = ? AND password = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, phone, hashedPassword);

            // 4. 判斷驗證結果
            if (count != null && count > 0) {
                // 驗證成功：代表這個人帳密正確，是合法使用者
                return ResponseEntity.ok("登入驗證成功");
            } else {
                // 驗證失敗：帳號或密碼錯誤
                return ResponseEntity.status(401).body("驗證失敗：手機號碼或密碼錯誤");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("驗證失敗：可能是資料庫連線錯誤");
        }
                
    }
    
}
