<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';
import './style.css'; // 引入外部 CSS 檔案

interface Comment {
  id: number;
  user_id: string;
  content: string;
  created_at: string;
}

interface Post {
  id: number;
  user_id: string;
  content: string;
  created_at: string;
  comments: Comment[];
  newCommentContent?: string;
  isEditing?: boolean;    // 新增：控制是否處於編輯狀態
  editContent?: string;   // 新增：暫存編輯中的內容
}

const posts = ref<Post[]>([]);
const isLoggedIn = ref<boolean>(false);
const currentPhone = ref<string>('');

const loginPhone = ref<string>('');
const loginPassword = ref<string>('');
const loginError = ref<string>('');
const registerSuccessMsg = ref<string>(''); // 註冊成功訊息

const newPostContent = ref<string>('');

// 取得所有貼文
const fetchPosts = async () => {
  try {
    const response = await axios.get('/api/posts');
    posts.value = response.data;
  } catch (error) {
    console.error('取得貼文失敗', error);
  }
};

onMounted(() => {
  fetchPosts();
});

// 1. 會員註冊 -> 對應後端 /api/auth/register
const handleRegister = async () => {
  loginError.value = '';
  registerSuccessMsg.value = '';
  try {
    const response = await axios.post('/api/auth/register', {
      phone: loginPhone.value,
      password: loginPassword.value
    });
    if (response.status === 200) {
      registerSuccessMsg.value = '註冊成功！請直接點擊登入';
    }
  } catch (error: any) {
    loginError.value = error.response?.data || '註冊失敗';
  }
};

// 2. 會員登入驗證 -> 對應後端 /api/auth/verify
const handleLogin = async () => {
  loginError.value = '';
  registerSuccessMsg.value = '';
  try {
    const response = await axios.post('/api/auth/verify', {
      phone: loginPhone.value,
      password: loginPassword.value
    });
    
    if (response.status === 200) {
      isLoggedIn.value = true;
      currentPhone.value = loginPhone.value;
      loginPassword.value = '';
    }
  } catch (error: any) {
    loginError.value = error.response?.data || '登入失敗：手機號碼或密碼錯誤';
  }
};

const handleLogout = () => {
  isLoggedIn.value = false;
  currentPhone.value = '';
};

const createPost = async () => {
  if (!newPostContent.value.trim()) return;
  
  try {
    await axios.post('/api/posts', {
      userId: currentPhone.value,
      content: newPostContent.value
    });
    newPostContent.value = '';
    fetchPosts();
  } catch (error) {
    alert('發文失敗');
  }
};

// 3. 刪除貼文 (對應後端 DELETE /api/posts/{id})
const deletePost = async (postId: number) => {
  if (!confirm('確定要刪除這篇貼文嗎？')) return;
  try {
    await axios.delete(`/api/posts/${postId}`);
    fetchPosts();
  } catch (error) {
    alert('刪除失敗');
  }
};

// 4. 開始編輯貼文
const startEdit = (post: Post) => {
  post.isEditing = true;
  post.editContent = post.content;
};

// 取消編輯
const cancelEdit = (post: Post) => {
  post.isEditing = false;
  post.editContent = '';
};

// 5. 儲存更新貼文 (對應後端 PUT /api/posts/{id})
const updatePost = async (postId: number, newContent?: string) => {
  if (!newContent || !newContent.trim()) {
    alert('內容不得為空');
    return;
  }
  try {
    await axios.put(`/api/posts/${postId}`, {
      content: newContent
    });
    fetchPosts();
  } catch (error) {
    alert('更新失敗');
  }
};

const createComment = async (postId: number, commentContent?: string) => {
  if (!commentContent || !commentContent.trim()) return;

  try {
    await axios.post(`/api/posts/${postId}/comments`, {
      userId: currentPhone.value,
      content: commentContent
    });
    fetchPosts();
  } catch (error) {
    alert('留言失敗');
  }
};
</script>

<template>
  <div class="ptt-container">
    <header class="ptt-header">
      <h1>[Gossiping] PTT 簡易看板系統</h1>
      <div class="user-status">
        <span v-if="isLoggedIn">👤 目前使用者：<b>{{ currentPhone }}</b> <button @click="handleLogout" class="btn-sm">登出</button></span>
        <span v-else class="text-warn">🔒 尚未登入（僅能瀏覽，發文與留言需先登入）</span>
      </div>
    </header>

    <section v-if="!isLoggedIn" class="login-card">
      <h3>🔐 會員登入 / 註冊</h3>
      <div class="form-group">
        <input v-model="loginPhone" placeholder="請輸入手機號碼" />
      </div>
      <div class="form-group">
        <input type="password" v-model="loginPassword" placeholder="請輸入密碼" />
      </div>
      
      <div class="button-group" style="display: flex; gap: 10px;">
        <button @click="handleRegister" class="btn-secondary" style="flex: 1;">註冊帳號</button>
        <button @click="handleLogin" class="btn-primary" style="flex: 1;">會員登入</button>
      </div>

      <p v-if="registerSuccessMsg" style="color: green; margin-top: 5px;">{{ registerSuccessMsg }}</p>
      <p v-if="loginError" class="error-msg">{{ loginError }}</p>
    </section>

    <section v-else class="post-card">
      <h3>✍️ 發表新文章</h3>
      <textarea v-model="newPostContent" placeholder="這一刻想分享什麼..."></textarea>
      <button @click="createPost" class="btn-success">發布貼文</button>
    </section>

    <hr class="divider" />

    <main class="posts-list">
      <h2>📌 熱門文章列表</h2>
      <div v-for="post in posts" :key="post.id" class="post-item">
        <div class="post-header-row" style="display: flex; justify-content: space-between; align-items: center;">
          <div class="post-meta">
            <span class="post-author">作者: {{ post.user_id }}</span>
            <span class="post-time" style="margin-left: 10px;">{{ post.created_at }}</span>
          </div>

          <!-- 權限控制：只有已登入且為該貼文作者時，才顯示編輯與刪除按鈕 -->
          <div v-if="isLoggedIn && currentPhone === post.user_id" class="post-owner-actions">
            <template v-if="!post.isEditing">
              <button @click="startEdit(post)" class="btn-sm" style="margin-right: 5px;">編輯</button>
              <button @click="deletePost(post.id)" class="btn-sm" style="background-color: #ff4d4f; color: white;">刪除</button>
            </template>
          </div>
        </div>

        <!-- 貼文內容區塊：若點擊編輯則切換為輸入框 -->
        <div v-if="post.isEditing" class="edit-section" style="margin-top: 10px;">
          <textarea v-model="post.editContent" style="width: 100%; min-height: 60px;"></textarea>
          <div style="display: flex; gap: 5px; margin-top: 5px;">
            <button @click="updatePost(post.id, post.editContent)" class="btn-sm btn-success">儲存</button>
            <button @click="cancelEdit(post)" class="btn-sm">取消</button>
          </div>
        </div>
        <div v-else class="post-content" style="margin-top: 8px;">{{ post.content }}</div>

        <div class="comments-section" style="margin-top: 15px;">
          <h4>💬 留言列表</h4>
          <div v-for="comment in post.comments" :key="comment.id" class="comment-item">
            <span class="comment-author">{{ comment.user_id }}:</span>
            <span class="comment-text">{{ comment.content }}</span>
            <span class="comment-time">({{ comment.created_at }})</span>
          </div>

          <div v-if="isLoggedIn" class="comment-input-box" style="margin-top: 8px;">
            <input v-model="post.newCommentContent" placeholder="寫下你的回應..." />
            <button @click="createComment(post.id, post.newCommentContent)" class="btn-sm">留言</button>
          </div>
          <div v-else class="text-muted">
            <small>🔒 登入後即可在此留言</small>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>


