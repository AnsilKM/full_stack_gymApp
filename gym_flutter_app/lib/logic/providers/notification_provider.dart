import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'dart:ui';

enum NotificationType { error, success, info }

class AppNotification {
  final String message;
  final NotificationType type;
  final DateTime timestamp;

  AppNotification({
    required this.message,
    required this.type,
    required this.timestamp,
  });
}

class NotificationNotifier extends StateNotifier<AppNotification?> {
  NotificationNotifier() : super(null);

  void show(String message, {NotificationType type = NotificationType.info}) {
    state = AppNotification(
      message: message,
      type: type,
      timestamp: DateTime.now(),
    );
  }

  void showError(String message) => show(message, type: NotificationType.error);
  void showSuccess(String message) => show(message, type: NotificationType.success);
  void showInfo(String message) => show(message, type: NotificationType.info);

  void clear() {
    state = null;
  }
}

final notificationProvider = StateNotifierProvider<NotificationNotifier, AppNotification?>((ref) {
  return NotificationNotifier();
});

class GlobalNotificationListener extends ConsumerWidget {
  final Widget child;
  const GlobalNotificationListener({super.key, required this.child});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    ref.listen(notificationProvider, (previous, next) {
      if (next != null) {
        _showNotification(context, next);
      }
    });
    return child;
  }

  void _showNotification(BuildContext context, AppNotification notification) {
    final color = switch (notification.type) {
      NotificationType.error => Colors.redAccent.withValues(alpha: 0.6),
      NotificationType.success => Colors.greenAccent.withValues(alpha: 0.6),
      NotificationType.info => Colors.blueAccent.withValues(alpha: 0.6),
    };

    final icon = switch (notification.type) {
      NotificationType.error => Icons.error_outline,
      NotificationType.success => Icons.check_circle_outline,
      NotificationType.info => Icons.info_outline,
    };

    ScaffoldMessenger.of(context).clearSnackBars();
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        behavior: SnackBarBehavior.floating,
        duration: const Duration(seconds: 4),
        content: ClipRRect(
          borderRadius: BorderRadius.circular(20),
          child: BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 10, sigmaY: 10),
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              decoration: BoxDecoration(
                color: color.withValues(alpha: 0.2),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(
                  color: Colors.white.withValues(alpha: 0.2),
                  width: 1.5,
                ),
              ),
              child: Row(
                children: [
                  Icon(icon, color: Colors.white, size: 24),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Text(
                      notification.message,
                      style: const TextStyle(
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                        fontSize: 14,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
