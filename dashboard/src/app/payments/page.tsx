"use client";

import { CreditCard, ArrowUpRight, ArrowDownRight, Search, Download, Filter } from "lucide-react";
import { motion } from "framer-motion";
import { useState, useEffect } from "react";
import { paymentsApi, reportsApi, gymsApi } from "@/lib/api";
import { useNotification } from "@/lib/contexts/NotificationContext";

export default function PaymentsPage() {
  const { showNotification } = useNotification();
  const [payments, setPayments] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [stats, setStats] = useState({ totalRevenue: 0 });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const gyms = await gymsApi.getAll();
        if (gyms.length > 0) {
          const gymId = gyms[0].id;
          const [paymentLogs, dashboardStats] = await Promise.all([
            paymentsApi.getAll(gymId),
            reportsApi.getStats(gymId)
          ]);
          setPayments(paymentLogs);
            setStats({ totalRevenue: dashboardStats.totalRevenue });
          }
        } catch (err: any) {
          showNotification(err.message || "Failed to fetch financial data", "error");
        } finally {
          setIsLoading(false);
        }
    };
    fetchData();
  }, []);

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
          <h1 className="text-3xl font-bold tracking-tight mb-2">Revenue & Billing</h1>
          <p className="text-slate-500 dark:text-slate-400">Track memberships, payments, and financial health.</p>
        </div>
        <button className="btn-primary flex items-center gap-2">
          <Download className="w-4 h-4" />
          Export Reports
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="card">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-500 text-sm font-medium">Total Revenue</h3>
            <ArrowUpRight className="w-4 h-4 text-emerald-500" />
          </div>
          <p className="text-3xl font-bold">₹{stats.totalRevenue.toLocaleString()}.00</p>
          <p className="text-xs text-emerald-500 mt-2 font-medium">+--% <span className="text-slate-400 font-normal">from last month</span></p>
        </div>
        <div className="card">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-500 text-sm font-medium">Pending Payments</h3>
            <Activity className="w-4 h-4 text-amber-500" />
          </div>
          <p className="text-3xl font-bold">₹0.00</p>
          <p className="text-xs text-slate-400 mt-2 font-normal">No pending payments</p>
        </div>
        <div className="card">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-500 text-sm font-medium">Failed Transactions</h3>
            <ArrowDownRight className="w-4 h-4 text-red-500" />
          </div>
          <p className="text-3xl font-bold">0</p>
          <p className="text-xs text-red-500 mt-2 font-medium">0% <span className="text-slate-400 font-normal">from last month</span></p>
        </div>
      </div>

      <div className="card p-0 overflow-hidden">
        <div className="px-6 py-4 border-b border-slate-200 dark:border-slate-800 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <h3 className="font-bold">Transaction History</h3>
          <div className="flex items-center gap-2">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-3 h-3 text-slate-400" />
              <input type="text" placeholder="Search..." className="bg-slate-50 dark:bg-slate-800 rounded-lg py-1.5 pl-8 pr-3 text-xs border border-slate-200 dark:border-slate-800 outline-none" />
            </div>
          </div>
        </div>
        <table className="w-full text-left">
          <thead className="bg-slate-50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-800">
            <tr>
              <th className="px-6 py-3 text-xs font-bold text-slate-500 uppercase tracking-wider">Member</th>
              <th className="px-6 py-3 text-xs font-bold text-slate-500 uppercase tracking-wider">Plan</th>
              <th className="px-6 py-3 text-xs font-bold text-slate-500 uppercase tracking-wider">Amount</th>
              <th className="px-6 py-3 text-xs font-bold text-slate-500 uppercase tracking-wider">Date</th>
              <th className="px-6 py-3 text-xs font-bold text-slate-500 uppercase tracking-wider">Status</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-200 dark:divide-slate-800">
            {payments.length === 0 ? (
              <tr>
                <td colSpan={5} className="px-6 py-12 text-center text-slate-500 text-sm">No transaction history found.</td>
              </tr>
            ) : payments.map((p, index) => (
              <motion.tr 
                key={p.id}
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: index * 0.05 }}
                className="hover:bg-slate-50 dark:hover:bg-slate-800/30 transition-colors"
              >
                <td className="px-6 py-4 font-semibold text-sm">{p.member.user.name}</td>
                <td className="px-6 py-4 text-xs text-slate-500">{p.plan.name}</td>
                <td className="px-6 py-4 font-bold text-sm">₹{p.amount}</td>
                <td className="px-6 py-4 text-xs text-slate-400">{new Date(p.date).toLocaleDateString()}</td>
                <td className="px-6 py-4">
                  <span className="px-2 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider bg-emerald-500/10 text-emerald-500">
                    Completed
                  </span>
                </td>
              </motion.tr>
            ))}
          </tbody>
        </table>
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
