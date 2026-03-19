# 🚀 Gym Full Stack: Deployment & Security Guide

This document provides a complete roadmap for moving your **Gym Management System** from your local machine to a live production environment.

---

## 🏗️ 1. The Production Architecture

In production, your local `localhost` setup is replaced by cloud-hosted services that talk to each other over secure, encrypted connections.

```mermaid
graph TD
    subgraph "Clients"
        A[Android App]
        B[iOS App]
        C[Web Dashboard]
    end

    subgraph "Cloud Hosting"
        D[NestJS API Service]
        E[(Managed PostgreSQL)]
        F[Next.js Web Server]
    end

    A -- "HTTPS" --> D
    B -- "HTTPS" --> D
    C -- "HTTPS" --> D
    D -- "Connection String" --> E
    F -- "Connection String" --> E
```

---

## 💾 2. Step 1: Managed Database (PostgreSQL)

You cannot use your local Postgres in production. You need a cloud-hosted database.

1.  **Provider**: I recommend [Railway](https://railway.app), [Supabase](https://supabase.com), or [Neon](https://neon.tech).
2.  **Setup**: Create a new "PostgreSQL" project.
3.  **Get Credentials**: Look for the **Connection String** or **DATABASE_URL**.
    *   *Example:* `postgresql://postgres:password@host:port/database`
4.  **Security**: Most providers already handle encryption. Ensure your DB is in the same region as your backend for speed.

---

## ⚙️ 3. Step 2: Backend Deployment (NestJS)

1.  **Host**: Deploy to [Railway](https://railway.app) or [Render](https://render.com).
2.  **Environment Variables**: You MUST set these in the hosting dashboard:
    *   `DATABASE_URL`: (The string from Step 1)
    *   `JWT_SECRET`: (A long, random string for security)
3.  **CORS Update**: In `backend/src/main.ts`, update the allowed origins to your live Web Dashboard URL.
4.  **HTTPS**: The host will automatically provide an `https://` endpoint (e.g., `https://api.yourgym.com`).

---

## 🌐 4. Step 3: Web Dashboard (Next.js)

1.  **Host**: Deploy to [Vercel](https://vercel.com).
2.  **Environment Variables**:
    *   `NEXT_PUBLIC_API_URL`: (Your live NestJS URL from Step 2)
    *   `DATABASE_URL`: (Same string from Step 1)
3.  **Build**: Connect your GitHub repository to Vercel. It will detect Next.js and deploy automatically.

---

## 📱 5. Step 4: Mobile App Configuration

Once your backend is live with HTTPS, you must update the mobile apps to point to it.

### Android
**File**: `GymApp/composeApp/src/androidMain/kotlin/com/gym/gymapp/Platform.android.kt`
```kotlin
override val baseUrl: String = "https://your-api-url.com" // MUST be https://
```

### iOS
**File**: `GymApp/composeApp/src/iosMain/kotlin/com/gym/gymapp/Platform.ios.kt`
```kotlin
override val baseUrl: String = "https://your-api-url.com" // MUST be https://
```

---

## 🔒 6. Security & HTTPS Protocols

HTTPS is mandatory. Mobile operating systems essentially block all insecure traffic.

### Why HTTPS?
1.  **Encryption**: Protects member passwords and personal data from being "sniffed" on public Wi-Fi.
2.  **Trust**: Browsers and mobile OSs flag `http` as "Not Secure."
3.  **Requirement**: Apple's **App Transport Security (ATS)** and Android's **Network Security Config** reject `http` by default.

### How to get SSL Certificates?
*   **Automatic**: Vercel, Railway, and Render generate free certificates (Let's Encrypt) automatically.
*   **Maintenance**: These certificates renew themselves—you don't have to do anything.

---

## ✅ 7. Pre-Launch Checklist

- [ ] **Database Migration**: Run `npx prisma db push` against the live production database.
- [ ] **CORS**: Check that the Backend allows the Web Dashboard domain.
- [ ] **Environment Variables**: Verify no secrets (like DB passwords) are hardcoded in the code.
- [ ] **HTTPS Verification**: Open your API URL in a browser and check for the "Padlock" icon.
- [ ] **App Build**: Compile the final APK/IPA using the production URL.

---

> [!TIP]
> **Pro Tip**: Use a tool like [Railway](https://railway.app) to host both your Database and Backend in the same project. They can communicate privately without data ever leaving their network, which is even more secure!
