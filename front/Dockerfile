# 將這裡的 node:18 改成 node:20
FROM node:20 AS build-stage
WORKDIR /app

# 先複製相依性描述檔
COPY package*.json ./

# 乾淨安裝所有套件
RUN npm install

# 再複製剩餘的源程式碼
COPY . .

# 執行編譯
RUN npm run build

# 階段二：Nginx 部署
FROM nginx:stable-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build-stage /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]