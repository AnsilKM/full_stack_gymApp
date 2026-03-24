import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/member_model.dart';
import '../../data/repositories/member_repository.dart';

class MemberDetailsUIState {
  final Member? member;
  final bool isLoading;
  final bool isEditing;
  final String? errorMessage;
  final bool deleteSuccess;
  final bool updateSuccess;

  MemberDetailsUIState({
    this.member,
    this.isLoading = false,
    this.isEditing = false,
    this.errorMessage,
    this.deleteSuccess = false,
    this.updateSuccess = false,
  });

  MemberDetailsUIState copyWith({
    Member? member,
    bool? isLoading,
    bool? isEditing,
    String? errorMessage,
    bool? deleteSuccess,
    bool? updateSuccess,
  }) {
    return MemberDetailsUIState(
      member: member ?? this.member,
      isLoading: isLoading ?? this.isLoading,
      isEditing: isEditing ?? this.isEditing,
      errorMessage: errorMessage ?? this.errorMessage,
      deleteSuccess: deleteSuccess ?? this.deleteSuccess,
      updateSuccess: updateSuccess ?? this.updateSuccess,
    );
  }
}

class MemberDetailsNotifier extends StateNotifier<MemberDetailsUIState> {
  final MemberRepository _repo;
  final String _memberId;

  MemberDetailsNotifier(this._repo, this._memberId) : super(MemberDetailsUIState()) {
    loadMember();
  }

  Future<void> loadMember() async {
    state = state.copyWith(isLoading: true);
    try {
      final response = await _repo.getMember(_memberId);
      if (response.status && response.data != null) {
        state = state.copyWith(member: response.data, isLoading: false);
      } else {
        state = state.copyWith(isLoading: false, errorMessage: response.message);
      }
    } catch (e) {
      state = state.copyWith(isLoading: false, errorMessage: e.toString());
    }
  }

  void toggleEdit() {
    state = state.copyWith(isEditing: !state.isEditing);
  }

  Future<void> deleteMember() async {
    state = state.copyWith(isLoading: true);
    try {
      final response = await _repo.deleteMember(_memberId);
      if (response.status) {
        state = state.copyWith(deleteSuccess: true, isLoading: false);
      } else {
        state = state.copyWith(isLoading: false, errorMessage: response.message);
      }
    } catch (e) {
      state = state.copyWith(isLoading: false, errorMessage: e.toString());
    }
  }

  Future<void> toggleStatus() async {
    state = state.copyWith(isLoading: true);
    try {
      final response = await _repo.toggleMemberStatus(_memberId);
      if (response.status && response.data != null) {
        state = state.copyWith(member: response.data, isLoading: false, updateSuccess: true);
      } else {
        state = state.copyWith(isLoading: false, errorMessage: response.message);
      }
    } catch (e) {
      state = state.copyWith(isLoading: false, errorMessage: e.toString());
    }
  }
}

final memberDetailsProvider = StateNotifierProvider.family<MemberDetailsNotifier, MemberDetailsUIState, String>((ref, memberId) {
  return MemberDetailsNotifier(ref.watch(memberRepositoryProvider), memberId);
});
