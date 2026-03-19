"use client";

import { Settings, User, Bell, Shield, Palette, Database } from "lucide-react";

export default function SettingsPage() {
  const sections = [
    { icon: User, label: "Account Settings", description: "Manage your personal information and login credentials." },
    { icon: Bell, label: "Notifications", description: "Configure how and when you receive alerts." },
    { icon: Shield, label: "Privacy & Security", description: "Enable two-factor authentication and manage active sessions." },
    { icon: Palette, label: "Appearance", description: "Customize the dashboard theme and styling preferences." },
    { icon: Database, label: "Database Management", description: "Configure Prisma connection and run migrations." },
  ];

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      <div>
        <h1 className="text-3xl font-bold tracking-tight mb-2">Settings</h1>
        <p className="text-slate-500">Configure your dashboard and management preferences.</p>
      </div>

      <div className="grid gap-4">
        {sections.map((section) => (
          <div key={section.label} className="card flex items-start gap-4 cursor-pointer hover:border-primary/30 group transition-colors">
            <div className="w-10 h-10 rounded-xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center group-hover:bg-primary/10 transition-colors">
              <section.icon className="w-5 h-5 text-slate-500 group-hover:text-primary transition-colors" />
            </div>
            <div className="flex-1">
              <h3 className="font-bold text-slate-900 dark:text-white">{section.label}</h3>
              <p className="text-sm text-slate-500">{section.description}</p>
            </div>
          </div>
        ))}
      </div>

      <div className="pt-8 border-t border-slate-200 dark:border-slate-800 flex justify-end gap-4">
        <button className="px-6 py-2 rounded-xl text-slate-500 font-medium hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors">
          Cancel
        </button>
        <button className="px-6 py-2 bg-primary text-white rounded-xl font-bold shadow-lg shadow-primary/20 hover:opacity-90 active:scale-95 transition-all">
          Save Changes
        </button>
      </div>
    </div>
  );
}
