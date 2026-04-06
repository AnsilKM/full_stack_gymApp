import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../logic/providers/plans_provider.dart';
import '../../logic/providers/notification_provider.dart';
import '../../data/repositories/member_repository.dart';
import '../../data/repositories/gym_repository.dart';
import 'dart:typed_data';
import 'package:image_picker/image_picker.dart';

class AddMemberScreen extends ConsumerStatefulWidget {
  const AddMemberScreen({super.key});

  @override
  ConsumerState<AddMemberScreen> createState() => _AddMemberScreenState();
}

class _AddMemberScreenState extends ConsumerState<AddMemberScreen> {
  final _nameController = TextEditingController();
  final _emailController = TextEditingController();
  final _phoneController = TextEditingController();
  String? _selectedBloodGroup;
  String? _selectedPlanId;
  Uint8List? _pickedImage;
  bool _isLoading = false;

  final _bloodGroups = ["A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"];

  Future<void> _pickImage() async {
    final picker = ImagePicker();
    final image = await picker.pickImage(source: ImageSource.gallery, imageQuality: 70);
    if (image != null) {
      final bytes = await image.readAsBytes();
      setState(() => _pickedImage = bytes);
    }
  }

  Future<void> _saveMember() async {
    if (_nameController.text.isEmpty || _phoneController.text.isEmpty || _selectedPlanId == null) {
      ref.read(notificationProvider.notifier).showError("Please fill all required fields");
      return;
    }

    setState(() => _isLoading = true);
    try {
      final gymRepo = ref.read(gymRepositoryProvider);
      final memberRepo = ref.read(memberRepositoryProvider);
      final gymsResponse = await gymRepo.getGyms();
      
      if (!gymsResponse.status || gymsResponse.data == null || gymsResponse.data!.isEmpty) throw Exception("No gym found");
      final gymId = gymsResponse.data!.first.id;

      String photoUrl = "";
      if (_pickedImage != null) {
        final uploadRes = await memberRepo.uploadImage(_pickedImage!);
        if (uploadRes.status) photoUrl = uploadRes.data ?? "";
      }

      final res = await memberRepo.createMember({
        "name": _nameController.text,
        "email": _emailController.text,
        "phone": _phoneController.text,
        "bloodGroup": _selectedBloodGroup ?? "Unknown",
        "photoUrl": photoUrl,
        "planId": _selectedPlanId,
        "gymId": gymId,
        "joinDate": DateTime.now().toIso8601String(),
      });

      if (res.status) {
        ref.read(notificationProvider.notifier).showSuccess("Member registered successfully");
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
    final plansState = ref.watch(plansProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text("Add Member", style: TextStyle(fontWeight: FontWeight.bold)),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Center(
              child: GestureDetector(
                onTap: _pickImage,
                child: Stack(
                  alignment: Alignment.bottomRight,
                  children: [
                    CircleAvatar(
                      radius: 60,
                      backgroundColor: theme.colorScheme.primary.withValues(alpha: 0.1),
                      backgroundImage: _pickedImage != null ? MemoryImage(_pickedImage!) : null,
                      child: _pickedImage == null ? Icon(Icons.person, size: 72, color: theme.colorScheme.primary) : null,
                    ),
                    CircleAvatar(
                      radius: 18,
                      backgroundColor: theme.colorScheme.primary,
                      child: const Icon(Icons.edit, size: 18, color: Colors.white),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 32),
            _SectionHeader(title: "Personal Information", isRequired: true),
            TextField(
              controller: _nameController,
              decoration: _inputDecoration("Full Name", isRequired: true),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _emailController,
              decoration: _inputDecoration("Email Address"),
              keyboardType: TextInputType.emailAddress,
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _phoneController,
              decoration: _inputDecoration("Phone Number", isRequired: true),
              keyboardType: TextInputType.phone,
            ),
            const SizedBox(height: 16),
            DropdownButtonFormField<String>(
              value: _selectedBloodGroup,
              decoration: _inputDecoration("Blood Group"),
              items: _bloodGroups.map((bg) => DropdownMenuItem(value: bg, child: Text(bg))).toList(),
              onChanged: (val) => setState(() => _selectedBloodGroup = val),
            ),
            const SizedBox(height: 32),
            _SectionHeader(title: "Membership Plan", isRequired: true),
            DropdownButtonFormField<String>(
              value: _selectedPlanId,
              decoration: _inputDecoration("Select Plan", isRequired: true),
              items: plansState.plans.map((p) => DropdownMenuItem(value: p.id, child: Text("${p.name} - ₹${p.price.toInt()}"))).toList(),
              onChanged: (val) => setState(() => _selectedPlanId = val),
            ),
            const SizedBox(height: 40),
            SizedBox(
              width: double.infinity,
              height: 56,
              child: ElevatedButton(
                onPressed: _isLoading ? null : _saveMember,
                style: ElevatedButton.styleFrom(
                  backgroundColor: theme.colorScheme.primary,
                  foregroundColor: Colors.black,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                ),
                child: _isLoading ? const CircularProgressIndicator(color: Colors.black) : const Text("Register Member", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
              ),
            ),
          ],
        ),
      ),
    );
  }

  InputDecoration _inputDecoration(String label, {bool isRequired = false}) {
    return InputDecoration(
      labelText: isRequired ? "$label *" : label,
      border: OutlineInputBorder(borderRadius: BorderRadius.circular(16)),
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
    );
  }
}

class _SectionHeader extends StatelessWidget {
  final String title;
  final bool isRequired;
  const _SectionHeader({required this.title, this.isRequired = false});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: Row(
        children: [
          Text(title, style: TextStyle(fontWeight: FontWeight.bold, color: Theme.of(context).colorScheme.primary)),
          if (isRequired) const Text(" *", style: TextStyle(color: Colors.red)),
        ],
      ),
    );
  }
}
