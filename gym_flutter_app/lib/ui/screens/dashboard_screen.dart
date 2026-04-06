import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../widgets/stat_card.dart';
import '../widgets/member_item.dart';
import '../../logic/providers/dashboard_provider.dart';
import 'member_list_screen.dart';
import 'member_profile_screen.dart';
import 'attendance_screen.dart';
import 'add_member_screen.dart';

class DashboardScreen extends ConsumerWidget {
  const DashboardScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final uiState = ref.watch(dashboardProvider);
    final theme = Theme.of(context);

    return Scaffold(
      backgroundColor: theme.colorScheme.surface,
      body: LayoutBuilder(
        builder: (context, constraints) {
          final isTablet = constraints.maxWidth > 600;

          if (isTablet) {
            return _TabletLayout(uiState: uiState);
          } else {
            return _MobileLayout(uiState: uiState, ref: ref);
          }
        },
      ),
    );
  }
}

class _MobileLayout extends StatelessWidget {
  final DashboardUIState uiState;
  final WidgetRef ref;

  const _MobileLayout({required this.uiState, required this.ref});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return RefreshIndicator(
      onRefresh: () => ref.read(dashboardProvider.notifier).loadDashboardData(),
      child: ListView(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        children: [
          const SizedBox(height: 60),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      uiState.gymName.isEmpty ? "Gym Dashboard" : uiState.gymName,
                      style: theme.textTheme.headlineSmall?.copyWith(
                        fontWeight: FontWeight.w900,
                        color: theme.colorScheme.onSurface,
                      ),
                      overflow: TextOverflow.ellipsis,
                    ),
                    Text(
                      "Main Branch Overview",
                      style: theme.textTheme.bodyMedium?.copyWith(
                        color: theme.colorScheme.onSurface.withValues(alpha: 0.6),
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 16),
              Image.asset(
                'assets/icon.png',
                height: 42,
              ),
            ],
          ),
          const SizedBox(height: 20),
          if (uiState.isLoading)
            const Center(child: CircularProgressIndicator())
          else ...[
            Text(
              "Overview",
              style: theme.textTheme.titleSmall?.copyWith(
                fontWeight: FontWeight.bold,
                color: theme.colorScheme.onSurface,
              ),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: StatCard(
                    title: "Total Members",
                    value: uiState.totalMembers,
                    icon: Icons.groups,
                    accentColor: theme.colorScheme.primary,
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: StatCard(
                    title: "Revenue",
                    value: uiState.totalRevenue,
                    icon: Icons.payments,
                    accentColor: theme.colorScheme.onSurface,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            StatCard(
              title: "Growth",
              value: uiState.monthlyGrowth,
              icon: Icons.trending_up,
              accentColor: theme.colorScheme.onSurface,
            ),
            const SizedBox(height: 24),
            Text(
              "Quick Actions",
              style: theme.textTheme.titleSmall?.copyWith(
                fontWeight: FontWeight.bold,
                color: theme.colorScheme.onSurface,
              ),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: _ActionButton(
                    label: "Add Member",
                    icon: Icons.add,
                    isPrimary: true,
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => const AddMemberScreen()),
                      );
                    },
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _ActionButton(
                    label: "Attendance",
                    icon: Icons.checklist,
                    isPrimary: false,
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => const AttendanceScreen()),
                      );
                    },
                  ),
                ),
              ],
            ),
            const SizedBox(height: 24),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  "Recent Members",
                  style: theme.textTheme.titleSmall?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: theme.colorScheme.onSurface,
                  ),
                ),
                TextButton(
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (context) => const MemberListScreen()),
                    );
                  },
                  child: Text(
                    "See More",
                    style: TextStyle(
                      color: theme.colorScheme.primary,
                      fontSize: 11,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ],
            ),
            if (uiState.recentMembers.isEmpty)
              const _EmptyCard(text: "No members joined yet")
            else
              ListView.separated(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: uiState.recentMembers.length,
                separatorBuilder: (context, index) => const SizedBox(height: 12),
                itemBuilder: (context, index) {
                  final member = uiState.recentMembers[index];
                  return MemberItem(
                    name: member.name,
                    status: member.status,
                    isActuallyExpired: member.isExpired,
                    joinDate: member.joiningDateDisplay,
                    expiryDate: member.expiryDateDisplay,
                    phone: member.phone,
                    imageUrl: member.photoUrl,
                    onClick: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => MemberProfileScreen(memberId: member.id)),
                      );
                    },
                  );
                },
              ),
            const SizedBox(height: 24),
          ],
        ],
      ),
    );
  }
}

class _TabletLayout extends StatelessWidget {
  final DashboardUIState uiState;

  const _TabletLayout({required this.uiState});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Padding(
      padding: const EdgeInsets.all(24.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Expanded(
            flex: 12,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  uiState.gymName.isEmpty ? "Gym Dashboard" : uiState.gymName,
                  style: theme.textTheme.headlineMedium?.copyWith(
                    fontWeight: FontWeight.w900,
                  ),
                ),
                Text(
                  "Main Branch Overview",
                  style: theme.textTheme.bodyLarge?.copyWith(
                    color: theme.colorScheme.onSurface.withValues(alpha: 0.6),
                  ),
                ),
                const SizedBox(height: 32),
                const Text("Overview", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                const SizedBox(height: 16),
                Row(
                  children: [
                    Expanded(
                      child: StatCard(
                        title: "Total Members",
                        value: uiState.totalMembers,
                        icon: Icons.groups,
                        accentColor: theme.colorScheme.primary,
                      ),
                    ),
                    const SizedBox(width: 16),
                    Expanded(
                      child: StatCard(
                        title: "Revenue",
                        value: uiState.totalRevenue,
                        icon: Icons.payments,
                        accentColor: theme.colorScheme.onSurface,
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                StatCard(
                  title: "Growth",
                  value: uiState.monthlyGrowth,
                  icon: Icons.trending_up,
                  accentColor: theme.colorScheme.onSurface,
                ),
                const SizedBox(height: 40),
                const Text("Quick Actions", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                const SizedBox(height: 16),
                Row(
                  children: [
                    Expanded(
                      child: _ActionButton(
                        label: "Add Member",
                        icon: Icons.add,
                        isPrimary: true,
                        onPressed: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(builder: (context) => const AddMemberScreen()),
                          );
                        },
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: _ActionButton(
                        label: "Attendance",
                        icon: Icons.checklist,
                        isPrimary: false,
                        onPressed: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(builder: (context) => const AttendanceScreen()),
                          );
                        },
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
          const SizedBox(width: 32),
          Expanded(
            flex: 10,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text("Recent Members", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                    TextButton(
                      onPressed: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(builder: (context) => const MemberListScreen()),
                        );
                      },
                      child: Text(
                        "See More",
                        style: TextStyle(
                          color: theme.colorScheme.primary,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                Expanded(
                  child: uiState.recentMembers.isEmpty
                      ? const _EmptyCard(text: "No members joined yet")
                      : ListView.separated(
                          itemCount: uiState.recentMembers.length,
                          separatorBuilder: (context, index) => const SizedBox(height: 12),
                          itemBuilder: (context, index) {
                            final member = uiState.recentMembers[index];
                            return MemberItem(
                              name: member.name,
                              status: member.status,
                              isActuallyExpired: member.isExpired,
                              joinDate: member.joiningDateDisplay,
                              expiryDate: member.expiryDateDisplay,
                              phone: member.phone,
                              imageUrl: member.photoUrl,
                              onClick: () {
                                Navigator.push(
                                  context,
                                  MaterialPageRoute(builder: (context) => MemberProfileScreen(memberId: member.id)),
                                );
                              },
                            );
                          },
                        ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _EmptyCard extends StatelessWidget {
  final String text;

  const _EmptyCard({required this.text});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Container(
      width: double.infinity,
      height: 80,
      decoration: BoxDecoration(
        color: theme.colorScheme.surface,
        borderRadius: BorderRadius.circular(20),
      ),
      alignment: Alignment.center,
      child: Text(
        text,
        style: TextStyle(
          color: theme.colorScheme.onSurface.withValues(alpha: 0.6),
          fontSize: 13,
        ),
      ),
    );
  }
}

class _ActionButton extends StatelessWidget {
  final String label;
  final IconData icon;
  final bool isPrimary;
  final VoidCallback onPressed;

  const _ActionButton({
    required this.label,
    required this.icon,
    required this.isPrimary,
    required this.onPressed,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return SizedBox(
      height: 56,
      child: ElevatedButton.icon(
        onPressed: onPressed,
        icon: Icon(icon),
        label: Text(label),
        style: ElevatedButton.styleFrom(
          backgroundColor: isPrimary ? theme.colorScheme.primary : Colors.transparent,
          foregroundColor: isPrimary ? Colors.black : theme.colorScheme.onSurface,
          elevation: 0,
          side: isPrimary ? BorderSide.none : BorderSide(color: theme.colorScheme.onSurface.withValues(alpha: 0.2)),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        ),
      ),
    );
  }
}
