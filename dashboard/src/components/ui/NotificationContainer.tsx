"use client";

import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, CheckCircle, AlertCircle } from 'lucide-react';
import { useNotification } from '@/lib/contexts/NotificationContext';

const NotificationContainer: React.FC = () => {
  const { notifications, removeNotification } = useNotification();

  return (
    <div className="fixed top-6 left-1/2 -translate-x-1/2 z-[9999] flex flex-col gap-3 w-[calc(100%-2rem)] max-w-2xl px-4">
      <AnimatePresence>
        {notifications.map((notification) => (
          <motion.div
            key={notification.id}
            initial={{ opacity: 0, y: -20, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, scale: 0.95, transition: { duration: 0.15 } }}
            className={`flex items-center justify-between py-3 px-6 rounded-full border shadow-sm ${
              notification.type === 'success' 
                ? 'bg-[#ecfdf5] border-[#10b98120] text-[#065f46]' 
                : 'bg-[#fef2f2] border-[#ef444420] text-[#991b1b]'
            }`}
          >
            <p className="text-[13.5px] font-medium leading-relaxed">
              {notification.message}
            </p>
            <button
              onClick={() => removeNotification(notification.id)}
              className={`ml-4 p-1 rounded-full transition-colors flex-shrink-0 ${
                notification.type === 'success' 
                  ? 'hover:bg-[#065f4610]' 
                  : 'hover:bg-[#991b1b10]'
              }`}
            >
              <X className="w-3.5 h-3.5" />
            </button>
          </motion.div>
        ))}
      </AnimatePresence>
    </div>
  );
};

export default NotificationContainer;
