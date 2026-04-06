import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:url_launcher/url_launcher.dart';
import '../widgets/profile_image.dart';
import '../../logic/providers/member_details_provider.dart';
import '../../data/models/member_model.dart';

class MemberProfileScreen extends ConsumerWidget {
  final String memberId;

  const MemberProfileScreen({super.key, required this.memberId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final uiState = ref.watch(memberDetailsProvider(memberId));
    final theme = Theme.of(context);

    if (uiState.deleteSuccess) {
      WidgetsBinding.instance.addPostFrameCallback((_) => Navigator.pop(context));
    }

    return Scaffold(
      backgroundColor: theme.colorScheme.surface,
      appBar: AppBar(
        title: Text(uiState.isEditing ? "Edit Member" : "Member Profile", style: const TextStyle(fontWeight: FontWeight.bold)),
        leading: IconButton(
          icon: Icon(uiState.isEditing ? Icons.close : Icons.arrow_back),
          onPressed: () {
            if (uiState.isEditing) {
              ref.read(memberDetailsProvider(memberId).notifier).toggleEdit();
            } else {
              Navigator.pop(context);
            }
          },
        ),
        actions: [
          if (!uiState.isEditing && uiState.member != null)
            IconButton(
              icon: const Icon(Icons.edit),
              onPressed: () => ref.read(memberDetailsProvider(memberId).notifier).toggleEdit(),
            ),
        ],
      ),
      body: uiState.isLoading && uiState.member == null
          ? const Center(child: CircularProgressIndicator())
          : uiState.member == null
              ? Center(child: Text(uiState.errorMessage ?? "Member not found"))
              : SingleChildScrollView(
                  child: Column(
                    children: [
                      const SizedBox(height: 24),
                      _ProfileHeader(member: uiState.member!),
                      const SizedBox(height: 24),
                      _QuickActions(member: uiState.member!, ref: ref),
                      const SizedBox(height: 24),
                      Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 24),
                        child: Column(
                          children: [
                            _MembershipSection(member: uiState.member!),
                            const SizedBox(height: 16),
                            _PersonalInfoSection(member: uiState.member!),
                            const SizedBox(height: 16),
                            _DigitalIDCard(member: uiState.member!),
                            const SizedBox(height: 24),
                            const Divider(),
                            TextButton(
                              onPressed: () => _showDeactivateDialog(context, ref, uiState.member!),
                              child: Text(uiState.member!.status == "ACTIVE" ? "Deactivate Member" : "Reactivate Member"),
                            ),
                            TextButton(
                              onPressed: () => _showDeleteDialog(context, ref, uiState.member!),
                              style: TextButton.styleFrom(foregroundColor: Colors.red),
                              child: const Text("Delete Member Profile"),
                            ),
                            const SizedBox(height: 40),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
    );
  }

  void _showDeactivateDialog(BuildContext context, WidgetRef ref, dynamic member) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(member.status == "ACTIVE" ? "Deactivate Member?" : "Reactivate Member?"),
        content: Text(member.status == "ACTIVE"
            ? "Are you sure you want to deactivate ${member.name}? Access will be restricted."
            : "Reactivating will restore member access."),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text("Cancel")),
          TextButton(
            onPressed: () {
              ref.read(memberDetailsProvider(memberId).notifier).toggleStatus();
              Navigator.pop(context);
            },
            child: Text(member.status == "ACTIVE" ? "Deactivate" : "Reactivate"),
          ),
        ],
      ),
    );
  }

  void _showDeleteDialog(BuildContext context, WidgetRef ref, dynamic member) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Delete Member?"),
        content: Text("Are you sure you want to delete ${member.name} permanently?"),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text("Cancel")),
          TextButton(
            onPressed: () {
              ref.read(memberDetailsProvider(memberId).notifier).deleteMember();
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

class _ProfileHeader extends StatelessWidget {
  final Member member;

  const _ProfileHeader({required this.member});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    
    // Simplified logic for state
    final isActuallyExpired = member.isExpired;
    final pillColor = isActuallyExpired ? Colors.red : (member.status == "ACTIVE" ? Colors.green : Colors.grey);
    final pillText = isActuallyExpired ? "EXPIRED" : (member.status == "ACTIVE" ? "ACTIVE" : "INACTIVE");

    return Column(
      children: [
        ProfileImage(imageUrl: member.photoUrl, name: member.name, size: 100),
        const SizedBox(height: 16),
        Text(member.name, style: theme.textTheme.headlineSmall?.copyWith(fontWeight: FontWeight.bold)),
        Text("Gym Member", style: theme.textTheme.bodyMedium?.copyWith(color: theme.colorScheme.onSurface.withValues(alpha: 0.6))),
        const SizedBox(height: 12),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
          decoration: BoxDecoration(
            color: pillColor.withValues(alpha: 0.1),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Text(
            pillText,
            style: TextStyle(color: pillColor, fontWeight: FontWeight.bold),
          ),
        ),
      ],
    );
  }
}

class _QuickActions extends StatelessWidget {
  final Member member;
  final WidgetRef ref;

  const _QuickActions({required this.member, required this.ref});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24),
      child: Row(
        children: [
          Expanded(
            child: OutlinedButton.icon(
              onPressed: () => _launchUrl("tel:${member.phone}"),
              icon: const Icon(Icons.call, size: 18),
              label: const Text("Call"),
              style: OutlinedButton.styleFrom(
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
            ),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: OutlinedButton.icon(
              onPressed: () {
                final number = member.phone.replaceAll("+", "").replaceAll(" ", "");
                _launchUrl("https://wa.me/$number");
              },
              icon: const Icon(Icons.chat_bubble_outline, size: 18),
              label: const Text("Chat"),
              style: OutlinedButton.styleFrom(
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _launchUrl(String url) async {
    final uri = Uri.parse(url);
    if (await canLaunchUrl(uri)) await launchUrl(uri);
  }
}

class _MembershipSection extends StatelessWidget {
  final Member member;

  const _MembershipSection({required this.member});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20), side: BorderSide(color: theme.colorScheme.onSurface.withValues(alpha: 0.1))),
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text("Membership", style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold, color: theme.colorScheme.primary)),
            const SizedBox(height: 20),
            Row(
              children: [
                _InfoColumn(label: "Join Date", value: member.joiningDateDisplay),
                const Spacer(),
                _InfoColumn(label: "Expiry Date", value: member.expiryDateDisplay),
              ],
            ),
            const SizedBox(height: 20),
            ClipRRect(
              borderRadius: BorderRadius.circular(4),
              child: LinearProgressIndicator(
                value: member.progress,
                backgroundColor: theme.colorScheme.primary.withValues(alpha: 0.1),
                valueColor: AlwaysStoppedAnimation<Color>(theme.colorScheme.primary),
                minHeight: 8,
              ),
            ),
            const SizedBox(height: 8),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text("${(member.progress * 100).toInt()}% Complete", style: theme.textTheme.labelSmall?.copyWith(color: theme.colorScheme.onSurface.withValues(alpha: 0.5))),
                Text(member.isExpired ? "Expired" : "Active", style: theme.textTheme.labelSmall?.copyWith(color: member.isExpired ? Colors.red : Colors.green, fontWeight: FontWeight.bold)),
              ],
            ),
            const SizedBox(height: 20),
            SizedBox(
              width: double.infinity,
              height: 48,
              child: ElevatedButton(
                onPressed: () {
                  // Show Renewal Sheet
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: theme.colorScheme.primary,
                  foregroundColor: Colors.black,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  elevation: 0,
                ),
                child: const Text("RENEW MEMBERSHIP", style: TextStyle(fontWeight: FontWeight.bold)),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _PersonalInfoSection extends StatelessWidget {
  final Member member;

  const _PersonalInfoSection({required this.member});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20), side: BorderSide(color: theme.colorScheme.onSurface.withValues(alpha: 0.1))),
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text("Personal Information", style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold, color: theme.colorScheme.primary)),
            const SizedBox(height: 16),
            _DetailRow(icon: Icons.email_outlined, value: member.email.isEmpty ? "Not provided" : member.email),
            const SizedBox(height: 12),
            _DetailRow(icon: Icons.phone_android_outlined, value: member.phone.isEmpty ? "Not provided" : member.phone),
            const SizedBox(height: 12),
            _DetailRow(icon: Icons.favorite_border, value: member.bloodGroup),
          ],
        ),
      ),
    );
  }
}

class _DetailRow extends StatelessWidget {
  final IconData icon;
  final String value;

  const _DetailRow({required this.icon, required this.value});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Icon(icon, size: 18, color: Theme.of(context).colorScheme.primary.withValues(alpha: 0.7)),
        const SizedBox(width: 12),
        Text(value, style: Theme.of(context).textTheme.bodyMedium),
      ],
    );
  }
}

class _InfoColumn extends StatelessWidget {
  final String label;
  final String value;

  const _InfoColumn({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: theme.textTheme.labelMedium?.copyWith(color: theme.colorScheme.onSurface.withValues(alpha: 0.5))),
        Text(value, style: theme.textTheme.bodyLarge?.copyWith(fontWeight: FontWeight.bold)),
      ],
    );
  }
}

class _DigitalIDCard extends StatelessWidget {
  final Member member;

  const _DigitalIDCard({required this.member});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Container(
      width: double.infinity,
      height: 190,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(24),
        gradient: const LinearGradient(colors: [Color(0xFF1E293B), Color(0xFF0F172A)]),
      ),
      padding: const EdgeInsets.all(20),
      child: Stack(
        children: [
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  ProfileImage(imageUrl: member.photoUrl, name: member.name, size: 48),
                  const SizedBox(width: 12),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(member.name.toUpperCase(), style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                      Text("PREMIUM", style: TextStyle(color: theme.colorScheme.primary, fontSize: 10, fontWeight: FontWeight.bold)),
                    ],
                  ),
                ],
              ),
              const Spacer(),
              Text("DIGITAL PASS", style: TextStyle(color: Colors.white.withValues(alpha: 0.3), fontSize: 10, letterSpacing: 2)),
              Text("ID: ${member.id.substring(0, 8).toUpperCase()}", style: TextStyle(color: Colors.white.withValues(alpha: 0.4), fontSize: 12)),
            ],
          ),
          Align(
            alignment: Alignment.bottomRight,
            child: Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(12)),
              child: QrImageView(
                data: member.id,
                version: QrVersions.auto,
                size: 64.0,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
