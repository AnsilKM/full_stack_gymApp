# 🏁 Gym Full Stack: Handover & Setup Guide

This document contains everything you need to set up, run, and hand over this project to a client.

---

## 🔑 Initial Credentials

The system has been initialized with the following master accounts for management. Use these to log into the Web Dashboard or the Mobile App.

### **1. System Admin (The Master Owner)**
*   **Email:** `super@gym.com`
*   **Password:** `admin123`
*   **Access:** Acts as the overall owner. Has full access to all gyms, can create more branches, and manage everything.

### **2. Gym Branch Account (Location Login)**
*   **Email:** `powerhouse@gym.com`
*   **Password:** `gym123`
*   **Access:** This is a shared account used by whoever is working at the **PowerHouse Fitness Central** branch. They can manage members of **only** that gym.

---

## 👥 Simplified Role System
To keep management easy, we use only 2 roles:
1. **ADMIN**: Acts as the owner/manager for the entire system (Global Control).
2. **GYM**: A branch-level login assigned to a specific location (Location Control).

---

## ⚙️ Changing Credentials

If you want to change the initial **System Admin** email or password:

1.  Open the **`backend/.env`** file.
2.  Update these values:
    ```env
    SUPER_ADMIN_EMAIL=your-new-email@example.com
    SUPER_ADMIN_PASSWORD=your-new-password
    ```
3.  Run the seed command to update the database:
    ```bash
    cd backend
    npx prisma db seed
    ```

---

## 📱 Mobile App Focus

The mobile app is now a powerful management tool for **Admins** and **Gym Branches**.

### **Key Features:**
1.  **Dashboard Tracking:** Real-time stats on active members and today's attendance.
2.  **Member Management:** Add new members and assign them to membership plans.
3.  **Cross-Platform Navigation:** Smooth back-navigation (Android physical back button support) and premium dark-mode aesthetics with zero white-flicker transitions.
4.  **Profile Photos:** Support for member profile pictures or professional avatar placeholders.

---

## 🚀 Server-Side Setup (Backend)

The backend is built with NestJS and Prisma.

### **1. Clear Port Conflicts**
If you see an `EADDRINUSE` error on port 3000, run this to kill any "zombie" processes:
```bash
lsof -ti:3000 | xargs kill -9 2>/dev/null || true
```

### **2. Database Initialization (Seeding)**
To reset the database or prepare it for a new client with default plans and admins:
```bash
cd backend
npx prisma db seed
```

### **3. Run the Backend**
```bash
npm run start:dev
```
*   **Listen Address:** The server now listens on `0.0.0.0:3000` so it can be reached by physical mobile devices on the same Wi-Fi.

---

## 💻 Web Dashboard Setup

The dashboard is built with Next.js.

### **1. Run the Dashboard**
```bash
cd dashboard
npm run dev
```
*   **URL:** `http://localhost:3001`

---

## 📱 Mobile App Setup (Physical Devices)

When running the app on a **physical Android/iOS phone**, the phone needs to know your Mac's IP address.

### **1. Configuration**
Update the computer's local IP in the app code:
*   **File:** `GymApp/composeApp/src/androidMain/kotlin/com/gym/gymapp/Platform.android.kt`
*   **Current Setting:** `http://192.168.1.35:3000`

### **2. Connection Requirements**
1.  **Same Wi-Fi:** Phone and Mac must be on the same network.
2.  **Firewall:** Mac Firewall must allow incoming traffic on port 3000.

---

## 🛠️ Developer Tools

### **Prisma Studio (Visual Database)**
To view or edit the database directly in a browser:
```bash
cd backend
npx prisma studio
```
*   Opens at: `http://localhost:5555`

---

## 📝 Client Handover Checklist
1. Give the client the **System Admin** credentials.
2. Instruct them to use the **Mobile App** to register their first members at their branch.
3. Show them **Prisma Studio** if they want a spreadsheet-like view of their data.
