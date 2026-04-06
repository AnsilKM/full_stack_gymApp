import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'api_client.dart';
import '../models/api_response.dart';

class MembershipPlan {
  final String id;
  final String name;
  final double price;
  final int durationMonths;
  final String? description;
  final String gymId;

  MembershipPlan({
    required this.id,
    required this.name,
    required this.price,
    required this.durationMonths,
    this.description,
    required this.gymId,
  });

  factory MembershipPlan.fromJson(Map<String, dynamic> json) {
    return MembershipPlan(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      price: (json['price'] as num?)?.toDouble() ?? 0.0,
      durationMonths: json['durationMonths'] ?? 0,
      description: json['description'],
      gymId: json['gymId'] ?? '',
    );
  }

  Map<String, dynamic> toJson() => {
    'name': name,
    'price': price,
    'durationMonths': durationMonths,
    'description': description,
    'gymId': gymId,
  };
}

class PlanRepository {
  final Dio _client;

  PlanRepository(this._client);

  Future<ApiResponse<List<MembershipPlan>>> getPlans(String gymId) async {
    try {
      final response = await _client.get('/plans', queryParameters: {'gymId': gymId});
      return ApiResponse.fromJson(
        response.data,
        (data) => (data as List).map((e) => MembershipPlan.fromJson(e)).toList(),
      );
    } catch (e) {
      return ApiResponse(status: false, message: e.toString(), data: []);
    }
  }

  Future<ApiResponse<MembershipPlan>> createPlan(MembershipPlan plan) async {
    try {
      final response = await _client.post('/plans', data: plan.toJson());
      return ApiResponse.fromJson(
        response.data,
        (data) => MembershipPlan.fromJson(data),
      );
    } catch (e) {
      return ApiResponse(status: false, message: e.toString(), data: null);
    }
  }

  Future<ApiResponse<bool>> deletePlan(String planId) async {
    try {
      final response = await _client.delete('/plans/$planId');
      return ApiResponse.fromJson(response.data, (data) => true);
    } catch (e) {
      return ApiResponse(status: false, message: e.toString(), data: false);
    }
  }
}

final planRepositoryProvider = Provider((ref) => PlanRepository(ref.watch(apiClientProvider)));
