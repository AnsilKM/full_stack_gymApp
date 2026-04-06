import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../logic/providers/dashboard_provider.dart';

class PaymentsScreen extends ConsumerWidget {
  const PaymentsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final uiState = ref.watch(dashboardProvider);
    final theme = Theme.of(context);

    return Scaffold(
      backgroundColor: theme.colorScheme.surface,
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 60),
            Text(
              "Revenue & Payments",
              style: theme.textTheme.headlineSmall?.copyWith(
                fontWeight: FontWeight.w900,
                color: theme.colorScheme.onSurface,
              ),
            ),
            const SizedBox(height: 24),
            Row(
              children: [
                Expanded(
                  child: _SummaryCard(
                    title: "Total Revenue",
                    amount: uiState.totalRevenue,
                    icon: Icons.payments,
                    color: const Color(0xFF10B981),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _SummaryCard(
                    title: "Pending Dues",
                    amount: "₹12,400",
                    icon: Icons.pending_actions,
                    color: const Color(0xFFF59E0B),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 24),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text("Recent Transactions", style: theme.textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold)),
                TextButton(onPressed: () {}, child: Text("View All", style: TextStyle(fontSize: 12, color: theme.colorScheme.primary))),
              ],
            ),
            const SizedBox(height: 8),
            const _TransactionItem(name: "Muhammed Ansib", plan: "Monthly Plan", amount: "₹2,500", date: "Mar 18, 02:30 PM", isSuccess: true),
            const _TransactionItem(name: "Rahul Sharma", plan: "Personal Training", amount: "₹5,000", date: "Mar 17, 11:15 AM", isSuccess: true),
            const _TransactionItem(name: "John Doe", plan: "Quarterly Plus", amount: "₹6,500", date: "Mar 17, 09:45 AM", isSuccess: true),
            const _TransactionItem(name: "Adarsh S", plan: "Basic Plan", amount: "₹1,200", date: "Mar 16, 04:20 PM", isSuccess: false),
            const _TransactionItem(name: "Vignesh K", plan: "Yearly Pack", amount: "₹12,000", date: "Mar 15, 10:00 AM", isSuccess: true),
            const SizedBox(height: 100),
          ],
        ),
      ),
    );
  }
}

class _SummaryCard extends StatelessWidget {
  final String title;
  final String amount;
  final IconData icon;
  final Color color;

  const _SummaryCard({required this.title, required this.amount, required this.icon, required this.color});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24), side: BorderSide(color: theme.colorScheme.onSurface.withValues(alpha: 0.1))),
      color: theme.colorScheme.surface,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(icon, color: color, size: 24),
            const SizedBox(height: 12),
            Text(title, style: theme.textTheme.labelSmall?.copyWith(color: theme.colorScheme.onSurface.withValues(alpha: 0.6))),
            const SizedBox(height: 4),
            Text(amount, style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.w900)),
          ],
        ),
      ),
    );
  }
}

class _TransactionItem extends StatelessWidget {
  final String name;
  final String plan;
  final String amount;
  final String date;
  final bool isSuccess;

  const _TransactionItem({required this.name, required this.plan, required this.amount, required this.date, required this.isSuccess});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Card(
      elevation: 0,
      margin: const EdgeInsets.symmetric(vertical: 4),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      color: theme.colorScheme.surface,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Row(
          children: [
            Container(
              width: 40,
              height: 40,
              decoration: BoxDecoration(
                color: (isSuccess ? const Color(0xFF10B981) : Colors.red).withValues(alpha: 0.1),
                shape: BoxShape.circle,
              ),
              child: Icon(
                isSuccess ? Icons.arrow_upward : Icons.history,
                color: isSuccess ? const Color(0xFF10B981) : Colors.red,
                size: 20,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(name, style: theme.textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold)),
                  Text(plan, style: theme.textTheme.bodySmall?.copyWith(color: theme.colorScheme.onSurface.withValues(alpha: 0.6))),
                ],
              ),
            ),
            Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Text(amount, style: theme.textTheme.titleSmall?.copyWith(fontWeight: FontWeight.w900, color: isSuccess ? const Color(0xFF10B981) : Colors.red)),
                Text(date, style: theme.textTheme.labelSmall?.copyWith(color: theme.colorScheme.onSurface.withValues(alpha: 0.5))),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
