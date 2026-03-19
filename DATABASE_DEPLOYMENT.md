# 🗄️ Database Deployment Guide — Gym Management Platform

This document covers the complete database architecture, schema design, local setup, and production deployment for the Gym Management SaaS platform.

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Database Schema](#database-schema)
4. [Entity Relationship Diagram](#entity-relationship-diagram)
5. [Local Development Setup](#local-development-setup)
6. [Production Deployment](#production-deployment)
7. [Migration Workflow](#migration-workflow)
8. [Backup & Recovery](#backup--recovery)
9. [Performance & Indexing](#performance--indexing)
10. [Environment Variables](#environment-variables)
11. [Troubleshooting](#troubleshooting)

---

## Overview

The platform uses **PostgreSQL** as the primary relational database, managed through **Prisma ORM**. The database serves two applications:

| Application         | Framework | Connection Method       |
| ------------------- | --------- | ----------------------- |
| Backend API         | NestJS    | Prisma Client (server)  |
| Admin Web Dashboard | Next.js   | Prisma Client (server)  |

Both applications share the same Prisma schema definition located at `backend/prisma/schema.prisma`.

---

## Technology Stack

| Component    | Technology        | Version |
| ------------ | ----------------- | ------- |
| Database     | PostgreSQL        | 17.x    |
| ORM          | Prisma            | 7.5+    |
| Runtime      | Node.js           | 18+     |
| Config       | prisma.config.ts  | —       |

---

## Database Schema

### Enums

| Enum Name          | Values                                    |
| ------------------ | ----------------------------------------- |
| `Role`             | `SUPER_ADMIN`, `OWNER`, `COACH`, `MEMBER` |
| `MemberStatus`     | `ACTIVE`, `EXPIRED`, `INACTIVE`           |
| `NotificationType` | `ANNOUNCEMENT`, `REMINDER`                |

### Tables

#### `User`
Central authentication and identity table.

| Column      | Type       | Constraints                     |
| ----------- | ---------- | ------------------------------- |
| `id`        | `String`   | Primary Key, CUID auto-gen      |
| `email`     | `String`   | Unique                          |
| `password`  | `String`   | Hashed (bcrypt)                 |
| `name`      | `String`   | Required                        |
| `phone`     | `String?`  | Optional                        |
| `role`      | `Role`     | Default: `MEMBER`               |
| `createdAt` | `DateTime` | Auto-set on creation            |
| `updatedAt` | `DateTime` | Auto-updated                    |

**Relations:** Owner of many `Gym`s, optional one `Member` profile.

---

#### `Gym`
Represents a gym branch/location.

| Column      | Type       | Constraints                |
| ----------- | ---------- | -------------------------- |
| `id`        | `String`   | Primary Key, CUID          |
| `name`      | `String`   | Required                   |
| `address`   | `String`   | Required                   |
| `phone`     | `String?`  | Optional                   |
| `ownerId`   | `String`   | FK → `User.id`            |
| `createdAt` | `DateTime` | Auto-set                   |
| `updatedAt` | `DateTime` | Auto-updated               |

**Relations:** Belongs to one `User` (owner). Has many `Member`s, `MembershipPlan`s, `Attendance` records, `Notification`s, and `Payment`s.

---

#### `Member`
Gym membership profile linked to a user.

| Column       | Type           | Constraints               |
| ------------ | -------------- | ------------------------- |
| `id`         | `String`       | Primary Key, CUID         |
| `userId`     | `String`       | FK → `User.id`, Unique   |
| `gymId`      | `String`       | FK → `Gym.id`            |
| `status`     | `MemberStatus` | Default: `ACTIVE`         |
| `bloodGroup` | `String?`      | Optional                  |
| `photoUrl`   | `String?`      | Optional                  |
| `joinDate`   | `DateTime`     | Default: now              |
| `expiryDate` | `DateTime?`    | Optional                  |
| `createdAt`  | `DateTime`     | Auto-set                  |
| `updatedAt`  | `DateTime`     | Auto-updated              |

**Relations:** Belongs to one `User` (1:1) and one `Gym`. Has many `Attendance` and `Payment` records.

---

#### `MembershipPlan`
Subscription plans defined per gym.

| Column           | Type       | Constraints         |
| ---------------- | ---------- | ------------------- |
| `id`             | `String`   | Primary Key, CUID   |
| `name`           | `String`   | Required            |
| `description`    | `String?`  | Optional            |
| `price`          | `Float`    | Required            |
| `durationMonths` | `Int`      | Required            |
| `gymId`          | `String`   | FK → `Gym.id`      |
| `createdAt`      | `DateTime` | Auto-set            |
| `updatedAt`      | `DateTime` | Auto-updated        |

**Relations:** Belongs to one `Gym`. Referenced by many `Payment`s.

---

#### `Attendance`
Check-in records for members.

| Column        | Type       | Constraints         |
| ------------- | ---------- | ------------------- |
| `id`          | `String`   | Primary Key, CUID   |
| `memberId`    | `String`   | FK → `Member.id`   |
| `gymId`       | `String`   | FK → `Gym.id`      |
| `date`        | `DateTime` | Default: now        |
| `checkInTime` | `DateTime` | Default: now        |

**Relations:** Belongs to one `Member` and one `Gym`.

---

#### `Payment`
Financial transaction records.

| Column      | Type       | Constraints                   |
| ----------- | ---------- | ----------------------------- |
| `id`        | `String`   | Primary Key, CUID             |
| `memberId`  | `String`   | FK → `Member.id`             |
| `gymId`     | `String`   | FK → `Gym.id`               |
| `amount`    | `Float`    | Required                      |
| `date`      | `DateTime` | Default: now                  |
| `method`    | `String`   | e.g. "CASH", "UPI", "CARD"   |
| `planId`    | `String`   | FK → `MembershipPlan.id`    |
| `createdAt` | `DateTime` | Auto-set                      |

**Relations:** Belongs to one `Member`, one `Gym`, and one `MembershipPlan`.

---

#### `Notification`
Announcements and reminders per gym.

| Column      | Type               | Constraints              |
| ----------- | ------------------ | ------------------------ |
| `id`        | `String`           | Primary Key, CUID        |
| `title`     | `String`           | Required                 |
| `message`   | `String`           | Required                 |
| `type`      | `NotificationType` | Default: `ANNOUNCEMENT`  |
| `gymId`     | `String`           | FK → `Gym.id`           |
| `createdAt` | `DateTime`         | Auto-set                 |

**Relations:** Belongs to one `Gym`.

---

## Entity Relationship Diagram

```
┌──────────────┐       ┌──────────────────┐       ┌──────────────────┐
│     User     │       │       Gym        │       │  MembershipPlan  │
├──────────────┤       ├──────────────────┤       ├──────────────────┤
│ id       (PK)│──┐    │ id          (PK) │──┐    │ id          (PK) │
│ email   (UQ) │  │    │ name             │  │    │ name             │
│ password     │  │    │ address          │  │    │ description      │
│ name         │  │    │ phone            │  │    │ price            │
│ phone        │  ├───▶│ ownerId     (FK) │  │    │ durationMonths   │
│ role         │  │    │ createdAt        │  ├───▶│ gymId       (FK) │
│ createdAt    │  │    │ updatedAt        │  │    │ createdAt        │
│ updatedAt    │  │    └──────────────────┘  │    │ updatedAt        │
└──────────────┘  │             │             │    └──────────────────┘
       │          │             │             │             │
       │ 1:1      │             │             │             │
       ▼          │             ▼             │             ▼
┌──────────────┐  │    ┌──────────────────┐  │    ┌──────────────────┐
│    Member    │  │    │   Attendance     │  │    │     Payment      │
├──────────────┤  │    ├──────────────────┤  │    ├──────────────────┤
│ id       (PK)│──┘    │ id          (PK) │  │    │ id          (PK) │
│ userId  (FK) │       │ memberId    (FK) │◀─┤    │ memberId    (FK) │
│ gymId   (FK) │──────▶│ gymId       (FK) │  ├───▶│ gymId       (FK) │
│ status       │       │ date             │  │    │ amount           │
│ bloodGroup   │       │ checkInTime      │  │    │ date             │
│ photoUrl     │       └──────────────────┘  │    │ method           │
│ joinDate     │                             │    │ planId      (FK) │
│ expiryDate   │    ┌──────────────────┐     │    │ createdAt        │
│ createdAt    │    │   Notification   │     │    └──────────────────┘
│ updatedAt    │    ├──────────────────┤     │
└──────────────┘    │ id          (PK) │     │
                    │ title            │     │
                    │ message          │     │
                    │ type             │     │
                    │ gymId       (FK) │◀────┘
                    │ createdAt        │
                    └──────────────────┘
```

**Key Relationships:**
- `User` 1 ──▶ N `Gym` (an owner can have multiple gym branches)
- `User` 1 ──▶ 1 `Member` (each user has at most one membership)
- `Gym` 1 ──▶ N `Member`, `MembershipPlan`, `Attendance`, `Payment`, `Notification`
- `Member` 1 ──▶ N `Attendance`, `Payment`
- `MembershipPlan` 1 ──▶ N `Payment`

---

## Local Development Setup

### Prerequisites
- **Node.js** v18+
- **PostgreSQL** 17 (install via Homebrew on macOS)

### Step 1: Install & Start PostgreSQL

```bash
# Install
brew install postgresql@17

# Initialize data directory (first time only)
initdb --locale=en_US.UTF-8 -E UTF-8 /usr/local/var/postgresql@17

# Start the service
brew services start postgresql@17

# Verify it's running
pg_isready
```

### Step 2: Create the Database

```bash
createdb gym_management
```

### Step 3: Configure Environment

Create or update `backend/.env`:
```dotenv
DATABASE_URL="postgresql://<your-username>@localhost:5432/gym_management"
JWT_SECRET="your-secret-key"
```

> Replace `<your-username>` with your macOS username (run `whoami` to check).

### Step 4: Run Migrations

```bash
cd backend
npm install
npx prisma migrate dev --name init
```

### Step 5: Generate Prisma Client

```bash
npx prisma generate
```

### Step 6: Verify

```bash
npx prisma studio
```
This opens a GUI at `http://localhost:5555` to browse your tables.

### Stopping PostgreSQL

```bash
brew services stop postgresql@17
```

---

## Production Deployment

### Recommended Providers

| Provider                              | Type              | Free Tier |
| ------------------------------------- | ----------------- | --------- |
| [Neon](https://neon.tech)             | Serverless PG     | Yes       |
| [Supabase](https://supabase.com)     | Managed PG        | Yes       |
| [Railway](https://railway.app)       | Managed PG        | Trial     |
| [AWS RDS](https://aws.amazon.com/rds) | Managed PG       | Trial     |

### Step 1: Provision a PostgreSQL Instance

1. Sign up on your chosen provider.
2. Create a new PostgreSQL database.
3. Copy the **connection string** (format: `postgresql://user:password@host:port/database`).

### Step 2: Set Environment Variables

On your hosting platform (Railway, Render, Vercel, etc.):

```
DATABASE_URL=postgresql://user:password@host:port/database?sslmode=require
JWT_SECRET=<strong-random-secret>
```

> Always use `?sslmode=require` for production connections.

### Step 3: Run Production Migration

```bash
# From the backend directory
DATABASE_URL="postgresql://..." npx prisma migrate deploy
```

> **Important:** Use `migrate deploy` (not `migrate dev`) in production. It applies pending migrations without creating new ones.

### Step 4: Generate Prisma Client for Production

```bash
npx prisma generate
```

This is typically handled in your build command:
```json
{
  "scripts": {
    "build": "npx prisma generate && nest build"
  }
}
```

---

## Migration Workflow

### Development (Local)

```bash
# Make changes to schema.prisma, then:
npx prisma migrate dev --name <description>

# Examples:
npx prisma migrate dev --name add_member_notes_field
npx prisma migrate dev --name create_invoice_table
```

This generates a new SQL migration file in `prisma/migrations/` and applies it locally.

### Production

```bash
# Deploy pending migrations (never creates new ones)
npx prisma migrate deploy
```

### Reset (Development Only!)

```bash
# ⚠️ DESTROYS ALL DATA — local dev only!
npx prisma migrate reset
```

### Viewing Migration Status

```bash
npx prisma migrate status
```

---

## Backup & Recovery

### Local Backup

```bash
# Backup
pg_dump gym_management > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore
psql gym_management < backup_20260316_120000.sql
```

### Production Backup

Most managed providers offer automatic backups. Verify:

| Provider | Backup Method                           |
| -------- | --------------------------------------- |
| Neon     | Point-in-time recovery (automatic)      |
| Supabase | Daily backups (automatic, Pro plan)     |
| Railway  | Manual via `pg_dump` or scheduled crons |
| AWS RDS  | Automated snapshots                     |

### Manual Production Backup

```bash
pg_dump "postgresql://user:password@host:port/database?sslmode=require" > prod_backup.sql
```

---

## Performance & Indexing

### Default Indexes (Auto-created by Prisma)

| Table            | Index                    | Type      |
| ---------------- | ------------------------ | --------- |
| `User`           | `User_email_key`         | Unique    |
| `Member`         | `Member_userId_key`      | Unique    |
| All tables       | Primary key (`id`)       | B-Tree    |

### Recommended Additional Indexes

For high-traffic production, add these to `schema.prisma`:

```prisma
model Member {
  // ... existing fields

  @@index([gymId])
  @@index([status])
}

model Attendance {
  // ... existing fields

  @@index([memberId])
  @@index([gymId])
  @@index([date])
}

model Payment {
  // ... existing fields

  @@index([memberId])
  @@index([gymId])
  @@index([date])
}

model Notification {
  // ... existing fields

  @@index([gymId])
  @@index([createdAt])
}
```

Then run `npx prisma migrate dev --name add_performance_indexes`.

---

## Environment Variables

### Backend (`backend/.env`)

| Variable       | Description                              | Example                                                     |
| -------------- | ---------------------------------------- | ----------------------------------------------------------- |
| `DATABASE_URL` | PostgreSQL connection string             | `postgresql://user:pass@localhost:5432/gym_management`       |
| `JWT_SECRET`   | Secret key for JWT token signing         | `a-long-random-string-here`                                 |

### Dashboard (`dashboard/.env`)

| Variable              | Description                        | Example                                                |
| --------------------- | ---------------------------------- | ------------------------------------------------------ |
| `DATABASE_URL`        | PostgreSQL connection string       | Same as backend                                        |
| `NEXT_PUBLIC_API_URL` | Backend API base URL               | `http://localhost:3000` or `https://api.yourgym.com`   |

### Prisma Config (`prisma.config.ts`)

Both `backend/` and `dashboard/` use this file to load `DATABASE_URL`:

```typescript
import "dotenv/config";
import { defineConfig } from "prisma/config";

export default defineConfig({
  schema: "prisma/schema.prisma",
  migrations: { path: "prisma/migrations" },
  datasource: { url: process.env["DATABASE_URL"] },
});
```

---

## Troubleshooting

### P1001: Can't reach database server

```
Error: P1001 — Can't reach database server at `localhost:XXXX`
```

**Cause:** PostgreSQL is not running, or the URL points to a wrong port/host.

**Fix:**
```bash
# Check if PostgreSQL is running
pg_isready

# Start it
brew services start postgresql@17

# Verify your .env has the correct URL
cat backend/.env
```

---

### P1003: Database does not exist

```
Error: P1003 — Database `gym_management` does not exist
```

**Fix:**
```bash
createdb gym_management
```

---

### Migration drift / Shadow database errors

**Fix:**
```bash
# Reset local dev database (⚠️ destroys data)
npx prisma migrate reset

# Then re-apply
npx prisma migrate dev
```

---

### Connection refused on production

- Ensure `?sslmode=require` is in the `DATABASE_URL`.
- Check your provider's IP allowlist — add your server's IP address.
- Verify the database credentials haven't been rotated.

---

### Prisma Client not updated after schema change

```bash
npx prisma generate
```

Always run this after modifying `schema.prisma` and before starting the app.
