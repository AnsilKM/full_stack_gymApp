import 'package:flutter/material.dart';
import '../widgets/gym_bottom_bar.dart';
import 'dashboard_screen.dart';
import 'member_list_screen.dart';
import 'profile_screen.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  MainTab _selectedTab = MainTab.home;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      backgroundColor: theme.colorScheme.surface,
      body: _buildBody(),
      bottomNavigationBar: GymBottomBar(
        selectedTab: _selectedTab,
        onTabSelected: (tab) => setState(() => _selectedTab = tab),
      ),
      floatingActionButton: (_selectedTab == MainTab.members || _selectedTab == MainTab.plans)
          ? FloatingActionButton(
              onPressed: () {
                // Handle Add Member or Plan
              },
              backgroundColor: theme.colorScheme.primary,
              foregroundColor: Colors.black,
              shape: const CircleBorder(),
              child: const Icon(Icons.add),
            )
          : null,
    );
  }

  Widget _buildBody() {
    switch (_selectedTab) {
      case MainTab.home:
        return const DashboardScreen();
      case MainTab.members:
        return const MemberListScreen();
      case MainTab.payments:
        return const _PlaceholderScreen(title: "Payments", description: "Payment history and management");
      case MainTab.plans:
        return const _PlaceholderScreen(title: "Membership Plans", description: "Manage your gym membership plans");
      case MainTab.profile:
        return const ProfileScreen();
    }
  }
}

class _PlaceholderScreen extends StatelessWidget {
  final String title;
  final String description;

  const _PlaceholderScreen({required this.title, required this.description});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              title,
              style: theme.textTheme.headlineMedium?.copyWith(fontWeight: FontWeight.bold, color: theme.colorScheme.onSurface),
            ),
            const SizedBox(height: 8),
            Text(
              description,
              style: theme.textTheme.bodyMedium?.copyWith(color: theme.colorScheme.onSurface.withValues(alpha: 0.6)),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }
}
