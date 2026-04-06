import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/api_response.dart';
import 'api_client.dart';

final reportsRepositoryProvider = Provider<ReportsRepository>((ref) {
  final dio = ref.watch(apiClientProvider);
  return ReportsRepository(dio);
});

class DashboardStats {
  final int totalMembers;
  final int activeMembers;
  final int expiredMembers;
  final double totalRevenue;
  final int todayAttendance;

  DashboardStats({
    required this.totalMembers,
    required this.activeMembers,
    required this.expiredMembers,
    required this.totalRevenue,
    required this.todayAttendance,
  });

  factory DashboardStats.fromJson(Map<String, dynamic> json) {
    return DashboardStats(
      totalMembers: json['totalMembers'] ?? 0,
      activeMembers: json['activeMembers'] ?? 0,
      expiredMembers: json['expiredMembers'] ?? 0,
      totalRevenue: (json['totalRevenue'] ?? 0).toDouble(),
      todayAttendance: json['todayAttendance'] ?? 0,
    );
  }
}

class ReportsRepository {
  final Dio _dio;

  ReportsRepository(this._dio);

  Future<ApiResponse<DashboardStats>> getDashboardStats() async {
    try {
      final response = await _dio.get('/reports/dashboard');
      return ApiResponse.fromJson(
        response.data,
        (data) => DashboardStats.fromJson(data),
      );
    } catch (e) {
      return ApiResponse(status: false, message: e.toString());
    }
  }
}
