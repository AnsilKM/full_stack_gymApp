import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/api_response.dart';
import '../models/member_model.dart';
import 'api_client.dart';

final memberRepositoryProvider = Provider<MemberRepository>((ref) {
  final dio = ref.watch(apiClientProvider);
  return MemberRepository(dio);
});

class MemberRepository {
  final Dio _dio;
  MemberRepository(this._dio);

  Future<ApiResponse<Member>> createMember(Map<String, dynamic> data) async {
    try {
      final response = await _dio.post('/members', data: data);
      return ApiResponse.fromJson(response.data, (data) => Member.fromJson(data));
    } catch (e) {
      return ApiResponse(status: false, message: e.toString());
    }
  }

  Future<ApiResponse<String>> uploadImage(List<int> imageBytes) async {
    try {
      final formData = FormData.fromMap({
        'file': MultipartFile.fromBytes(imageBytes, filename: 'profile.jpg'),
      });
      final response = await _dio.post('/members/upload', data: formData);
      return ApiResponse.fromJson(response.data, (data) => (data as Map)['url'] ?? '');
    } catch (e) {
      return ApiResponse(status: false, message: e.toString());
    }
  }

  Future<ApiResponse<List<Member>>> getMembers({required String gymId, int page = 1, int limit = 50}) async {
    try {
      final response = await _dio.get('/members', queryParameters: {'gymId': gymId, 'page': page, 'limit': limit});
      return ApiResponse.fromJson(response.data, (data) => (data as List).map((m) => Member.fromJson(m)).toList());
    } catch (e) {
      return ApiResponse(status: false, message: e.toString());
    }
  }

  Future<ApiResponse<Member>> getMember(String id) async {
    try {
      final response = await _dio.get('/members/$id');
      return ApiResponse.fromJson(response.data, (data) => Member.fromJson(data));
    } catch (e) {
      return ApiResponse(status: false, message: e.toString());
    }
  }

  Future<ApiResponse<Member>> updateMember(String id, Map<String, dynamic> data) async {
    try {
      final response = await _dio.patch('/members/$id', data: data);
      return ApiResponse.fromJson(response.data, (data) => Member.fromJson(data));
    } catch (e) {
      return ApiResponse(status: false, message: e.toString());
    }
  }

  Future<ApiResponse<void>> deleteMember(String id) async {
    try {
      final response = await _dio.delete('/members/$id');
      return ApiResponse(status: response.data['status'] ?? false, message: response.data['message'] ?? '');
    } catch (e) {
      return ApiResponse(status: false, message: e.toString());
    }
  }

  Future<ApiResponse<Member>> toggleMemberStatus(String id) async {
    try {
      final response = await _dio.post('/members/$id/toggle-status');
      return ApiResponse.fromJson(response.data, (data) => Member.fromJson(data));
    } catch (e) {
      return ApiResponse(status: false, message: e.toString());
    }
  }
}

