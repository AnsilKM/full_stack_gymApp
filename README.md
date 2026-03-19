# 🏋️ Gym Management SaaS - Setup & Execution Guide

This document provides the definitive steps to run the complete stack (Backend, Web Dashboard, and Mobile App) on your local machine.

---

## 1. 🗄️ Start the Database
The backend requires **PostgreSQL** to be running.
```bash
brew services start postgresql@17
```

---

## 2. 🛡️ Backend API (NestJS)
Run the following in a dedicated terminal window:

```bash
# Navigate to backend directory
cd backend

# Sync database schema and generate Prisma client
npx prisma generate
npx prisma db push

# Start the server in watch mode
npm run start:dev
```
*   **Endpoint:** `http://localhost:3000`
*   **Logs:** Request logs will appear in the terminal via `morgan`.

---

## 3. 💻 Admin Web Dashboard (Next.js)
Run the following in a second dedicated terminal window:

```bash
# Navigate to dashboard directory
cd dashboard

# Start the development server (configured for port 3001)
npm run dev
```
*   **URL:** `http://localhost:3001`
*   **API Connection:** Automatically configured to reach the backend at `localhost:3000`.
*   **Browser Logs:** Open DevTools (F12) to see `[API REQUEST]` and `[API SUCCESS]` logs.

---

## 4. 📱 Mobile App (Android)
1.  Open **Android Studio**.
2.  Open the folder: `./GymApp`
3.  Select the **`composeApp`** run configuration.
4.  Launch an **Android Emulator**.
    *   **Network:** The app is configured to use `http://10.0.2.2:3000` to connect to your Mac's backend.
    *   **Cleartext:** HTTP traffic is enabled in `AndroidManifest.xml` for development.

---

## 📝 How to Read Logs
I have implemented a unified logging system across the whole project.

### In Terminal (Backend/Dashboard)
You will see raw HTTP logs showing every endpoint reached, the method (GET/POST), and the status code.

### In Android Logcat (Mobile)
Search/Filter by the following tags:
*   `API_LOG:` — Shows raw network requests, headers, and bodies.
*   `APP_LOG:` — Shows application events like "Login Successful", "Deleting Member", etc.

### In Browser Console (Dashboard)
Look for objects prefixed with:
*   `[API REQUEST]`
*   `[API SUCCESS]`
*   `[API ERROR]`

---
