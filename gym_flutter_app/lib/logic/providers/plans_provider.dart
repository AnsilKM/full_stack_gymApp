import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/repositories/plan_repository.dart';
import '../../data/repositories/gym_repository.dart';
import 'notification_provider.dart';

class PlansUIState {
  final List<MembershipPlan> plans;
  final bool isLoading;
  final String? errorMessage;

  PlansUIState({
    this.plans = const [],
    this.isLoading = false,
    this.errorMessage,
  });

  PlansUIState copyWith({
    List<MembershipPlan>? plans,
    bool? isLoading,
    String? errorMessage,
  }) {
    return PlansUIState(
      plans: plans ?? this.plans,
      isLoading: isLoading ?? this.isLoading,
      errorMessage: errorMessage ?? this.errorMessage,
    );
  }
}

class PlansNotifier extends StateNotifier<PlansUIState> {
  final PlanRepository _repo;
  final GymRepository _gymRepo;
  final Ref _ref;

  PlansNotifier(this._repo, this._gymRepo, this._ref) : super(PlansUIState()) {
    loadPlans();
  }

  Future<void> loadPlans() async {
    state = state.copyWith(isLoading: true);
    try {
      final gymsResponse = await _gymRepo.getGyms();
      if (gymsResponse.status && gymsResponse.data != null && gymsResponse.data!.isNotEmpty) {
        final response = await _repo.getPlans(gymsResponse.data!.first.id);
        if (response.status) {
          state = state.copyWith(plans: response.data, isLoading: false);
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

  Future<void> deletePlan(String planId) async {
    final response = await _repo.deletePlan(planId);
    if (response.status) {
      _ref.read(notificationProvider.notifier).showSuccess("Plan deleted successfully");
      loadPlans();
    } else {
      _ref.read(notificationProvider.notifier).showError(response.message);
    }
  }
}

final plansProvider = StateNotifierProvider<PlansNotifier, PlansUIState>((ref) {
  return PlansNotifier(
    ref.watch(planRepositoryProvider),
    ref.watch(gymRepositoryProvider),
    ref,
  );
});
