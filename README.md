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
- Navigate to `frontend/` directory.
- Install dependencies: `npm install`
- Start the server: `npm run serve`

## Acknowledgements

- Thanks to Vue.js, Spring Boot, Element UI, and Alibaba Cloud.
