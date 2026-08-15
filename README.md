# 如何使用 java 做出 PTT 

本專案實作一個仿 PTT 互動模式的社群留言板。系統支援訪客免登入瀏覽最新文章與留言，並透過註冊與身分驗證機制，控管使用者的發文與留言權限。架構上採用前後端分離，並全面導入容器化，確保開發與生產環境的一致性。

## 快速啟動 (Quick Start)
本專案將前端、後端以及資料庫完全 Docker 化，並配置 Nginx 作為反向代理伺服器。只需透過 Docker 即可一鍵啟動完整服務：

1. 克隆此專案至本地端。

2. 於 IDE 終端機或專案根目錄執行以下指令：

```bash
   docker-compose up --build
```

啟動成功後，打開瀏覽器訪問 http://localhost:5173 即可開始使用。


## 核心功能與技術實作 (Features & Technical Details)
免登入瀏覽 (Guest Browsing)

功能描述：訪客進入首頁即可無縫瀏覽最新的熱門文章與留言紀錄。

技術實作：前端透過 RESTful API 的 GET 請求後端路由，載入初始化的 post 與 comment 列表資料。

使用者註冊與驗證 (Authentication)

功能描述：使用者須透過手機號碼與密碼進行註冊及登入，取得身分驗證後方可進行互動。

技術實作：前端發送 POST 請求至後端註冊路由，後端接收資料後，觸發資料庫端的 Stored Procedure (預存程序) 安全地將註冊資訊寫入 user 資料表。

發文與留言互動 (Posting & Commenting)

功能描述：經權限校驗通過的登入使用者，可發表新文章或於現有文章下進行留言。

技術實作：由後端驗證使用者 Session/Token 後，透過呼叫對應的 Stored Procedure，將發文者 ID、內容文本以及生成的時間戳記，分別關聯並插入至 post 與 comment 資料表中，確保資料的一致性與關聯性。
