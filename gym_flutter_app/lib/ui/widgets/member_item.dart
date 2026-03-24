import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';
import 'profile_image.dart';

class MemberItem extends StatelessWidget {
  final String name;
  final String status;
  final bool isActuallyExpired;
  final String joinDate;
  final String expiryDate;
  final String? phone;
  final String? imageUrl;
  final VoidCallback onClick;

  const MemberItem({
    super.key,
    required this.name,
    required this.status,
    required this.isActuallyExpired,
    required this.joinDate,
    required this.expiryDate,
    this.phone,
    this.imageUrl,
    required this.onClick,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final displayStatus = isActuallyExpired ? "EXPIRED" : status;

    return Card(
      margin: EdgeInsets.zero,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      color: theme.colorScheme.surface,
      child: InkWell(
        onTap: onClick,
        borderRadius: BorderRadius.circular(20),
        child: Padding(
          padding: const EdgeInsets.all(12.0),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              ProfileImage(
                imageUrl: imageUrl,
                name: name,
                size: 44,
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          name,
                          style: theme.textTheme.bodyLarge?.copyWith(
                            fontWeight: FontWeight.bold,
                            fontSize: 14,
                            color: theme.colorScheme.onSurface,
                          ),
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                          decoration: BoxDecoration(
                            color: isActuallyExpired
                                ? Colors.red.withValues(alpha: 0.1)
                                : (status == "ACTIVE"
                                    ? theme.colorScheme.primary.withValues(alpha: 0.15)
                                    : Colors.red.withValues(alpha: 0.1)),
                            borderRadius: BorderRadius.circular(10),
                          ),
                          child: Text(
                            displayStatus,
                            style: TextStyle(
                              color: isActuallyExpired || status != "ACTIVE"
                                  ? Colors.red
                                  : theme.colorScheme.primary,
                              fontSize: 9,
                              fontWeight: FontWeight.w900,
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Row(
                      children: [
                        Icon(
                          Icons.calendar_today,
                          size: 10,
                          color: theme.colorScheme.onSurface.withValues(alpha: 0.5),
                        ),
                        Text(
                          " Joined: $joinDate",
                          style: TextStyle(
                            fontSize: 10,
                            color: theme.colorScheme.onSurface.withValues(alpha: 0.5),
                          ),
                        ),
                        const SizedBox(width: 16),
                        _ExpiryInfo(
                          isActuallyExpired: isActuallyExpired,
                          expiryDate: expiryDate,
                          primaryColor: theme.colorScheme.primary,
                        ),
                      ],
                    ),
                    if (phone != null && phone!.isNotEmpty) ...[
                      const SizedBox(height: 12),
                      Row(
                        children: [
                          _ActionButton(
                            label: "WhatsApp",
                            icon: Icons.chat_rounded,
                            color: const Color(0xFF059669),
                            onPressed: () {
                              final number = phone!.replaceAll("+", "").replaceAll(" ", "");
                              _launchUrl("https://wa.me/$number");
                            },
                          ),
                          const SizedBox(width: 8),
                          _ActionButton(
                            label: "Call",
                            icon: Icons.call,
                            color: const Color(0xFF4F46E5),
                            onPressed: () {
                              _launchUrl("tel:$phone");
                            },
                          ),
                        ],
                      ),
                    ],
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _launchUrl(String url) async {
    final uri = Uri.parse(url);
    if (await canLaunchUrl(uri)) {
      await launchUrl(uri);
    }
  }
}

class _ExpiryInfo extends StatelessWidget {
  final bool isActuallyExpired;
  final String expiryDate;
  final Color primaryColor;

  const _ExpiryInfo({
    required this.isActuallyExpired,
    required this.expiryDate,
    required this.primaryColor,
  });

  @override
  Widget build(BuildContext context) {
    final expiryColor = isActuallyExpired ? Colors.red : primaryColor.withValues(alpha: 0.7);

    return Row(
      children: [
        Icon(
          Icons.event,
          size: 10,
          color: expiryColor,
        ),
        Text(
          " Expires: $expiryDate",
          style: TextStyle(
            fontSize: 10,
            color: expiryColor,
            fontWeight: FontWeight.bold,
          ),
        ),
      ],
    );
  }
}

class _ActionButton extends StatelessWidget {
  final String label;
  final IconData icon;
  final Color color;
  final VoidCallback onPressed;

  const _ActionButton({
    required this.label,
    required this.icon,
    required this.color,
    required this.onPressed,
  });

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 30,
      child: TextButton.icon(
        onPressed: onPressed,
        icon: Icon(icon, size: 14),
        label: Text(
          label,
          style: const TextStyle(fontSize: 10, fontWeight: FontWeight.bold),
        ),
        style: TextButton.styleFrom(
          backgroundColor: color.withValues(alpha: 0.12),
          foregroundColor: color,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
          padding: const EdgeInsets.symmetric(horizontal: 12),
        ),
      ),
    );
  }
}
