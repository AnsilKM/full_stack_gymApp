"use client";

import { Plus, MapPin, Phone, Users, User, ArrowRight } from "lucide-react";
import { motion } from "framer-motion";

import { useState, useEffect } from "react";
import { gymsApi } from "@/lib/api";

export default function BranchesPage() {
  const [branches, setBranches] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    gymsApi.getAll().then(data => {
      setBranches(data);
      setIsLoading(false);
    }).catch(err => {
      console.error(err);
      setIsLoading(false);
    });
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
          <h1 className="text-3xl font-bold tracking-tight mb-2">Gym Branches</h1>
          <p className="text-slate-500 dark:text-slate-400">Manage your gym network and head coaches.</p>
        </div>
        <button className="btn-primary flex items-center gap-2">
          <Plus className="w-4 h-4" />
          Add New Branch
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {branches.map((branch, index) => (
          <motion.div 
            key={branch.id}
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: index * 0.1 }}
            className="card flex flex-col h-full"
          >
            <div className="flex items-center gap-3 mb-6">
              <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-primary to-primary-light flex items-center justify-center text-white font-bold text-xl">
                {branch.name.charAt(0)}
              </div>
              <div>
                <h3 className="font-bold text-lg">{branch.name}</h3>
                <div className="flex items-center gap-1 text-xs text-slate-500">
                  <MapPin className="w-3 h-3" /> {branch.address}
                </div>
              </div>
            </div>

            <div className="space-y-3 mb-8 flex-1">
              <div className="flex items-center justify-between p-3 rounded-xl bg-slate-50 dark:bg-slate-800/50">
                <div className="flex items-center gap-2 text-sm text-slate-500">
                  <User className="w-4 h-4 text-primary" /> Manager
                </div>
                <span className="text-sm font-semibold">Alex Johnson</span>
              </div>
              <div className="flex items-center justify-between p-3 rounded-xl bg-slate-50 dark:bg-slate-800/50">
                <div className="flex items-center gap-2 text-sm text-slate-500">
                  <Users className="w-4 h-4 text-primary" /> Active Members
                </div>
                <span className="text-sm font-semibold">Active</span>
              </div>
              <div className="flex items-center justify-between p-3 rounded-xl bg-slate-50 dark:bg-slate-800/50">
                <div className="flex items-center gap-2 text-sm text-slate-500">
                  <Phone className="w-4 h-4 text-primary" /> Contact
                </div>
                <span className="text-sm font-semibold">{branch.phone || 'No phone'}</span>
              </div>
            </div>

            <button className="w-full flex items-center justify-center gap-2 py-3 rounded-xl border border-primary/20 hover:bg-primary/10 text-primary font-semibold transition-all group">
              View Branch Stats
              <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
            </button>
          </motion.div>
        ))}
      </div>
    </div>
  );
}
