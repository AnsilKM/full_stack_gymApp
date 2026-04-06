import 'package:flutter/material.dart';

enum MainTab { home, members, payments, plans, profile }

class GymBottomBar extends StatelessWidget {
  final MainTab selectedTab;
  final Function(MainTab) onTabSelected;

  const GymBottomBar({
    super.key,
    required this.selectedTab,
    required this.onTabSelected,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    
    return Container(
      padding: const EdgeInsets.only(bottom: 20, left: 16, right: 16),
      height: 92,
      child: Center(
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 6),
          decoration: BoxDecoration(
            color: theme.colorScheme.surface,
            borderRadius: BorderRadius.circular(36),
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: MainTab.values.map((tab) {
              final isSelected = selectedTab == tab;
              return _BottomBarItem(
                tab: tab,
                isSelected: isSelected,
                onTap: () => onTabSelected(tab),
              );
            }).toList(),
          ),
        ),
      ),
    );
  }
}

class _BottomBarItem extends StatelessWidget {
  final MainTab tab;
  final bool isSelected;
  final VoidCallback onTap;

  const _BottomBarItem({
    required this.tab,
    required this.isSelected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return GestureDetector(
      onTap: onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeInOut,
        padding: EdgeInsets.symmetric(horizontal: isSelected ? 10 : 6, vertical: 6),
        margin: const EdgeInsets.symmetric(horizontal: 2),
        decoration: BoxDecoration(
          color: isSelected ? theme.colorScheme.onSurface.withValues(alpha: 0.05) : Colors.transparent,
          borderRadius: BorderRadius.circular(24),
        ),
        child: Row(
          children: [
            Container(
              width: 36,
              height: 36,
              decoration: BoxDecoration(
                color: isSelected ? theme.colorScheme.primary : Colors.transparent,
                shape: BoxShape.circle,
              ),
              child: Icon(
                _getIcon(tab),
                color: isSelected ? Colors.black : theme.colorScheme.onSurface.withValues(alpha: 0.5),
                size: 24,
              ),
            ),
            if (isSelected) ...[
              const SizedBox(width: 6),
              Text(
                _getLabel(tab),
                style: theme.textTheme.bodySmall?.copyWith(
                  color: theme.colorScheme.onSurface,
                  fontWeight: FontWeight.bold,
                  fontSize: 11,
                ),
              ),
              const SizedBox(width: 4),
            ],
          ],
        ),
      ),
    );
  }

  IconData _getIcon(MainTab tab) {
    switch (tab) {
      case MainTab.home: return Icons.home;
      case MainTab.members: return Icons.format_list_bulleted;
      case MainTab.payments: return Icons.payments;
      case MainTab.plans: return Icons.assignment;
      case MainTab.profile: return Icons.person;
    }
  }

  String _getLabel(MainTab tab) {
    switch (tab) {
      case MainTab.home: return "Home";
      case MainTab.members: return "Members";
      case MainTab.payments: return "Pay";
      case MainTab.plans: return "Plans";
      case MainTab.profile: return "Profile";
    }
  }
}
