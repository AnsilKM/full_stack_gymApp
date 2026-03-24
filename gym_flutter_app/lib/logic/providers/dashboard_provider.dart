import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/member_model.dart';
import '../../data/repositories/gym_repository.dart';
import '../../data/repositories/member_repository.dart';

class DashboardUIState {
  final String totalMembers;
  final String todayAttendance;
  final String totalRevenue;
  final String monthlyGrowth;
  final List<Member> recentMembers;
  final bool isLoading;
  final String gymName;

  DashboardUIState({
    this.totalMembers = "0",
    this.todayAttendance = "0",
    this.totalRevenue = "₹0",
    this.monthlyGrowth = "0%",
    this.recentMembers = const [],
    this.isLoading = true,
    this.gymName = "",
  });

  DashboardUIState copyWith({
    String? totalMembers,
    String? todayAttendance,
    String? totalRevenue,
    String? monthlyGrowth,
    List<Member>? recentMembers,
    bool? isLoading,
    String? gymName,
  }) {
    return DashboardUIState(
      totalMembers: totalMembers ?? this.totalMembers,
      todayAttendance: todayAttendance ?? this.todayAttendance,
      totalRevenue: totalRevenue ?? this.totalRevenue,
      monthlyGrowth: monthlyGrowth ?? this.monthlyGrowth,
      recentMembers: recentMembers ?? this.recentMembers,
      isLoading: isLoading ?? this.isLoading,
      gymName: gymName ?? this.gymName,
    );
  }
}

class DashboardNotifier extends StateNotifier<DashboardUIState> {
  final GymRepository _gymRepo;
  final MemberRepository _memberRepo;

  DashboardNotifier(this._gymRepo, this._memberRepo) : super(DashboardUIState()) {
    loadDashboardData();
  }

  Future<void> loadDashboardData() async {
    state = state.copyWith(isLoading: true);
    
    try {
      final gymsResponse = await _gymRepo.getGyms();
      if (gymsResponse.status && gymsResponse.data != null && gymsResponse.data!.isNotEmpty) {
        final gym = gymsResponse.data![0];
        
        final statsResponse = await _gymRepo.getDashboardStats(gymId: gym.id);
        final membersResponse = await _memberRepo.getMembers(gymId: gym.id);
        
        String totalMembers = "0";
        String revenue = "₹0";
        String growth = "0%";
        List<Member> recentMembers = [];
        
        if (statsResponse.status && statsResponse.data != null) {
          final stats = statsResponse.data!;
          totalMembers = stats.activeMembers.toString();
          revenue = "₹${stats.totalRevenue.toInt()}";
          growth = stats.monthlyGrowth >= 0 ? "+${stats.monthlyGrowth}%" : "${stats.monthlyGrowth}%";
        }
        
        if (membersResponse.status && membersResponse.data != null) {
          recentMembers = membersResponse.data!.take(5).toList();
        }
        
        state = state.copyWith(
          gymName: gym.name,
          totalMembers: totalMembers,
          totalRevenue: revenue,
          monthlyGrowth: growth,
          recentMembers: recentMembers,
          isLoading: false,
        );
      } else {
        state = state.copyWith(isLoading: false);
      }
    } catch (e) {
      state = state.copyWith(isLoading: false);
    }
  }
}

final dashboardProvider = StateNotifierProvider<DashboardNotifier, DashboardUIState>((ref) {
  return DashboardNotifier(
    ref.watch(gymRepositoryProvider),
    ref.watch(memberRepositoryProvider),
  );
});
