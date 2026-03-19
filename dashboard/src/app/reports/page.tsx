"use client";

import { motion } from "framer-motion";
import { BarChart3, TrendingUp, TrendingDown, Calendar, Download } from "lucide-react";
import RevenueChart from "@/components/dashboard/RevenueChart";

export default function ReportsPage() {
  return (
    <div className="space-y-8">
      <div className="flex items-end justify-between">
        <div className="flex items-center gap-4">
          <div className="w-12 h-12 bg-emerald-500 rounded-2xl flex items-center justify-center animate-float shadow-lg shadow-emerald-500/20">
            <BarChart3 className="w-6 h-6 text-white" />
          </div>
          <div>
            <h1 className="text-3xl font-bold tracking-tight mb-1">Reports & Analytics</h1>
            <p className="text-slate-500 dark:text-slate-400 font-medium">Deep dive into your gym's performance and growth metrics.</p>
          </div>
        </div>
        <button className="flex items-center gap-2 px-6 py-3 bg-slate-900 dark:bg-white text-white dark:text-slate-900 rounded-2xl font-bold hover:opacity-90 transition-all hover:scale-105 active:scale-95 shadow-xl">
          <Download className="w-4 h-4" />
          Export Data
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 card h-[500px] flex flex-col group">
          <div className="flex items-center justify-between mb-8">
            <h3 className="text-xl font-bold">Revenue Insights</h3>
            <div className="flex bg-slate-100 dark:bg-slate-800 p-1 rounded-xl">
              <button className="px-4 py-1.5 text-xs font-semibold bg-white dark:bg-slate-700 shadow-md rounded-lg">6 Months</button>
              <button className="px-4 py-1.5 text-xs font-semibold text-slate-500 rounded-lg hover:text-slate-700 transition-colors">1 Year</button>
              <button className="px-4 py-1.5 text-xs font-semibold text-slate-500 rounded-lg hover:text-slate-700 transition-colors">All Time</button>
            </div>
          </div>
          <div className="flex-1 w-full">
            <RevenueChart />
          </div>
        </div>

        <div className="space-y-6">
          <div className="card">
            <h3 className="text-xs font-bold text-slate-400 mb-6 uppercase tracking-widest">Growth Metrics</h3>
            <div className="space-y-6">
              {[
                { label: "Retention Rate", value: "94.2%", icon: TrendingUp, color: "text-emerald-500", bg: "bg-emerald-500/10" },
                { label: "New Signups", value: "+42", icon: TrendingUp, color: "text-emerald-500", bg: "bg-emerald-500/10" },
                { label: "Avg Revenue/User", value: "$54.20", icon: Calendar, color: "text-blue-500", bg: "bg-blue-500/10" },
                { label: "Plan Churn", value: "2.1%", icon: TrendingDown, color: "text-red-500", bg: "bg-red-500/10" },
              ].map((kpi, idx) => (
                <div key={idx} className="flex items-center justify-between group/item cursor-pointer">
                  <div className="flex items-center gap-4">
                    <div className={`w-10 h-10 rounded-2xl ${kpi.bg} flex items-center justify-center transition-transform group-hover/item:scale-110`}>
                      <kpi.icon className={`w-5 h-5 ${kpi.color}`} />
                    </div>
                    <span className="text-sm font-semibold text-slate-600 dark:text-slate-300">{kpi.label}</span>
                  </div>
                  <span className="text-lg font-bold">{kpi.value}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="card bg-emerald-500 text-white border-none shadow-emerald-500/20">
            <h3 className="text-xl font-bold mb-2">Performance Spike</h3>
            <p className="text-emerald-100 text-sm mb-6 leading-relaxed">
              Your "Gold Tier" memberships increased by 22% this quarter. Consider launching a referral program to maintain momentum.
            </p>
            <button className="w-full py-3 bg-white/20 hover:bg-white/30 rounded-2xl text-sm font-bold transition-all backdrop-blur-md">
              Generate AI Strategy
            </button>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {[
          { label: "Total Members", value: "1,284", sub: "+12% from last month" },
          { label: "Active Plans", value: "842", sub: "72% of total" },
          { label: "Monthly Revenue", value: "$64,200", sub: "+5.4% increase" },
          { label: "Pending Renewals", value: "12", sub: "Urgent attention" },
        ].map((stat, idx) => (
          <div key={idx} className="card group cursor-default">
            <p className="text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">{stat.label}</p>
            <h4 className="text-2xl font-black mb-1 group-hover:text-primary transition-colors">{stat.value}</h4>
            <p className="text-xs text-slate-500 font-medium">{stat.sub}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
