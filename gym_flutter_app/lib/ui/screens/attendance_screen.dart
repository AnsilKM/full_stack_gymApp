import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../logic/providers/attendance_provider.dart';
import '../widgets/profile_image.dart';

class AttendanceScreen extends ConsumerWidget {
  const AttendanceScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final uiState = ref.watch(attendanceProvider);
    final theme = Theme.of(context);

    return Scaffold(
      backgroundColor: theme.colorScheme.surface,
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Column(
          children: [
            const SizedBox(height: 60),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  children: [
                    IconButton(
                      icon: const Icon(Icons.arrow_back_ios, size: 20),
                      onPressed: () => Navigator.pop(context),
                    ),
                    Text(
                      "Attendance",
                      style: theme.textTheme.headlineSmall?.copyWith(fontWeight: FontWeight.w900, color: theme.colorScheme.onSurface),
                    ),
                  ],
                ),
                GestureDetector(
                  onTap: () => ref.read(attendanceProvider.notifier).loadAttendance(),
                  child: Container(
                    width: 40,
                    height: 40,
                    decoration: BoxDecoration(color: theme.colorScheme.surface, shape: BoxShape.circle),
                    child: Icon(Icons.refresh, size: 20, color: theme.colorScheme.onSurface),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 20),
            Expanded(
              child: uiState.isLoading
                  ? const Center(child: CircularProgressIndicator())
                  : uiState.attendanceList.isEmpty
                      ? Center(child: Text("No check-ins today", style: TextStyle(color: theme.colorScheme.onBackground.withValues(alpha: 0.5), fontSize: 13)))
                      : ListView.separated(
                          itemCount: uiState.attendanceList.length,
                          separatorBuilder: (context, index) => const SizedBox(height: 10),
                          itemBuilder: (context, index) {
                            final record = uiState.attendanceList[index];
                            return _AttendanceItem(record: record);
                          },
                        ),
            ),
          ],
        ),
      ),
    );
  }
}

class _AttendanceItem extends StatelessWidget {
  final Attendance record;
  const _AttendanceItem({required this.record});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final time = record.checkInTime.split('T').last.substring(0, 5);

    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      color: theme.colorScheme.surface,
      child: Padding(
        padding: const EdgeInsets.all(12.0),
        child: Row(
          children: [
            ProfileImage(imageUrl: record.member?.photoUrl, name: record.member?.name ?? "M", size: 40),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(record.member?.name ?? "Member", style: theme.textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold)),
                  Text("Checked in at $time", style: theme.textTheme.bodySmall?.copyWith(color: theme.colorScheme.onSurface.withValues(alpha: 0.6))),
                ],
              ),
            ),
            Container(
              width: 32,
              height: 32,
              decoration: BoxDecoration(color: theme.colorScheme.primary.withValues(alpha: 0.15), shape: BoxShape.circle),
              child: Icon(Icons.check_circle, size: 16, color: theme.colorScheme.primary),
            ),
          ],
        ),
      ),
    );
  }
}
