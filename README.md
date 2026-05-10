# Medify - Medication Inventory & Expense Tracker

A vertical-slice full-stack MVP based on the Medify SDD.

## Stack

- **Backend:** Java 17, Spring Boot 3.x, Spring Security, JWT, BCrypt, Spring Data JPA
- **Database:** Supabase PostgreSQL
- **Frontend:** ReactJS, Vite, Tailwind CSS, Axios
- **Architecture:** Vertical slicing by feature (`auth`, `medication`, `dashboard`, `profile`) instead of one giant controller/service/repository folder

## Implemented features

- User registration, email verification flow, login, logout
- JWT-protected API
- BCrypt password hashing
- User profile view/update
- Password change
- Profile image upload with image-only validation
- Medication CRUD
- Medication search, week/month filtering, and sorting by date/name/price
- Delete confirmation dialog
- Dashboard with current medications, recently added medications, weekly/monthly expenses, all-time total, and a simple 6-month expense review
- Supabase PostgreSQL schema
- Standard API response wrapper: `{ success, data, error, timestamp }`

> Note: The SDD has a conflict: the scope excludes social media login, but the MoSCoW list marks Google OAuth as a must-have. This package focuses on the fully working email/password + SMTP verification path. Google OAuth is not wired because it requires Google Cloud OAuth credentials and redirect configuration.

## Project structure

```txt
medify-webapp/
├── backend/                  # Spring Boot API
│   └── src/main/java/com/medify/
│       ├── auth/              # auth vertical slice
│       ├── dashboard/         # dashboard vertical slice
│       ├── medication/        # medication vertical slice
│       ├── profile/           # profile vertical slice
│       ├── security/          # JWT/security config
│       ├── user/              # user model/repo/shared DTO
│       └── common/            # API wrapper + errors
├── frontend/                 # ReactJS + Tailwind web app
│   └── src/features/
│       ├── auth/
│       ├── dashboard/
│       ├── medications/
│       └── profile/
└── database/
    └── schema.sql             # Run this in Supabase SQL editor
```

## 1. Create the Supabase database tables

1. Open your Supabase project.
2. Go to **SQL Editor**.
3. Paste and run `database/schema.sql`.

Supabase connection details usually look like this:

```txt
jdbc:postgresql://db.YOUR_PROJECT_REF.supabase.co:5432/postgres?sslmode=require
```

If you use the Supabase pooler, use the pooler host/port from your Supabase dashboard instead.

## 2. Configure backend environment

Copy the example file:

```bash
cd backend
cp .env.example .env
```

Edit `.env`:

```bash
SUPABASE_DB_URL=jdbc:postgresql://db.YOUR_PROJECT_REF.supabase.co:5432/postgres?sslmode=require
SUPABASE_DB_USERNAME=postgres
SUPABASE_DB_PASSWORD=your-supabase-db-password
JWT_SECRET=replace-this-with-a-very-long-secret-at-least-32-characters
FRONTEND_URL=http://localhost:5173
APP_DEV_MODE=true
```

Optional SMTP email verification:

```bash
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password
SMTP_FROM=your-email@gmail.com
```

If SMTP is not configured, registration still works in dev mode and returns a verification link in the API response.

## 3. Run the backend

On Windows PowerShell:

```powershell
cd backend
$env:SUPABASE_DB_URL="jdbc:postgresql://db.YOUR_PROJECT_REF.supabase.co:5432/postgres?sslmode=require"
$env:SUPABASE_DB_USERNAME="postgres"
$env:SUPABASE_DB_PASSWORD="your-password"
$env:JWT_SECRET="replace-this-with-a-very-long-secret-at-least-32-characters"
mvn spring-boot:run
```

On macOS/Linux:

```bash
cd backend
export SUPABASE_DB_URL="jdbc:postgresql://db.YOUR_PROJECT_REF.supabase.co:5432/postgres?sslmode=require"
export SUPABASE_DB_USERNAME="postgres"
export SUPABASE_DB_PASSWORD="your-password"
export JWT_SECRET="replace-this-with-a-very-long-secret-at-least-32-characters"
mvn spring-boot:run
```

Backend runs at:

```txt
http://localhost:8080/api/v1
```

## 4. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at:

```txt
http://localhost:5173
```

## 5. Quick manual test

1. Register a new account.
2. If SMTP is not configured, copy the dev verification link shown on the register page.
3. Open the verification link.
4. You should be logged in and redirected to the dashboard.
5. Go to **Medications** and add a medication.
6. Return to **Dashboard**. Weekly/monthly totals should update.
7. Edit and delete the medication to test recalculation.

## API overview

Base URL:

```txt
/api/v1
```

Public:

```txt
POST /auth/register
GET  /auth/verify-email?token=...
POST /auth/login
POST /auth/resend-verification
POST /auth/logout
```

Protected:

```txt
GET    /dashboard
GET    /medications
GET    /medications/recent
GET    /medications/{id}
POST   /medications
PUT    /medications/{id}
DELETE /medications/{id}
GET    /users/profile
PUT    /users/profile
PUT    /users/profile/password
POST   /users/profile/image
```

## Sample medication payload

```json
{
  "medicineName": "Hydroxychloroquine",
  "brandName": "Plaquenil",
  "dosage": "200mg",
  "purpose": "SLE",
  "quantity": 20,
  "price": 45.50,
  "purchaseDate": "2026-03-15",
  "notes": "Take after meals"
}
```

## Notes for deployment

- Deploy the backend to Render/Railway and set the same environment variables.
- Deploy the frontend to Vercel/Netlify and set:

```txt
VITE_API_BASE_URL=https://your-backend-domain.com/api/v1
```

- In backend env, set:

```txt
FRONTEND_URL=https://your-frontend-domain.com
APP_DEV_MODE=false
```
