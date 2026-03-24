import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../widgets/member_item.dart';
import '../../logic/providers/member_provider.dart';
import 'member_profile_screen.dart';

class MemberListScreen extends ConsumerWidget {
  const MemberListScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final uiState = ref.watch(memberListProvider);
    final theme = Theme.of(context);

    return Scaffold(
      backgroundColor: theme.colorScheme.surface,
      body: LayoutBuilder(
        builder: (context, constraints) {
          final isTablet = constraints.maxWidth > 600;

          return Column(
            children: [
              const SizedBox(height: 60),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: Row(
                  children: [
                    IconButton(
                      icon: const Icon(Icons.arrow_back_ios, size: 20),
                      onPressed: () => Navigator.pop(context),
                      color: theme.colorScheme.onSurface,
                    ),
                    Text(
                      "Members",
                      style: theme.textTheme.headlineSmall?.copyWith(
                        fontWeight: FontWeight.w900,
                        color: theme.colorScheme.onSurface,
                        fontSize: isTablet ? 24 : 18,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 16),
              _SearchBar(ref: ref, uiState: uiState),
              const SizedBox(height: 16),
              _FilterChips(ref: ref, uiState: uiState),
              const SizedBox(height: 16),
              Expanded(
                child: RefreshIndicator(
                  onRefresh: () => ref.read(memberListProvider.notifier).refresh(),
                  child: uiState.isLoading && uiState.members.isEmpty
                      ? const Center(child: CircularProgressIndicator())
                      : _MembersList(uiState: uiState, isTablet: isTablet),
                ),
              ),
            ],
          );
        },
      ),
    );
  }
}

class _SearchBar extends StatelessWidget {
  final WidgetRef ref;
  final MemberListUIState uiState;

  const _SearchBar({required this.ref, required this.uiState});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: TextField(
        onChanged: (value) => ref.read(memberListProvider.notifier).onSearchQueryChange(value),
        style: const TextStyle(fontSize: 13),
        decoration: InputDecoration(
          hintText: "Search...",
          hintStyle: TextStyle(color: theme.colorScheme.onSurface.withValues(alpha: 0.5)),
          prefixIcon: Icon(Icons.search, size: 18, color: theme.colorScheme.onSurface.withValues(alpha: 0.5)),
          filled: true,
          fillColor: theme.colorScheme.surface,
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(16),
            borderSide: BorderSide.none,
          ),
          contentPadding: const EdgeInsets.symmetric(vertical: 12),
        ),
      ),
    );
  }
}

class _FilterChips extends StatelessWidget {
  final WidgetRef ref;
  final MemberListUIState uiState;

  const _FilterChips({required this.ref, required this.uiState});

  @override
  Widget build(BuildContext context) {
    final filters = ["ALL", "ACTIVE", "INACTIVE", "EXPIRED"];
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Row(
        children: filters.map((filter) {
          final isSelected = uiState.statusFilter == filter;
          return Expanded(
            child: GestureDetector(
              onTap: () => ref.read(memberListProvider.notifier).onStatusFilterChange(filter),
              child: Container(
                margin: const EdgeInsets.symmetric(horizontal: 4),
                height: 36,
                decoration: BoxDecoration(
                  color: isSelected ? Theme.of(context).colorScheme.primary : Theme.of(context).colorScheme.surface,
                  borderRadius: BorderRadius.circular(10),
                  border: isSelected ? null : Border.all(color: Theme.of(context).colorScheme.onSurface.withValues(alpha: 0.1)),
                ),
                alignment: Alignment.center,
                child: Text(
                  filter[0].toUpperCase() + filter.substring(1).toLowerCase(),
                  style: TextStyle(
                    color: isSelected ? Colors.black : Theme.of(context).colorScheme.onSurface,
                    fontSize: 11,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}

class _MembersList extends StatelessWidget {
  final MemberListUIState uiState;
  final bool isTablet;

  const _MembersList({required this.uiState, required this.isTablet});

  @override
  Widget build(BuildContext context) {
    if (uiState.filteredMembers.isEmpty) {
      return Center(
        child: Text(
          "No members matching filter",
          style: TextStyle(color: Theme.of(context).colorScheme.onSurface.withValues(alpha: 0.6), fontSize: 13),
        ),
      );
    }

    if (isTablet) {
      return GridView.builder(
        padding: const EdgeInsets.fromLTRB(16, 0, 16, 24),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          crossAxisSpacing: 16,
          mainAxisSpacing: 16,
          mainAxisExtent: 140, // Height of MemberItem roughly
        ),
        itemCount: uiState.filteredMembers.length,
        itemBuilder: (context, index) {
          final member = uiState.filteredMembers[index];
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
      );
    }

    return ListView.separated(
      padding: const EdgeInsets.fromLTRB(16, 0, 16, 24),
      itemCount: uiState.filteredMembers.length,
      separatorBuilder: (context, index) => const SizedBox(height: 10),
      itemBuilder: (context, index) {
        final member = uiState.filteredMembers[index];
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
    );
  }
}
