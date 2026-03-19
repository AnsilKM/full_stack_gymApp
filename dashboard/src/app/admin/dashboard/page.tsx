"use client";

import { useState, useEffect } from "react";
import { adminApi } from "@/lib/api";
import { Building2, UserPlus, Shield, Activity, Plus, List } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import { useNotification } from "@/lib/contexts/NotificationContext";

export default function AdminDashboard() {
  const { showNotification } = useNotification();
  const [gyms, setGyms] = useState<any[]>([]);
  const [owners, setOwners] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showAddGym, setShowAddGym] = useState(false);
  const [showAddOwner, setShowAddOwner] = useState(false);

  // Form states
  const [gymData, setGymData] = useState({ name: "", address: "", phone: "", ownerId: "" });
  const [ownerData, setOwnerData] = useState({ name: "", email: "", password: "", role: "OWNER" });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setIsLoading(true);
    try {
      const [gymsList, ownersList] = await Promise.all([
        adminApi.getGyms(),
        adminApi.getOwners()
      ]);
      setGyms(gymsList);
      setOwners(ownersList);
    } catch (err: any) {
      showNotification(err.message || "Failed to fetch admin data", "error");
    } finally {
      setIsLoading(false);
    }
  };

  const handleAddGym = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await adminApi.createGym(gymData);
      showNotification("Gym registered successfully", "success");
      setShowAddGym(false);
      setGymData({ name: "", address: "", phone: "", ownerId: "" });
      fetchData();
    } catch (err: any) {
      showNotification(err.message || "Error adding gym", "error");
    }
  };

  const handleAddOwner = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await adminApi.createUser(ownerData);
      showNotification(`${ownerData.name} invited as ${ownerData.role}`, "success");
      setShowAddOwner(false);
      setOwnerData({ name: "", email: "", password: "", role: "OWNER" });
      fetchData();
    } catch (err: any) {
      showNotification(err.message || "Error adding owner", "error");
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="w-12 h-12 border-4 border-primary border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div className="p-4 sm:p-8 max-w-7xl mx-auto space-y-8">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-end gap-6">
        <div>
          <h1 className="text-2xl sm:text-4xl font-bold tracking-tight text-slate-900 dark:text-white">System Administration</h1>
          <p className="text-slate-500 mt-2">Manage gym nodes and system administrators</p>
        </div>
        <div className="flex flex-col sm:flex-row gap-4 w-full sm:w-auto">
          <button 
            id="add-owner-btn"
            onClick={() => setShowAddOwner(true)}
            className="flex items-center justify-center gap-2 px-6 py-3 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-2xl font-semibold hover:bg-slate-50 transition-all w-full sm:w-auto"
          >
            <UserPlus className="w-5 h-5" /> Add Owner
          </button>
          <button 
            id="add-gym-btn"
            onClick={() => setShowAddGym(true)}
            className="flex items-center justify-center gap-2 px-6 py-3 bg-primary text-white rounded-2xl font-semibold shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-[0.98] transition-all w-full sm:w-auto"
          >
            <Plus className="w-5 h-5" /> New Gym
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white dark:bg-slate-800 p-6 rounded-3xl border border-slate-200 dark:border-slate-700 shadow-sm">
          <div className="flex items-center gap-4 mb-4">
            <div className="p-3 bg-indigo-500/10 rounded-2xl text-indigo-500">
              <Building2 className="w-6 h-6" />
            </div>
            <div>
              <p className="text-sm font-medium text-slate-500">Total Gyms</p>
              <h3 className="text-2xl font-bold">{gyms.length}</h3>
            </div>
          </div>
        </div>
        <div className="bg-white dark:bg-slate-800 p-6 rounded-3xl border border-slate-200 dark:border-slate-700 shadow-sm">
          <div className="flex items-center gap-4 mb-4">
            <div className="p-3 bg-emerald-500/10 rounded-2xl text-emerald-500">
              <Shield className="w-6 h-6" />
            </div>
            <div>
              <p className="text-sm font-medium text-slate-500">Gym Owners</p>
              <h3 className="text-2xl font-bold">{owners.length}</h3>
            </div>
          </div>
        </div>
        <div className="bg-white dark:bg-slate-800 p-6 rounded-3xl border border-slate-200 dark:border-slate-700 shadow-sm">
          <div className="flex items-center gap-4 mb-4">
            <div className="p-3 bg-rose-500/10 rounded-2xl text-rose-500">
              <Activity className="w-6 h-6" />
            </div>
            <div>
              <p className="text-sm font-medium text-slate-500">System Status</p>
              <h3 className="text-2xl font-bold text-emerald-500">Healthy</h3>
            </div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Gyms List */}
        <div className="bg-white dark:bg-slate-800 rounded-3xl border border-slate-200 dark:border-slate-700 overflow-hidden shadow-sm">
          <div className="p-6 border-b border-slate-200 dark:border-slate-700 flex justify-between items-center">
            <h2 className="text-xl font-bold flex items-center gap-2">
              <List className="w-5 h-5 text-primary" /> Registered Gyms
            </h2>
          </div>
          <div className="divide-y divide-slate-100 dark:divide-slate-700">
            {gyms.map((gym) => (
              <div key={gym.id} className="p-6 hover:bg-slate-50 dark:hover:bg-slate-900/50 transition-colors">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="font-bold text-lg">{gym.name}</h3>
                    <p className="text-sm text-slate-500">{gym.address}</p>
                    <div className="mt-2 flex items-center gap-3">
                      <span className="text-xs bg-slate-100 dark:bg-slate-700 px-2 py-1 rounded-full text-slate-600 dark:text-slate-400">
                        Owner: {gym.owner.name}
                      </span>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-xs text-slate-400 uppercase font-semibold">Joined</p>
                    <p className="text-sm font-medium">{new Date(gym.createdAt).toLocaleDateString()}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Owners List */}
        <div className="bg-white dark:bg-slate-800 rounded-3xl border border-slate-200 dark:border-slate-700 overflow-hidden shadow-sm">
          <div className="p-6 border-b border-slate-200 dark:border-slate-700 flex justify-between items-center">
            <h2 className="text-xl font-bold flex items-center gap-2">
              <Shield className="w-5 h-5 text-primary" /> Active Owners
            </h2>
          </div>
          <div className="divide-y divide-slate-100 dark:divide-slate-700">
            {owners.map((owner) => (
              <div key={owner.id} className="p-6 hover:bg-slate-50 dark:hover:bg-slate-900/50 transition-colors">
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 rounded-2xl bg-primary/10 flex items-center justify-center font-bold text-primary text-xl">
                    {owner.name.charAt(0)}
                  </div>
                  <div className="flex-1">
                    <h3 className="font-bold">{owner.name}</h3>
                    <p className="text-sm text-slate-500">{owner.email}</p>
                  </div>
                  <span className="text-xs font-bold text-primary bg-primary/10 px-3 py-1 rounded-full uppercase tracking-wider">
                    Owner
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Modals */}
      <AnimatePresence>
        {showAddGym && (
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
            <motion.div 
              initial={{ scale: 0.95, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.95, opacity: 0 }}
              className="bg-white dark:bg-slate-800 w-full max-w-md rounded-3xl p-8 shadow-2xl"
            >
              <h2 className="text-2xl font-bold mb-6">Register New Gym</h2>
              <form onSubmit={handleAddGym} className="space-y-4">
                <div>
                  <label className="text-sm font-semibold mb-1 block">Gym Name</label>
                  <input 
                    required
                    className="w-full p-4 bg-slate-50 dark:bg-slate-900 rounded-2xl border-none outline-none focus:ring-2 focus:ring-primary"
                    placeholder="E.g. Titan Gym"
                    value={gymData.name}
                    onChange={(e) => setGymData({...gymData, name: e.target.value})}
                  />
                </div>
                <div>
                  <label className="text-sm font-semibold mb-1 block">Address</label>
                  <input 
                    required
                    className="w-full p-4 bg-slate-50 dark:bg-slate-900 rounded-2xl border-none outline-none focus:ring-2 focus:ring-primary"
                    placeholder="123 Street, City"
                    value={gymData.address}
                    onChange={(e) => setGymData({...gymData, address: e.target.value})}
                  />
                </div>
                <div>
                  <label className="text-sm font-semibold mb-1 block">Assign Owner</label>
                  <select 
                    required
                    className="w-full p-4 bg-slate-50 dark:bg-slate-900 rounded-2xl border-none outline-none focus:ring-2 focus:ring-primary appearance-none"
                    value={gymData.ownerId}
                    onChange={(e) => setGymData({...gymData, ownerId: e.target.value})}
                  >
                    <option value="">Select an owner</option>
                    {owners.map(o => <option key={o.id} value={o.id}>{o.name}</option>)}
                  </select>
                </div>
                <div className="flex gap-4 pt-4">
                  <button type="button" onClick={() => setShowAddGym(false)} className="flex-1 py-4 font-bold text-slate-500">Cancel</button>
                  <button type="submit" className="flex-1 py-4 bg-primary text-white rounded-2xl font-bold">Create Gym</button>
                </div>
              </form>
            </motion.div>
          </div>
        )}

        {showAddOwner && (
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
            <motion.div 
              initial={{ scale: 0.95, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.95, opacity: 0 }}
              className="bg-white dark:bg-slate-800 w-full max-w-md rounded-3xl p-8 shadow-2xl"
            >
              <h2 className="text-2xl font-bold mb-6">Create New Owner/Admin</h2>
              <form onSubmit={handleAddOwner} className="space-y-4">
                <div>
                  <label className="text-sm font-semibold mb-1 block">Full Name</label>
                  <input 
                    required
                    className="w-full p-4 bg-slate-50 dark:bg-slate-900 rounded-2xl border-none outline-none focus:ring-2 focus:ring-primary"
                    placeholder="John Doe"
                    value={ownerData.name}
                    onChange={(e) => setOwnerData({...ownerData, name: e.target.value})}
                  />
                </div>
                <div>
                  <label className="text-sm font-semibold mb-1 block">Email</label>
                  <input 
                    required
                    type="email"
                    className="w-full p-4 bg-slate-50 dark:bg-slate-900 rounded-2xl border-none outline-none focus:ring-2 focus:ring-primary"
                    placeholder="owner@example.com"
                    value={ownerData.email}
                    onChange={(e) => setOwnerData({...ownerData, email: e.target.value})}
                  />
                </div>
                <div>
                  <label className="text-sm font-semibold mb-1 block">Temporary Password</label>
                  <input 
                    required
                    type="password"
                    className="w-full p-4 bg-slate-50 dark:bg-slate-900 rounded-2xl border-none outline-none focus:ring-2 focus:ring-primary"
                    value={ownerData.password}
                    onChange={(e) => setOwnerData({...ownerData, password: e.target.value})}
                  />
                </div>
                <div>
                  <label className="text-sm font-semibold mb-1 block">Role</label>
                  <select 
                    className="w-full p-4 bg-slate-50 dark:bg-slate-900 rounded-2xl border-none outline-none focus:ring-2 focus:ring-primary appearance-none"
                    value={ownerData.role}
                    onChange={(e) => setOwnerData({...ownerData, role: e.target.value})}
                  >
                    <option value="OWNER">Gym Owner</option>
                    <option value="SUPER_ADMIN">System Admin</option>
                  </select>
                </div>
                <div className="flex gap-4 pt-4">
                  <button type="button" onClick={() => setShowAddOwner(false)} className="flex-1 py-4 font-bold text-slate-500">Cancel</button>
                  <button type="submit" className="flex-1 py-4 bg-primary text-white rounded-2xl font-bold">Create User</button>
                </div>
              </form>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
}
