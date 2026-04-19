# 🏋️ Gym Management Ecosystem

A full-stack gym management system built end-to-end — cross-platform mobile app, RESTful backend, and an admin web dashboard. Designed to handle real-world gym operations: member management, role-based access, and automated membership lifecycles.

---

## 📱 Mobile App — Kotlin Multiplatform (KMP)

Built with **Kotlin Multiplatform + Compose Multiplatform**, sharing 90%+ of business logic across Android and iOS from a single codebase.

**Tech Stack**
- Kotlin Multiplatform (KMP)
- Compose Multiplatform (UI)
- Koin (Dependency Injection)
- Ktor Client (API communication)
- Clean Architecture + MVVM

**Features**
- Member login and profile management
- View membership status and expiry
- Role-based screens (Admin / Trainer / Member)
- Real-time data sync with backend

---

## 🛡️ Backend — NestJS + PostgreSQL

A production-grade REST API built with **NestJS**, backed by **PostgreSQL** via **Prisma ORM**.

**Tech Stack**
- NestJS (Node.js framework)
- PostgreSQL + Prisma ORM
- JWT Authentication
- Role-Based Access Control (RBAC)
- TypeScript

**Features**
- JWT-secured authentication with refresh token support
- RBAC system for Admin, Trainer, and Member roles
- Membership lifecycle automation (active → expiring → expired)
- 10+ REST API endpoints covering users, memberships, billing, and attendance
- Morgan request logging for debugging

---

## 💻 Admin Dashboard — Next.js

A web-based dashboard built with **Next.js** for gym admins to manage members, memberships, and revenue in real time.

**Tech Stack**
- Next.js (React framework)
- Recharts (data visualization)
- Tailwind CSS
- TypeScript

**Features**
- Real-time membership lifecycle overview
- Revenue metrics and charts via Recharts
- Member CRUD — add, edit, deactivate members
- Role-based UI visibility

---

## 🏗️ Architecture Overview

```
full_stack_gymApp/
├── GymApp/          → KMP mobile app (Android + iOS)
├── backend/         → NestJS REST API
└── dashboard/       → Next.js admin web dashboard
```

**Security:** JWT tokens secured with RBAC at both API and UI level.  
**Logging:** Unified logging across all three layers — Logcat (mobile), Morgan (backend), DevTools (dashboard).

---

## 🚀 Running Locally

### 1. Start PostgreSQL
```bash
brew services start postgresql@17
```

### 2. Backend (NestJS)
```bash
cd backend
npx prisma generate
npx prisma db push
npm run start:dev
# Runs at http://localhost:3000
```

### 3. Admin Dashboard (Next.js)
```bash
cd dashboard
npm run dev
# Runs at http://localhost:3001
```

### 4. Mobile App (Android)
- Open `./GymApp` in Android Studio
- Select the `composeApp` run configuration
- Launch an Android Emulator
- App connects to backend via `http://10.0.2.2:3000`

---

## 👨‍💻 Built By

**Muhammed Ansil** — Android & KMP Developer  
[LinkedIn](https://linkedin.com/in/muhammed-ansil-810212269) · [GitHub](https://github.com/AnsilKM)
