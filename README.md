# ResumeRefiner Backend

Java Spring Boot backend for ResumeRefiner SaaS.  
Implements member management, resume storage, AI review processing, billing (payments & credits), and infrastructure setup.

## Check out the Frontend counterpart of this project
[ResumeRefiner-web](https://github.com/Ozymandias089/ResumeRefiner-web)

## 🚀 Tech Stack

- **Java 21**
- **Spring Boot 4.0.0**
- **Gradle (Groovy DSL)**
- **PostgreSQL 18**
- **Redis (Session Store)**
- **Spring Data JPA**
- **Spring Security (Session Auth)**
- **Lombok**
- **SLF4J Logging**

---

## 📂 Project Structure

```
src/main/java/com/resumerefiner/resumerefinerbackend
 ├─ global
 │   ├─ config
 │   ├─ jpa
 │   ├─ shared
 ├─ member
 │   ├─ domain
 │   ├─ application
 │   ├─ infra
 │   └─ web
 ├─ resume
 ├─ review
 ├─ media
 └─ billing
```

Domain layers follow **DDD-lite + Hexagonal Port/Adapter** pattern:

- `domain` → Aggregate + ValueObject + Repository Interface (Port)
- `infra` → JPA Adapter + Spring Data Repositories
- `application` → Use Cases (services)
- `web` → REST Controllers

---

## 🗄 Database (PostgreSQL)

### Create Role & Database

```sql
CREATE ROLE id WITH LOGIN PASSWORD 'password';
CREATE DATABASE resume_refiner OWNER password;
```

### Local Connection

```
host: localhost
port: 5432
database: resume_refiner
user: id
password: password
```

---

## 🔧 Running Locally

### 1. Start PostgreSQL (Homebrew)

```bash
brew services start postgresql@18
```

### 2. Start Redis

```bash
redis-server
```

### 3. Run Backend

From IntelliJ IDEA or CLI:

```bash
./gradlew bootRun
```

---

## 🔐 Environment Variables (.env)

```
SPRING_PROFILES_ACTIVE=local

DB_HOST=localhost
DB_PORT=5432
DB_NAME=resume_refiner
DB_USER=id
DB_PASSWORD=password

REDIS_HOST=localhost
REDIS_PORT=6379

APP_VERSION=0.0.1
```

---

## 🩺 Health Check & Version

| Method | Path          | Description      |
|--------|---------------|------------------|
| GET    | /api/health   | App health       |
| GET    | /api/version  | Version metadata |

---

## 📦 Domain Aggregates

### Member
- Register (local/social)
- Login / Session auth
- Profile image
- Credits

### Resume
- Slug-based access
- Photo image
- Original text store

### Review
- AI model output store
- Sentence-level feedback (1:N)

### Billing
- Payment Aggregate
- Credit History Aggregate

---

## 🧪 Testing DB Connection

```bash
psql -U resume -d resume_refiner -c "\dt"
```

Should display tables created by JPA.

---

## 📜 License

Private project — not for distribution.
