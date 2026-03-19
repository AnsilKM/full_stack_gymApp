"use client";

import { CheckCircle2, Calendar, User } from "lucide-react";
import { motion } from "framer-motion";
import { useState, useEffect } from "react";
import { attendanceApi, gymsApi } from "@/lib/api";
import { useNotification } from "@/lib/contexts/NotificationContext";

export default function AttendancePage() {
  const { showNotification } = useNotification();
  const [attendance, setAttendance] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [stats, setStats] = useState({ present: 0 });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const gyms = await gymsApi.getAll();
        if (gyms.length > 0) {
          const logs = await attendanceApi.getToday(gyms[0].id);
          setAttendance(logs);
          setStats({ present: logs.length });
        }
      } catch (err: any) {
        showNotification(err.message || "Failed to fetch attendance logs", "error");
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, [showNotification]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="w-12 h-12 border-4 border-primary border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight mb-2">Daily Attendance</h1>
          <p className="text-slate-500 dark:text-slate-400">Track real-time member check-ins across all branches.</p>
        </div>
        <div className="flex items-center gap-3">
          <div className="flex -space-x-2">
            {[1, 2, 3, 4].map((i) => (
              <div key={i} className="w-8 h-8 rounded-full border-2 border-white dark:border-slate-900 bg-slate-200 dark:bg-slate-800" />
            ))}
          </div>
          <span className="text-xs font-medium text-slate-500">124 checked in today</span>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="card text-center py-8">
          <h3 className="text-slate-500 text-sm font-medium mb-2">Expected Today</h3>
          <p className="text-3xl font-bold">N/A</p>
        </div>
        <div className="card text-center py-8 border-primary/20 bg-primary/5">
          <h3 className="text-slate-500 text-sm font-medium mb-2">Present</h3>
          <p className="text-3xl font-bold text-primary">{stats.present}</p>
        </div>
        <div className="card text-center py-8">
          <h3 className="text-slate-500 text-sm font-medium mb-2">Absent</h3>
          <p className="text-3xl font-bold text-slate-400">N/A</p>
        </div>
      </div>

      <div className="card p-0 overflow-hidden">
        <div className="px-6 py-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
          <h3 className="font-bold">Recent Check-ins</h3>
          <Calendar className="w-4 h-4 text-slate-400" />
        </div>
        <div className="divide-y divide-slate-200 dark:divide-slate-800">
          {attendance.length === 0 ? (
            <p className="px-6 py-12 text-center text-slate-500">No check-ins recorded today.</p>
          ) : attendance.map((log, index) => (
            <motion.div 
              key={log.id}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.05 }}
              className="px-6 py-4 flex items-center justify-between hover:bg-slate-50 dark:hover:bg-slate-800/30 transition-colors"
            >
              <div className="flex items-center gap-4">
                <div className="w-10 h-10 rounded-full bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
                  <User className="w-5 h-5 text-slate-400" />
                </div>
                <div>
                  <p className="font-semibold">{log.member.user.name}</p>
                  <p className="text-xs text-slate-500">{log.gym.name}</p>
                </div>
              </div>
              <div className="flex items-center gap-8">
                <div className="text-right">
                  <p className="text-sm font-medium">{new Date(log.checkInTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</p>
                  <p className="text-xs text-slate-400">Check-in time</p>
                </div>
                <div className="flex items-center gap-1 px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/10 text-emerald-500">
                  <CheckCircle2 className="w-3 h-3" />
                  Present
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </div>
  );
}

import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";
import { Activity } from "lucide-react";

function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}
