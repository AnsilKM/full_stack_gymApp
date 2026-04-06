import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../logic/providers/plans_provider.dart';
import '../../data/repositories/plan_repository.dart';
import 'add_plan_screen.dart';

class PlansScreen extends ConsumerWidget {
  const PlansScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final uiState = ref.watch(plansProvider);
    final theme = Theme.of(context);

    return Scaffold(
      backgroundColor: theme.colorScheme.surface,
      body: RefreshIndicator(
        onRefresh: () => ref.read(plansProvider.notifier).loadPlans(),
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(height: 60),
                Text(
                  "Membership Plans",
                  style: theme.textTheme.headlineSmall?.copyWith(
                    fontWeight: FontWeight.w900,
                    color: theme.colorScheme.onSurface,
                  ),
                ),
                const SizedBox(height: 24),
                if (uiState.isLoading && uiState.plans.isEmpty)
                  const Center(child: CircularProgressIndicator())
                else if (uiState.plans.isEmpty)
                  _EmptyPlansPlaceholder()
                else
                  ...uiState.plans.map((plan) => _PlanCard(plan: plan, ref: ref)),
                const SizedBox(height: 100),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _PlanCard extends StatelessWidget {
  final MembershipPlan plan;
  final WidgetRef ref;

  const _PlanCard({required this.plan, required this.ref});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final features = plan.description?.split(',') ?? ['Gym Access'];

    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: BorderSide(color: theme.colorScheme.onSurface.withValues(alpha: 0.1)),
      ),
      elevation: 0,
      color: theme.colorScheme.surface,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(plan.name, style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold)),
                    Text("${plan.durationMonths} Months", style: theme.textTheme.bodySmall?.copyWith(color: theme.colorScheme.onSurface.withValues(alpha: 0.6))),
                  ],
                ),
                Text("₹${plan.price.toInt()}", style: theme.textTheme.titleLarge?.copyWith(fontWeight: FontWeight.w900, color: theme.colorScheme.primary)),
              ],
            ),
            const SizedBox(height: 12),
            ...features.map((feature) => _FeatureRow(feature: feature.trim())),
            const SizedBox(height: 16),
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                TextButton.icon(
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (context) => AddPlanScreen(plan: plan)),
                    );
                  },
                  icon: const Icon(Icons.edit, size: 16),
                  label: const Text("Edit", style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                ),
                IconButton(
                  onPressed: () => _confirmDelete(context, ref),
                  icon: Icon(Icons.delete, size: 18, color: theme.colorScheme.error),
                  style: IconButton.styleFrom(
                    backgroundColor: theme.colorScheme.error.withValues(alpha: 0.1),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  void _confirmDelete(BuildContext context, WidgetRef ref) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Delete Plan?"),
        content: Text("Are you sure you want to delete the '${plan.name}' plan? This may affect members currently on this plan."),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text("Cancel")),
          TextButton(
            onPressed: () {
              ref.read(plansProvider.notifier).deletePlan(plan.id);
              Navigator.pop(context);
            },
            style: TextButton.styleFrom(foregroundColor: Colors.red),
            child: const Text("Delete"),
          ),
        ],
      ),
    );
  }
}

class _FeatureRow extends StatelessWidget {
  final String feature;
  const _FeatureRow({required this.feature});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        children: [
          Icon(Icons.check_circle, size: 14, color: theme.colorScheme.primary.withValues(alpha: 0.6)),
          const SizedBox(width: 8),
          Text(feature, style: theme.textTheme.bodySmall?.copyWith(color: theme.colorScheme.onSurface.withValues(alpha: 0.8))),
        ],
      ),
    );
  }
}

class _EmptyPlansPlaceholder extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      height: 200,
      alignment: Alignment.center,
      child: Text("No plans added yet", style: TextStyle(color: Theme.of(context).colorScheme.onSurface.withValues(alpha: 0.5))),
    );
  }
}
