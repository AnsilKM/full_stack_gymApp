"use client";

import { Bell, Megaphone, Send, History, Trash2, MoreHorizontal } from "lucide-react";
import { motion } from "framer-motion";

const notifications = [
  { id: 1, title: "Easter Weekend Schedule", message: "Gym will be closed on Sunday, April 9th. Regular hours on Monday.", type: "Announcement", date: "2024-03-12" },
  { id: 2, title: "New Yoga Classes", message: "Exciting news! We're adding 3 new Yoga slots starting next week.", type: "Activity", date: "2024-03-10" },
  { id: 3, title: "Maintenance Alert", message: "Shower facilities in the Downtown Elite branch will be closed for maintenance tomorrow.", type: "Alert", date: "2024-03-08" },
];

export default function NotificationsPage() {
  return (
    <div className="space-y-8 max-w-5xl mx-auto">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight mb-2">Notifications & Alerts</h1>
          <p className="text-slate-500 dark:text-slate-400">Send announcements and manage system notifications.</p>
        </div>
        <button className="btn-primary flex items-center gap-2">
          <Megaphone className="w-4 h-4" />
          New Announcement
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-1 space-y-6">
          <div className="card">
            <h3 className="font-bold mb-4 flex items-center gap-2">
              <Send className="w-4 h-4 text-primary" /> Quick Send
            </h3>
            <div className="space-y-4">
              <div>
                <label className="text-xs font-bold text-slate-500 uppercase tracking-wider block mb-2">Subject</label>
                <input type="text" className="w-full bg-slate-50 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-800 rounded-xl py-2 px-3 outline-none focus:ring-2 focus:ring-primary/20" placeholder="e.g. Schedule Change" />
              </div>
              <div>
                <label className="text-xs font-bold text-slate-500 uppercase tracking-wider block mb-2">Message</label>
                <textarea className="w-full bg-slate-50 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-800 rounded-xl py-2 px-3 outline-none focus:ring-2 focus:ring-primary/20 min-h-[120px]" placeholder="Type your message here..." />
              </div>
              <button className="w-full btn-primary py-3">Send to All Branches</button>
            </div>
          </div>
        </div>

        <div className="lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between mb-2">
            <h3 className="font-bold flex items-center gap-2">
              <History className="w-4 h-4 text-slate-400" /> Recent History
            </h3>
            <button className="text-xs font-bold text-primary hover:underline">Clear all</button>
          </div>
          
          {notifications.map((n, index) => (
            <motion.div 
              key={n.id}
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: index * 0.1 }}
              className="card relative group hover:border-primary/30 transition-all"
            >
              <div className="flex items-start gap-4">
                <div className={cn(
                  "w-10 h-10 rounded-xl flex items-center justify-center shrink-0",
                  n.type === 'Announcement' ? 'bg-primary/10 text-primary' : 
                  n.type === 'Alert' ? 'bg-red-500/10 text-red-500' : 'bg-slate-100/10 text-slate-400'
                )}>
                  {n.type === 'Announcement' ? <Megaphone className="w-5 h-5" /> : <Bell className="w-5 h-5" />}
                </div>
                <div className="flex-1">
                  <div className="flex items-center justify-between mb-1">
                    <h4 className="font-bold">{n.title}</h4>
                    <span className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">{n.date}</span>
                  </div>
                  <p className="text-sm text-slate-500 leading-relaxed">{n.message}</p>
                </div>
                <div className="flex flex-col gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button className="p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg text-slate-400 hover:text-red-500 transition-colors">
                    <Trash2 className="w-4 h-4" />
                  </button>
                  <button className="p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg text-slate-400">
                    <MoreHorizontal className="w-4 h-4" />
                  </button>
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

function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}
