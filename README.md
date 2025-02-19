# 网络应用开发课程实验——购物网站
# 电子商务网站

一个基于 **Vue 2**、**Spring Boot** 和 **Element UI** 的前后端分离电商平台，部署在 **阿里云** 上。

## 技术栈

- **前端**: Vue 2, Element UI, Nginx
- **后端**: Spring Boot, Tomcat 8, MyBatis
- **数据库**: MySQL 8.0.24, 阿里云 OSS 用于商品图片存储

## 功能

### 用户认证
- 基于 JWT 的登录和注册，支持基于角色的访问控制（顾客或商家）。

### 顾客功能
- **商品展示**: 分页显示商品列表，支持筛选（价格、属性、按名称/店铺搜索）。
- **购物车**: 添加商品，计算总价，并生成订单。
- **订单管理**: 创建订单、支付和查看历史订单。

### 商家功能
- **商品管理**: 添加、更新、删除商品，上传商品图片。
- **订单管理**: 查看和管理订单，处理发货。
- **数据分析**: 销售数据、退款率和商品数据的可视化图表。

### 管理员功能
- **日志监控**: 通过 AOP 记录用户操作日志，系统管理员可查看。

## 部署

### 后端
- 克隆仓库并配置 `application.properties` 文件中的 MySQL 配置。
- 启动 Spring Boot 应用: `mvn spring-boot:run`

### 前端
- 进入 `shopvue/` 目录。
- 安装依赖: `npm install`
- 启动前端服务器: `npm run serve`

## 鸣谢

- 感谢 Vue.js、Spring Boot、Element UI 和阿里云。

# E-commerce Platform

A front-end and back-end separated e-commerce platform built with **Vue 2**, **Spring Boot**, and **Element UI**, deployed on **Alibaba Cloud**.

## Tech Stack

- **Frontend**: Vue 2, Element UI, Nginx
- **Backend**: Spring Boot, Tomcat 8, MyBatis
- **Database**: MySQL 8.0.24, Aliyun OSS for product images

## Features

### User Authentication
- JWT-based login and registration with role-based access (Customer or Merchant).

### Customer Features
- **Product Display**: Paginated product list with filters (price, attributes, search by name/store).
- **Shopping Cart**: Add products, calculate total price, and create orders.
- **Order Management**: Order creation, payment, and history tracking.

### Merchant Features
- **Product Management**: Add, update, delete products, upload images.
- **Order Management**: View and manage orders, process shipments.
- **Data Analytics**: Sales, refund rates, and product data with visual charts.

### Admin Features
- **Log Monitoring**: User operation logging via AOP, viewable by system admins.

## Deployment

### Backend
- Clone repo and configure `application.properties` for MySQL.
- Run Spring Boot app: `mvn spring-boot:run`

### Frontend
- Navigate to `shopvue/` directory.
- Install dependencies: `npm install`
- Start the server: `npm run serve`

## Acknowledgements

- Thanks to Vue.js, Spring Boot, Element UI, and Alibaba Cloud.
