import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/member_model.dart';
import '../../data/repositories/member_repository.dart';

class MemberListUIState {
  final List<Member> members;
  final List<Member> filteredMembers;
  final String searchQuery;
  final String statusFilter;
  final bool isLoading;
  final bool isRefreshing;
  final bool hasMore;

  MemberListUIState({
    this.members = const [],
    this.filteredMembers = const [],
    this.searchQuery = "",
    this.statusFilter = "ALL",
    this.isLoading = true,
    this.isRefreshing = false,
    this.hasMore = false,
  });

  MemberListUIState copyWith({
    List<Member>? members,
    List<Member>? filteredMembers,
    String? searchQuery,
    String? statusFilter,
    bool? isLoading,
    bool? isRefreshing,
    bool? hasMore,
  }) {
    return MemberListUIState(
      members: members ?? this.members,
      filteredMembers: filteredMembers ?? this.filteredMembers,
      searchQuery: searchQuery ?? this.searchQuery,
      statusFilter: statusFilter ?? this.statusFilter,
      isLoading: isLoading ?? this.isLoading,
      isRefreshing: isRefreshing ?? this.isRefreshing,
      hasMore: hasMore ?? this.hasMore,
    );
  }
}

class MemberListNotifier extends StateNotifier<MemberListUIState> {
  final MemberRepository _repo;
  final String? _gymId;

  MemberListNotifier(this._repo, this._gymId) : super(MemberListUIState()) {
    if (_gymId != null) {
      refresh();
    }
  }

  Future<void> refresh() async {
    if (_gymId == null) return;
    state = state.copyWith(isRefreshing: true);
    await loadMembers();
    state = state.copyWith(isRefreshing: false);
  }

  Future<void> loadMembers() async {
    if (_gymId == null) return;
    state = state.copyWith(isLoading: true);
    try {
      final response = await _repo.getMembers(gymId: _gymId);
      if (response.status && response.data != null) {
        state = state.copyWith(
          members: response.data,
          isLoading: false,
        );
        _filterMembers();
      } else {
        state = state.copyWith(isLoading: false);
      }
    } catch (e) {
      state = state.copyWith(isLoading: false);
    }
  }

  void onSearchQueryChange(String query) {
    state = state.copyWith(searchQuery: query);
    _filterMembers();
  }

  void onStatusFilterChange(String filter) {
    state = state.copyWith(statusFilter: filter);
    _filterMembers();
  }

  void _filterMembers() {
    List<Member> filtered = state.members;

    // Apply Search
    if (state.searchQuery.isNotEmpty) {
      final query = state.searchQuery.toLowerCase();
      filtered = filtered.where((m) => m.name.toLowerCase().contains(query)).toList();
    }

    // Apply Status Filter
    if (state.statusFilter != "ALL") {
      filtered = filtered.where((m) {
        if (state.statusFilter == "EXPIRED") {
          return m.isExpired;
        } else {
          // If filtering by ACTIVE/INACTIVE, ensure we only get non-expired ones if required by logic
          // Actually, if a member is EXPIRED but status is ACTIVE, we might want to exclude them from the "ACTIVE" filter 
          // depending on how the "EXPIRED" filter works.
          return m.status == state.statusFilter && !m.isExpired;
        }
      }).toList();
    }

    state = state.copyWith(filteredMembers: filtered);
  }
}

final memberListProvider = StateNotifierProvider<MemberListNotifier, MemberListUIState>((ref) {
  // In a real app we'd get gymId properly from another provider or storage.
  
  return MemberListNotifier(
    ref.watch(memberRepositoryProvider),
    "gym_123", // placeholder, should be reactive
  );
});
