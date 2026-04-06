import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/api_response.dart';
import 'api_client.dart';

final authRepositoryProvider = Provider<AuthRepository>((ref) {
  final dio = ref.watch(apiClientProvider);
  return AuthRepository(dio);
});

class AuthRepository {
  final Dio _dio;
  AuthRepository(this._dio);

  Future<ApiResponse<Map<String, dynamic>>> login(String email, String password) async {
    try {
      final response = await _dio.post('/auth/login', data: {'email': email, 'password': password});
      debugPrint("Login Response: ${response.data}");
      final apiResponse = ApiResponse.fromJson(response.data, (data) => data as Map<String, dynamic>);
      if (apiResponse.status && apiResponse.data != null) {
        final prefs = await SharedPreferences.getInstance();
        final token = apiResponse.data!['access_token'] ?? '';
        await prefs.setString('auth_token', token);
      }
      return apiResponse;
    } on DioException catch (e) {
      debugPrint("Login DioError: ${e.response?.data}");
      String message = "Connection error";
      if (e.response?.data != null && e.response?.data['message'] != null) {
        message = e.response?.data['message'];
      }
      return ApiResponse(status: false, message: message);
    } catch (e) {
      debugPrint("Login Error: $e");
      return ApiResponse(status: false, message: e.toString());
    }
  }

  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('auth_token');
  }
}
