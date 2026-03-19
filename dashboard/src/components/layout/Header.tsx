"use client";

import { Search, Bell, UserCircle, LayoutDashboard } from "lucide-react";

export default function Header() {
  return (
    <header className="fixed top-0 right-0 left-0 lg:left-64 h-16 glass border-b z-40 px-4 lg:px-8 flex items-center justify-between transition-[left] duration-300">
      <div className="lg:hidden p-2 -ml-2 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors">
        <LayoutDashboard className="w-6 h-6 text-primary" />
      </div>
      <div className="flex-1 max-w-xl">
        <div className="relative group">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 group-focus-within:text-primary transition-colors" />
          <input 
            type="text" 
            placeholder="Search members, activities..." 
            className="w-full bg-slate-100 dark:bg-slate-800 border-none rounded-xl py-2 pl-10 pr-4 focus:ring-2 focus:ring-primary/20 transition-all outline-none"
          />
        </div>
      </div>

      <div className="flex items-center gap-4">
        <button className="relative p-2 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors">
          <Bell className="w-5 h-5 text-slate-500" />
          <span className="absolute top-2 right-2 w-2 h-2 bg-red-500 rounded-full border-2 border-white dark:border-slate-900"></span>
        </button>
        
        <div className="flex items-center gap-3 pl-4 border-l border-white/10">
          <div className="text-right">
            <p className="text-sm font-semibold">Alex Johnson</p>
            <p className="text-xs text-slate-500">Super Admin</p>
          </div>
          <button className="p-1 rounded-full border-2 border-primary/20">
            <UserCircle className="w-8 h-8 text-slate-400" />
          </button>
        </div>
      </div>
    </header>
  );
}
