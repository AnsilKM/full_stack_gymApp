import type { Metadata } from "next";
import { Poppins } from "next/font/google";
import "./globals.css";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { NotificationProvider } from "@/lib/contexts/NotificationContext";
import NotificationContainer from "@/components/ui/NotificationContainer";

const poppins = Poppins({ 
  subsets: ["latin"],
  weight: ["300", "400", "500", "600", "700", "800", "900"],
  display: 'swap',
});

export const metadata: Metadata = {
  title: "GreenFitness Admin Dashboard",
  description: "Modern Gym Management SaaS Platform",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={`${poppins.className} bg-background-light dark:bg-background-dark text-slate-900 dark:text-slate-100`}>
        <NotificationProvider>
          <div className="flex min-h-screen">
            <Sidebar />
            <div className="flex-1 flex flex-col lg:ml-64 transition-[margin] duration-300">
              <Header />
              <main className="flex-1 pt-16 px-8 py-8 overflow-auto">
                {children}
              </main>
            </div>
          </div>
          <NotificationContainer />
        </NotificationProvider>
      </body>
    </html>
  );
}
