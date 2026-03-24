import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'api_client.dart';
import '../models/other_models.dart';

final gymRepositoryProvider = Provider<GymRepository>((ref) {
  return GymRepository(ref.watch(apiClientProvider));
});

class GymRepository {
  final Dio _client;

  GymRepository(this._client);

  Future<ApiResponse<List<Gym>>> getGyms() async {
    try {
      final response = await _client.get('/gyms');
      return ApiResponse.fromJson(
        response.data,
        (json) => (json as List).map((e) => Gym.fromJson(e)).toList(),
      );
    } catch (e) {
      return ApiResponse(status: false, message: e.toString());
    }
  }

  Future<ApiResponse<DashboardStats>> getDashboardStats({required String gymId}) async {
    try {
      final response = await _client.get('/dashboard/stats', queryParameters: {'gymId': gymId});
      return ApiResponse.fromJson(
        response.data,
        (json) => DashboardStats.fromJson(json),
      );
    } catch (e) {
      return ApiResponse(status: false, message: e.toString());
    }
  }

  Future<ApiResponse<List<Plan>>> getMembershipPlans({required String gymId}) async {
    try {
      final response = await _client.get('/plans', queryParameters: {'gymId': gymId});
      return ApiResponse.fromJson(
        response.data,
        (json) => (json as List).map((e) => Plan.fromJson(e)).toList(),
      );
    } catch (e) {
      return ApiResponse(status: false, message: e.toString());
    }
  }
}
