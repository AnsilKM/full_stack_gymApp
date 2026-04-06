import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/api_response.dart';
import '../../data/models/member_model.dart';
import '../../data/repositories/api_client.dart';
import '../../data/repositories/gym_repository.dart';

class Attendance {
  final String id;
  final String memberId;
  final String checkInTime;
  final Member? member;

  Attendance({
    required this.id,
    required this.memberId,
    required this.checkInTime,
    this.member,
  });

  factory Attendance.fromJson(Map<String, dynamic> json) {
    return Attendance(
      id: json['id'] ?? '',
      memberId: json['memberId'] ?? '',
      checkInTime: json['checkInTime'] ?? '',
      member: json['member'] != null ? Member.fromJson(json['member']) : null,
    );
  }
}

class AttendanceRepository {
  final Dio _client;

  AttendanceRepository(this._client);

  Future<ApiResponse<List<Attendance>>> getTodayAttendance(String gymId) async {
    try {
      final response = await _client.get('/attendance/today', queryParameters: {'gymId': gymId});
      return ApiResponse.fromJson(
        response.data,
        (data) => (data as List).map((e) => Attendance.fromJson(e)).toList(),
      );
    } catch (e) {
      return ApiResponse(status: false, message: e.toString(), data: []);
    }
  }

  Future<ApiResponse<void>> resetAttendance() async {
    try {
      await _client.post('/attendance/reset');
      return ApiResponse(status: true, message: "Attendance reset", data: null);
    } catch (e) {
      return ApiResponse(status: false, message: e.toString(), data: null);
    }
  }
}

final attendanceRepositoryProvider = Provider((ref) => AttendanceRepository(ref.watch(apiClientProvider)));

class AttendanceUIState {
  final List<Attendance> attendanceList;
  final bool isLoading;
  final String? errorMessage;

  AttendanceUIState({
    this.attendanceList = const [],
    this.isLoading = false,
    this.errorMessage,
  });

  AttendanceUIState copyWith({
    List<Attendance>? attendanceList,
    bool? isLoading,
    String? errorMessage,
  }) {
    return AttendanceUIState(
      attendanceList: attendanceList ?? this.attendanceList,
      isLoading: isLoading ?? this.isLoading,
      errorMessage: errorMessage ?? this.errorMessage,
    );
  }
}

class AttendanceNotifier extends StateNotifier<AttendanceUIState> {
  final AttendanceRepository _repo;
  final GymRepository _gymRepo;

  AttendanceNotifier(this._repo, this._gymRepo) : super(AttendanceUIState()) {
    loadAttendance();
  }

  Future<void> loadAttendance() async {
    state = state.copyWith(isLoading: true);
    try {
      final gymsResponse = await _gymRepo.getGyms();
      if (gymsResponse.status && gymsResponse.data != null && gymsResponse.data!.isNotEmpty) {
        final response = await _repo.getTodayAttendance(gymsResponse.data!.first.id);
        if (response.status) {
          state = state.copyWith(attendanceList: response.data, isLoading: false);
        } else {
          state = state.copyWith(isLoading: false, errorMessage: response.message);
        }
      } else {
        state = state.copyWith(isLoading: false, errorMessage: "No gym found");
      }
    } catch (e) {
      state = state.copyWith(isLoading: false, errorMessage: e.toString());
    }
  }
}

final attendanceProvider = StateNotifierProvider<AttendanceNotifier, AttendanceUIState>((ref) {
  return AttendanceNotifier(ref.watch(attendanceRepositoryProvider), ref.watch(gymRepositoryProvider));
});
