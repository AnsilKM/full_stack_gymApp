const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:3000";

export async function apiFetch(endpoint: string, options: RequestInit = {}) {
  const token = typeof window !== "undefined" ? localStorage.getItem("access_token") : null;
  
  const headers = {
    "Content-Type": "application/json",
    ...options.headers,
  } as Record<string, string>;

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  console.log(`[API REQUEST] ${options.method || 'GET'} ${endpoint}`);

  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: "An error occurred" }));
    console.error(`[API ERROR] ${options.method || 'GET'} ${endpoint} - Status: ${response.status}`, error);
    throw new Error(error.message || "Something went wrong");
  }

  const data = await response.json();
  console.log(`[API SUCCESS] ${options.method || 'GET'} ${endpoint}`, data);
  return data;
}

export const authApi = {
  login: (credentials: any) => apiFetch("/auth/login", { method: "POST", body: JSON.stringify(credentials) }),
  register: (data: any) => apiFetch("/auth/register", { method: "POST", body: JSON.stringify(data) }),
};

export const membersApi = {
  getAll: (gymId?: string) => apiFetch(`/members${gymId ? `?gymId=${gymId}` : ""}`),
  getOne: (id: string) => apiFetch(`/members/${id}`),
  create: (data: any) => apiFetch("/members", { method: "POST", body: JSON.stringify(data) }),
  update: (id: string, data: any) => apiFetch(`/members/${id}`, { method: "PUT", body: JSON.stringify(data) }),
  delete: (id: string) => apiFetch(`/members/${id}`, { method: "DELETE" }),
};

export const gymsApi = {
  getAll: () => apiFetch("/gyms"),
  getOne: (id: string) => apiFetch(`/gyms/${id}`),
};

export const reportsApi = {
  getStats: (gymId?: string) => apiFetch(`/reports/dashboard${gymId ? `?gymId=${gymId}` : ""}`),
};

export const attendanceApi = {
  checkIn: (data: { memberId: string; gymId: string }) => apiFetch("/attendance/checkin", { method: "POST", body: JSON.stringify(data) }),
  getToday: (gymId: string) => apiFetch(`/attendance/today?gymId=${gymId}`),
  getHistory: (memberId: string) => apiFetch(`/attendance/history/${memberId}`),
};

export const paymentsApi = {
  getAll: (gymId: string) => apiFetch(`/payments?gymId=${gymId}`),
  create: (data: any) => apiFetch("/payments", { method: "POST", body: JSON.stringify(data) }),
};

export const plansApi = {
  getAll: (gymId: string) => apiFetch(`/plans?gymId=${gymId}`),
  getOne: (id: string) => apiFetch(`/plans/${id}`),
  create: (data: any) => apiFetch("/plans", { method: "POST", body: JSON.stringify(data) }),
};

export const adminApi = {
  createGym: (data: any) => apiFetch("/admin/gyms", { method: "POST", body: JSON.stringify(data) }),
  createUser: (data: any) => apiFetch("/admin/users", { method: "POST", body: JSON.stringify(data) }),
  getGyms: () => apiFetch("/admin/gyms"),
  getOwners: () => apiFetch("/admin/owners"),
};
