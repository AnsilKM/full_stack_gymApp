import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../logic/providers/plans_provider.dart';
import '../../logic/providers/notification_provider.dart';
import '../../data/repositories/plan_repository.dart';
import '../../data/repositories/gym_repository.dart';

class AddPlanScreen extends ConsumerStatefulWidget {
  final MembershipPlan? plan;
  const AddPlanScreen({super.key, this.plan});

  @override
  ConsumerState<AddPlanScreen> createState() => _AddPlanScreenState();
}

class _AddPlanScreenState extends ConsumerState<AddPlanScreen> {
  late TextEditingController _nameController;
  late TextEditingController _priceController;
  late TextEditingController _durationController;
  late TextEditingController _descriptionController;
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _nameController = TextEditingController(text: widget.plan?.name ?? "");
    _priceController = TextEditingController(text: widget.plan?.price.toString() ?? "");
    _durationController = TextEditingController(text: widget.plan?.durationMonths.toString() ?? "1");
    _descriptionController = TextEditingController(text: widget.plan?.description ?? "");
  }

  Future<void> _savePlan() async {
    if (_nameController.text.isEmpty || _priceController.text.isEmpty || _durationController.text.isEmpty) {
      ref.read(notificationProvider.notifier).showError("Please fill all required fields");
      return;
    }

    setState(() => _isLoading = true);
    try {
      final gymRepo = ref.read(gymRepositoryProvider);
      final planRepo = ref.read(planRepositoryProvider);
      final gymsResponse = await gymRepo.getGyms();

      if (!gymsResponse.status || gymsResponse.data == null || gymsResponse.data!.isEmpty) throw Exception("No gym found");
      final gymId = gymsResponse.data!.first.id;

      final res = await planRepo.createPlan(MembershipPlan(
        id: widget.plan?.id ?? "",
        name: _nameController.text,
        price: double.tryParse(_priceController.text) ?? 0.0,
        durationMonths: int.tryParse(_durationController.text) ?? 1,
        description: _descriptionController.text,
        gymId: gymId,
      ));

      if (res.status) {
        ref.read(notificationProvider.notifier).showSuccess(widget.plan == null ? "Plan created successfully" : "Plan updated successfully");
        ref.read(plansProvider.notifier).loadPlans();
        if (mounted) Navigator.pop(context);
      } else {
        ref.read(notificationProvider.notifier).showError(res.message);
      }
    } catch (e) {
      ref.read(notificationProvider.notifier).showError(e.toString());
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.plan == null ? "Create New Plan" : "Edit Plan", style: const TextStyle(fontWeight: FontWeight.bold)),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            TextField(
              controller: _nameController,
              decoration: _inputDecoration("Plan Name", isRequired: true, hint: "e.g. Monthly Intro"),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _priceController,
              decoration: _inputDecoration("Price (₹)", isRequired: true),
              keyboardType: TextInputType.number,
            ),
            const SizedBox(height: 24),
            Text("Plan Duration", style: theme.textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold)),
            const SizedBox(height: 12),
            _DurationSelector(
              currentValue: _durationController.text,
              onSelect: (val) => setState(() => _durationController.text = val),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _durationController,
              decoration: _inputDecoration("Custom Duration (Months)", suffix: "Months"),
              keyboardType: TextInputType.number,
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _descriptionController,
              decoration: _inputDecoration("Features (comma separated)", hint: "Locker, Cardio, Personal Training"),
              maxLines: 3,
            ),
            const SizedBox(height: 40),
            SizedBox(
              width: double.infinity,
              height: 56,
              child: ElevatedButton(
                onPressed: _isLoading ? null : _savePlan,
                style: ElevatedButton.styleFrom(
                  backgroundColor: theme.colorScheme.primary,
                  foregroundColor: Colors.black,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                ),
                child: _isLoading ? const CircularProgressIndicator(color: Colors.black) : Text(widget.plan == null ? "Save & Publish Plan" : "Update Plan", style: const TextStyle(fontWeight: FontWeight.bold)),
              ),
            ),
          ],
        ),
      ),
    );
  }

  InputDecoration _inputDecoration(String label, {bool isRequired = false, String? hint, String? suffix}) {
    return InputDecoration(
      labelText: isRequired ? "$label *" : label,
      hintText: hint,
      suffixText: suffix,
      border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
    );
  }
}

class _DurationSelector extends StatelessWidget {
  final String currentValue;
  final Function(String) onSelect;

  const _DurationSelector({required this.currentValue, required this.onSelect});

  @override
  Widget build(BuildContext context) {
    final durations = [
      {"val": "1", "label": "1 Month"},
      {"val": "3", "label": "3 Months"},
      {"val": "6", "label": "6 Months"},
      {"val": "12", "label": "1 Year"},
    ];

    return Row(
      children: durations.map((d) {
        final isSelected = currentValue == d["val"];
        return Expanded(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 4),
            child: FilterChip(
              selected: isSelected,
              label: Text(d["label"]!, style: const TextStyle(fontSize: 10), maxLines: 1),
              onSelected: (_) => onSelect(d["val"]!),
              selectedColor: Theme.of(context).colorScheme.primary,
              checkmarkColor: Colors.black,
              labelStyle: TextStyle(color: isSelected ? Colors.black : null),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
            ),
          ),
        );
      }).toList(),
    );
  }
}
