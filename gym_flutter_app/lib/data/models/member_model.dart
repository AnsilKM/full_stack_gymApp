import 'package:flutter/foundation.dart';
import 'package:intl/intl.dart';

@immutable
class Member {
  final String id;
  final String name;
  final String email;
  final String phone;
  final String status;
  final String bloodGroup;
  final String photoUrl;
  final DateTime joinDate;
  final DateTime expiryDate;

  const Member({
    required this.id,
    required this.name,
    required this.email,
    required this.phone,
    required this.status,
    required this.bloodGroup,
    required this.photoUrl,
    required this.joinDate,
    required this.expiryDate,
  });

  bool get isExpired => DateTime.now().isAfter(expiryDate);
  String get joiningDateDisplay => DateFormat('dd MMM yyyy').format(joinDate);
  String get expiryDateDisplay => DateFormat('dd MMM yyyy').format(expiryDate);

  factory Member.fromJson(Map<String, dynamic> json) {
    return Member(
      id: json['id'],
      name: json['name'],
      email: json['email'] ?? '',
      phone: json['phone'] ?? '',
      status: json['status'] ?? 'EXPIRED',
      bloodGroup: json['bloodGroup'] ?? 'Unknown',
      photoUrl: json['photoUrl'] ?? '',
      joinDate: json['joinDate'] != null ? DateTime.parse(json['joinDate']) : DateTime.now(),
      expiryDate: json['expiryDate'] != null ? DateTime.parse(json['expiryDate']) : DateTime.now(),
    );
  }
}
