"use client";

import { Users, Building2, CreditCard, Activity, RefreshCw } from "lucide-react";
import DashboardCard from "@/components/dashboard/DashboardCard";
import RevenueChart from "@/components/dashboard/RevenueChart";
import { motion } from "framer-motion";
import { useState, useEffect } from "react";
import { reportsApi, membersApi, gymsApi } from "@/lib/api";
import Loader from "@/components/ui/Loader";

export default function Dashboard() {
  const [stats, setStats] = useState<any>(null);
  const [recentMembers, setRecentMembers] = useState<any[]>([]);
  const [gyms, setGyms] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);

  const fetchData = async () => {
    if (gyms.length === 0) {
      setIsLoading(true);
    }
    setIsRefreshing(true);
    try {
      const gymsList = await gymsApi.getAll();
      setGyms(gymsList);
      
      if (gymsList.length > 0) {
        const gymId = gymsList[0].id; // Use first gym for demo
        const [dashboardStats, members] = await Promise.all([
          reportsApi.getStats(gymId),
          membersApi.getAll(gymId)
        ]);
        setStats(dashboardStats);
        setRecentMembers(members.slice(0, 5));
      } else {
        // If no gyms, we can't fetch stats
        setStats({
          totalMembers: 0,
          activeMembers: 0,
          totalRevenue: 0,
          todayAttendance: 0
        });
      }
    } catch (error) {
      console.error("Error fetching dashboard data:", error);
    } finally {
      setIsLoading(false);
      setIsRefreshing(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <div className="space-y-8">
      <div className="flex items-start justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight mb-2">Dashboard Overview</h1>
          <p className="text-slate-500 dark:text-slate-400">Welcome back! Here's what's happening at {gyms[0]?.name || 'your gym'} today.</p>
        </div>
        <button 
          onClick={fetchData} 
          disabled={isRefreshing}
          className="p-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl hover:bg-slate-50 dark:hover:bg-slate-800/80 transition-all shadow-sm active:scale-95 disabled:opacity-50"
          title="Refresh Dashboard"
        >
          <RefreshCw className={`w-5 h-5 text-primary ${isRefreshing ? 'animate-spin' : ''}`} />
        </button>
      </div>

      {isLoading ? (
        <Loader isFullPage size="lg" />
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <DashboardCard 
              title="Total Members" 
              value={stats?.totalMembers.toString() || "0"} 
              trend="+" 
              trendUp={true}
              icon={Users} 
              delay={0.1}
            />
            <DashboardCard 
              title="Active Memberships" 
              value={stats?.activeMembers.toString() || "0"} 
              trend="+" 
              trendUp={true}
              icon={Activity} 
              delay={0.2}
            />
            <DashboardCard 
              title="Total Revenue" 
              value={`₹${stats?.totalRevenue.toLocaleString() || "0"}`} 
              trend="+" 
              trendUp={true}
              icon={CreditCard} 
              delay={0.3}
            />
            <DashboardCard 
              title="Gym Branches" 
              value={gyms.length.toString()} 
              trend="+" 
              trendUp={true}
              icon={Building2} 
              delay={0.4}
            />
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div className="card h-[400px] flex flex-col">
              <h3 className="text-lg font-bold mb-6">Revenue Growth</h3>
              <div className="flex-1">
                <RevenueChart />
              </div>
            </div>
            <div className="card h-[400px] flex flex-col">
              <h3 className="text-lg font-bold mb-6">Recent Members</h3>
              <div className="space-y-4">
                {recentMembers.length === 0 ? (
                  <p className="text-slate-500 text-center py-8">No recent activity.</p>
                ) : recentMembers.map((member, i) => (
                  <div key={member.id} className="flex items-center justify-between p-3 rounded-xl hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-primary/20 flex items-center justify-center font-bold text-primary">
                        {member.user.name.charAt(0)}
                      </div>
                      <div>
                        <p className="text-sm font-semibold text-slate-900 dark:text-white">{member.user.name}</p>
                        <p className="text-xs text-slate-500">{new Date(member.createdAt).toLocaleDateString()}</p>
                      </div>
                    </div>
                    <span className={`text-xs font-medium px-2 py-1 rounded-full ${
                      member.status === 'ACTIVE' ? 'text-emerald-500 bg-emerald-500/10' : 'text-slate-500 bg-slate-500/10'
                    }`}>
                      {member.status}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
