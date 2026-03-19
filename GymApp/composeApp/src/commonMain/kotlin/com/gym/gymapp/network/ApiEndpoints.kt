package com.gym.gymapp.network

object ApiEndpoints {
    private val BASE_URL = NetworkClient.BASE_URL

    // Auth
    val LOGIN = "$BASE_URL/auth/login"
    val REGISTER = "$BASE_URL/auth/register"
    val LOGOUT = "$BASE_URL/auth/logout"


    
    // Members
    val MEMBERS = "$BASE_URL/members"
    fun memberDetails(id: String) = "$BASE_URL/members/$id"
    
    // Gyms
    val GYMS = "$BASE_URL/gyms"
    
    // Attendance
    val ATTENDANCE = "$BASE_URL/attendance/checkin"
    val ATTENDANCE_TODAY = "$BASE_URL/attendance/today"
    
    // Reports
    val DASHBOARD_STATS = "$BASE_URL/reports/dashboard"

    // Plans
    val PLANS = "$BASE_URL/plans"
}
