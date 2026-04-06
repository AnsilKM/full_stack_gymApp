import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'ui/theme/app_theme.dart';
import 'ui/screens/login_screen.dart';
import 'ui/screens/main_screen.dart';
import 'logic/providers/auth_provider.dart';

import 'logic/providers/notification_provider.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const ProviderScope(child: GymApp()));
}

class GymApp extends ConsumerWidget {
  const GymApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authStateProvider);
    return MaterialApp(
      title: 'ProGym Management',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.darkTheme,
      builder: (context, child) => GlobalNotificationListener(child: child!),
      home: authState.when(
        data: (isLoggedIn) => isLoggedIn ? const MainScreen() : const LoginScreen(),
        loading: () => const Scaffold(body: Center(child: CircularProgressIndicator())),
        error: (err, _) => Scaffold(body: Center(child: Text('Error: $err'))),
      ),
    );
  }
}
