"use client";

import { useState, useEffect } from "react";
import { Search, Filter, Plus, MoreVertical, Mail, Phone, Calendar, Trash2, Edit2, ShieldCheck, ShieldAlert, Heart, RefreshCcw } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import QRCode from "react-qr-code";
import { membersApi, plansApi, gymsApi, API_URL } from "@/lib/api";
import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";
import { useNotification } from "@/lib/contexts/NotificationContext";

import ConfirmationModal from "@/components/ui/ConfirmationModal";
import Loader from "@/components/ui/Loader";

function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

interface Member {
  id: string;
  userId: string;
  user: {
    name: string;
    email: string;
    phone?: string;
  };
  status: string;
  bloodGroup?: string;
  photoUrl?: string;
  createdAt: string;
}

export default function MembersPage() {
  const { showNotification } = useNotification();
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [members, setMembers] = useState<Member[]>([]);
  const [plans, setPlans] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showIdModal, setShowIdModal] = useState(false);
  const [selectedMember, setSelectedMember] = useState<Member | null>(null);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [memberToDelete, setMemberToDelete] = useState<string | null>(null);
  const [isStatusModalOpen, setIsStatusModalOpen] = useState(false);
  const [memberToToggle, setMemberToToggle] = useState<Member | null>(null);
  
  // Form State
  const [formData, setFormData] = useState({
    name: "", email: "", phone: "", bloodGroup: "", status: "ACTIVE", gymId: "", planId: ""
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setIsLoading(true);
    try {
      const g = await gymsApi.getAll();
      if (g.length > 0) {
        const gymId = g[0].id; // Use first gym for demo
        const [m, p] = await Promise.all([
          membersApi.getAll(gymId),
          plansApi.getAll(gymId)
        ]);
        setMembers(m);
        setPlans(p);
        setFormData(prev => ({ ...prev, gymId }));
      }
    } catch (err: any) {
      showNotification(err.message || "Failed to fetch data", "error");
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    setMemberToDelete(id);
    setIsDeleteModalOpen(true);
  };

  const confirmDelete = async () => {
    if (!memberToDelete) return;
    try {
      await membersApi.delete(memberToDelete);
      showNotification("Member deleted successfully", "success");
      fetchData();
    } catch (err: any) {
      showNotification(err.message || "Error deleting member", "error");
    } finally {
      setMemberToDelete(null);
    }
  };

  const handleToggleStatus = (member: Member) => {
    setMemberToToggle(member);
    setIsStatusModalOpen(true);
  };

  const confirmToggleStatus = async () => {
    if (!memberToToggle) return;
    const newStatus = memberToToggle.status === "ACTIVE" ? "INACTIVE" : "ACTIVE";
    try {
      await membersApi.update(memberToToggle.id, { status: newStatus });
      showNotification(`Status updated to ${newStatus}`, "success");
      fetchData();
    } catch (err: any) {
      showNotification(err.message || "Error updating status", "error");
    } finally {
      setMemberToToggle(null);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (selectedMember) {
        await membersApi.update(selectedMember.id, formData);
      } else {
        await membersApi.create(formData);
      }
      showNotification(selectedMember ? "Member updated successfully" : "Member registered successfully", "success");
      setShowAddModal(false);
      setSelectedMember(null);
      setFormData({ name: "", email: "", phone: "", bloodGroup: "", status: "ACTIVE", gymId: formData.gymId, planId: "" });
      fetchData();
    } catch (err: any) {
      showNotification(err.message || "Error saving member", "error");
    }
  };

  const openEdit = (member: Member) => {
    setSelectedMember(member);
    setFormData({
      name: member.user.name,
      email: member.user.email,
      phone: member.user.phone || "",
      bloodGroup: member.bloodGroup || "",
      status: member.status,
      gymId: formData.gymId,
      planId: ""
    });
    setShowAddModal(true);
  };

  const filteredMembers = members.filter(m => {
    const matchesSearch = m.user.name.toLowerCase().includes(searchTerm.toLowerCase()) || 
                         m.user.email.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === "ALL" || m.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const filterTabs = [
    { label: "All", value: "ALL" },
    { label: "Active", value: "ACTIVE" },
    { label: "Inactive", value: "INACTIVE" },
    { label: "Expired", value: "EXPIRED" },
  ];

  return (
    <div className="space-y-8">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight mb-2">Members Management</h1>
          <p className="text-slate-500 dark:text-slate-400">View and manage all members across your branches.</p>
        </div>
        <button 
          onClick={() => { setSelectedMember(null); setShowAddModal(true); }}
          className="btn-primary flex items-center gap-2"
        >
          <Plus className="w-4 h-4" />
          Add New Member
        </button>
      </div>

      <div className="flex flex-col md:flex-row items-center gap-4">
        <div className="relative flex-1 w-full">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
          <input 
            type="text" 
            placeholder="Search by name, email, or phone..." 
            className="w-full bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl py-2.5 pl-10 pr-4 outline-none focus:ring-2 focus:ring-primary/20 transition-all text-sm"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        
        <div className="flex items-center p-1 bg-slate-100 dark:bg-slate-800/50 rounded-xl w-full md:w-auto overflow-x-auto whitespace-nowrap hide-scrollbar">
          {filterTabs.map((tab) => (
            <button
              key={tab.value}
              onClick={() => setStatusFilter(tab.value)}
              className={cn(
                "px-4 py-1.5 text-xs font-bold rounded-lg transition-all flex-1 md:flex-none",
                statusFilter === tab.value 
                  ? "bg-white dark:bg-slate-700 text-primary shadow-sm" 
                  : "text-slate-500 hover:text-slate-700 dark:hover:text-slate-300"
              )}
            >
              {tab.label}
            </button>
          ))}
        </div>
      </div>

      <div className="card p-0 overflow-hidden shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-800">
              <tr>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Member</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Contact</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Details</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Status</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Joined</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200 dark:divide-slate-800">
              {isLoading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center">
                    <Loader size="md" className="mx-auto" />
                  </td>
                </tr>
              ) : filteredMembers.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center text-slate-500">
                    No members found.
                  </td>
                </tr>
              ) : filteredMembers.map((member, index) => (
                <motion.tr 
                  key={member.id}
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.05 }}
                  className="hover:bg-slate-50 dark:hover:bg-slate-800/30 transition-colors"
                >
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-2xl bg-primary/10 flex items-center justify-center text-primary font-bold overflow-hidden">
                        {member.photoUrl ? (
                          <img 
                            src={member.photoUrl.startsWith('http') ? member.photoUrl : `${API_URL}${member.photoUrl}`} 
                            alt={member.user.name} 
                            className="w-full h-full object-cover"
                          />
                        ) : (
                          member.user.name.charAt(0)
                        )}
                      </div>
                      <p className="font-semibold text-sm">{member.user.name}</p>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2 text-[11px] text-slate-500">
                        <Mail className="w-3 h-3" /> {member.user.email}
                      </div>
                      <div className="flex items-center gap-2 text-[11px] text-slate-500">
                        <Phone className="w-3 h-3" /> {member.user.phone || 'N/A'}
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2 text-xs font-medium text-slate-600 dark:text-slate-400">
                      <Heart className="w-3 h-3 text-red-500" /> {member.bloodGroup || 'N/A'}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <button 
                      onClick={() => handleToggleStatus(member)}
                      className={cn(
                        "px-2 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider transition-all hover:scale-105",
                        member.status === 'ACTIVE' ? 'bg-emerald-500/10 text-emerald-500 hover:bg-emerald-500/20' : 
                        member.status === 'EXPIRED' ? 'bg-red-500/10 text-red-500 hover:bg-red-500/20' : 'bg-slate-500/10 text-slate-500'
                      )}
                    >
                      {member.status}
                    </button>
                  </td>
                  <td className="px-6 py-4 text-[11px] text-slate-500">
                    <div className="flex items-center gap-2">
                      <Calendar className="w-3 h-3" /> {new Date(member.createdAt).toLocaleDateString()}
                    </div>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex items-center justify-end gap-2">
                       <button 
                        onClick={() => { setSelectedMember(member); setShowIdModal(true); }}
                        className="p-2 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg transition-colors text-slate-500"
                        title="View ID Card"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect width="18" height="18" x="3" y="3" rx="2" ry="2"/><path d="M3 9h18"/><path d="M9 21V9"/></svg>
                      </button>
                       <button 
                        onClick={() => openEdit(member)}
                        className="p-2 hover:bg-primary/10 rounded-lg transition-colors text-primary"
                        title="Edit Member"
                      >
                        <Edit2 className="w-4 h-4" />
                      </button>
                      <button 
                        onClick={() => handleDelete(member.id)}
                        className="p-2 hover:bg-red-100 rounded-lg transition-colors text-red-500"
                        title="Delete Member"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal */}
      <AnimatePresence>
        {showAddModal && (
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-[100] flex items-center justify-center p-4">
            <motion.div 
              initial={{ scale: 0.95, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.95, opacity: 0 }}
              className="bg-white dark:bg-slate-900 w-full max-w-lg rounded-3xl p-8 shadow-2xl overflow-y-auto max-h-[90vh]"
            >
              <h2 className="text-2xl font-bold mb-6">{selectedMember ? 'Edit Member' : 'Register New Member'}</h2>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div className="col-span-2 sm:col-span-1">
                    <label className="text-xs font-bold text-slate-500 uppercase ml-1">Full Name</label>
                    <input required className="input-field" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} />
                  </div>
                  <div className="col-span-2 sm:col-span-1">
                    <label className="text-xs font-bold text-slate-500 uppercase ml-1">Email</label>
                    <input required type="email" className="input-field" value={formData.email} onChange={e => setFormData({...formData, email: e.target.value})} />
                  </div>
                  <div className="col-span-2 sm:col-span-1">
                    <label className="text-xs font-bold text-slate-500 uppercase ml-1">Phone</label>
                    <input className="input-field" value={formData.phone} onChange={e => setFormData({...formData, phone: e.target.value})} />
                  </div>
                  <div className="col-span-2 sm:col-span-1">
                    <label className="text-xs font-bold text-slate-500 uppercase ml-1">Blood Group</label>
                    <select className="input-field" value={formData.bloodGroup} onChange={e => setFormData({...formData, bloodGroup: e.target.value})}>
                      <option value="">Select</option>
                      {['A+', 'A-', 'B+', 'B-', 'O+', 'O-', 'AB+', 'AB-'].map(bg => <option key={bg} value={bg}>{bg}</option>)}
                    </select>
                  </div>
                  <div className="col-span-2">
                    <label className="text-xs font-bold text-slate-500 uppercase ml-1">Membership Plan</label>
                    <select required className="input-field" value={formData.planId} onChange={e => setFormData({...formData, planId: e.target.value})}>
                      <option value="">Select a Plan</option>
                      {plans.map(p => <option key={p.id} value={p.id}>{p.name} - ₹{p.price}</option>)}
                    </select>
                  </div>
                   <div className="col-span-2">
                    <label className="text-xs font-bold text-slate-500 uppercase ml-1">Status</label>
                    <select className="input-field" value={formData.status} onChange={e => setFormData({...formData, status: e.target.value})}>
                      <option value="ACTIVE">ACTIVE</option>
                      <option value="INACTIVE">INACTIVE</option>
                    </select>
                  </div>
                </div>
                <div className="flex gap-4 pt-6">
                  <button type="button" onClick={() => setShowAddModal(false)} className="flex-1 py-3 font-bold text-slate-500">Cancel</button>
                  <button type="submit" className="flex-1 py-3 bg-primary text-white rounded-2xl font-bold shadow-lg shadow-primary/20">
                    {selectedMember ? 'Update Member' : 'Create Member'}
                  </button>
                </div>
              </form>
            </motion.div>
          </div>
        )}
      </AnimatePresence>

      {/* ID Card Modal */}
      <AnimatePresence>
        {showIdModal && selectedMember && (
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-[100] flex items-center justify-center p-4" onClick={() => setShowIdModal(false)}>
            <motion.div 
              initial={{ scale: 0.95, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.95, opacity: 0 }}
              onClick={(e) => e.stopPropagation()}
              className="relative w-full max-sm rounded-[2rem] overflow-hidden shadow-2xl bg-gradient-to-br from-slate-900 to-slate-800"
            >
              <div className="p-8 pb-32">
                <p className="text-white/60 text-xs font-bold tracking-widest uppercase mb-1">GYM PASS</p>
                <h3 className="text-2xl font-black text-white uppercase tracking-tight leading-none mb-1">
                  {selectedMember.user.name}
                </h3>
                <p className="text-primary font-bold text-sm">
                  {plans.find(p => p.id === formData.planId)?.name || 'Standard Membership'}
                </p>
                
                <div className="absolute top-8 right-8">
                  <span className={cn(
                    "px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider shadow-lg",
                    selectedMember.status === 'ACTIVE' ? 'bg-emerald-500 text-white' : 
                    selectedMember.status === 'EXPIRED' ? 'bg-red-500 text-white' : 'bg-slate-500 text-white'
                  )}>
                    {selectedMember.status}
                  </span>
                </div>
              </div>
              
              <div className="absolute bottom-0 left-0 right-0 bg-white dark:bg-slate-950 p-6 pt-12 rounded-t-[2rem] flex flex-col items-center">
                <div className="absolute -top-16 p-4 bg-white dark:bg-slate-950 rounded-2xl shadow-xl">
                  <QRCode 
                    value={selectedMember.id} 
                    size={100}
                    level="Q"
                    className="rounded-lg"
                    style={{ height: "auto", maxWidth: "100%", width: "100%" }}
                  />
                </div>
                
                <p className="text-slate-400 text-xs font-mono tracking-widest mt-4">
                  ID: {selectedMember.id.substring(0, 8).toUpperCase()}
                </p>
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>

      <ConfirmationModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onConfirm={confirmDelete}
        title="Delete Member"
        message="Are you sure you want to permanently delete this member? This action cannot be reversed and all related data will be lost."
        confirmText="Delete"
        type="danger"
      />

      <ConfirmationModal
        isOpen={isStatusModalOpen}
        onClose={() => setIsStatusModalOpen(false)}
        onConfirm={confirmToggleStatus}
        title={memberToToggle?.status === 'ACTIVE' ? 'Deactivate Member' : 'Activate Member'}
        message={memberToToggle?.status === 'ACTIVE' 
          ? `Are you sure you want to deactivate ${memberToToggle?.user.name}? This will restrict their access to the gym.` 
          : `Activate ${memberToToggle?.user.name}? This will restore their access immediately.`
        }
        confirmText={memberToToggle?.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
        type={memberToToggle?.status === 'ACTIVE' ? 'danger' : 'info'}
      />
    </div>
  );
}
