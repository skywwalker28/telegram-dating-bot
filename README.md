# Telegram Dating Bot

## Overview
Telegram Dating Bot is a Spring Boot application that allows users to register, browse other profiles, and interact through likes and dislikes. It also provides a REST API for managing user data. This project demonstrates backend development skills, database interaction, and integration with external services like Telegram.

## Features
- **User Registration**: Register through the bot by providing last name and photo.  
- **Profile Viewing**: Browse other user profiles with photos and basic information.  
- **Like/Dislike Mechanism**: Like or dislike profiles. Matches are notified in real-time.  
- **Profile Management**: View your profile, delete your account, and update information via REST API.  
- **Telegram Bot Validation**: Automatic validation of bot availability at startup.  
- **REST API**: Full CRUD operations for user data via `/api/users`.

## Technologies Used
- Java 17+  
- Spring Boot 3  
- Spring Data JPA (MySQL)  
- Telegram Bots API  
- Maven for dependency management  
- SLF4J / Logback for logging

## Architecture
- **Controller Layer**: Handles Telegram updates and REST API requests.  
- **Service Layer**: Implements business logic for matches and profile management.  
- **Repository Layer**: Interacts with MySQL database using JPA.  
- **Utility Layer**: Provides helper classes (e.g., inline keyboards).  
- **Validation Layer**: Ensures bot availability on startup.

## Database
- **Database**: MySQL  
- **Table**: `bot_users`  
- **Fields**:  
  - `chatId` (Primary Key)  
  - `username`  
  - `firstName`  
  - `lastName`  
  - `photoFileId`

## Setup & Running
# 1. Clone the repository:
```
git clone https://github.com/yourusername/telegram-bot.git
```

# 2. Configure .env or application.properties with database credentials and Telegram bot token. Example:
```
spring.datasource.url=jdbc:mysql://localhost:3306/telegram_bot?allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

telegram.bot.token=${TELEGRAM_BOT_TOKEN}
telegram.bot.username=${TELEGRAM_BOT_USERNAME}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```
# 3.	Run MySQL locally and create a database:
```
CREATE DATABASE telegram_bot;
```
# 4.	Build and run the application:
```
mvn clean install
mvn spring-boot:run
```
# 5.	Start interacting with your bot on Telegram.
Bot Commands
- /start — register or greet user
- /view — view next profile
- /myProfile — view your own profile
- /delete — delete your account

REST API Endpoints
- GET /api/users — get all users
- GET /api/users/{chatId} — get user by ID
- POST /api/users — create new user
- PUT /api/users/{chatId} — update user
- DELETE /api/users/{chatId} — delete user

Logging & Error Handling
- Uses SLF4J for logging bot actions and errors.
- Handles invalid inputs and missing profiles gracefully.

Future Improvements
- Add pagination for profiles in /view.
- Implement advanced matching algorithm based on preferences.
- Add photo moderation or verification.
- Deploy with Docker for production-ready setup.

# 6. How to Create a Telegram Bot
1. Open Telegram and search for @BotFather.
2. Send the command /newbot.
3. Choose a display name and a unique username (must end with bot, e.g. DatingHelperBot).
4. BotFather will generate a token — save it and put it into your .env file as TELEGRAM_BOT_TOKEN.
5. Copy the username into .env as TELEGRAM_BOT_USERNAME.
