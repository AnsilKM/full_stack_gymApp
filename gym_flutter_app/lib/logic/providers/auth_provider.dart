import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../data/repositories/auth_repository.dart';

import 'notification_provider.dart';

final authNotifierProvider = StateNotifierProvider<AuthNotifier, AuthUIState>((ref) {
  final authRepository = ref.watch(authRepositoryProvider);
  return AuthNotifier(authRepository, ref);
});

class AuthUIState {
  final bool isLoading;
  final String? errorMessage;
  final bool isLoggedIn;

  AuthUIState({this.isLoading = false, this.errorMessage, this.isLoggedIn = false});

  AuthUIState copyWith({bool? isLoading, String? errorMessage, bool? isLoggedIn}) {
    return AuthUIState(
      isLoading: isLoading ?? this.isLoading,
      errorMessage: errorMessage ?? this.errorMessage,
      isLoggedIn: isLoggedIn ?? this.isLoggedIn,
    );
  }
}

class AuthNotifier extends StateNotifier<AuthUIState> {
  final AuthRepository _authRepository;
  final Ref _ref;
  AuthNotifier(this._authRepository, this._ref) : super(AuthUIState());

  Future<void> login(String email, String password) async {
    state = state.copyWith(isLoading: true, errorMessage: null);
    final response = await _authRepository.login(email, password);
    if (response.status) {
      state = state.copyWith(isLoading: false, isLoggedIn: true);
      _ref.read(notificationProvider.notifier).showSuccess("Welcome to GreenFitness!");
      _ref.invalidate(authStateProvider);
    } else {
      state = state.copyWith(isLoading: false, errorMessage: response.message);
      _ref.read(notificationProvider.notifier).showError(response.message);
    }
  }

  Future<void> logout() async {
    await _authRepository.logout();
    state = AuthUIState();
    _ref.invalidate(authStateProvider);
  }
}

final authStateProvider = FutureProvider<bool>((ref) async {
  final prefs = await SharedPreferences.getInstance();
  return prefs.getString('auth_token') != null;
});
