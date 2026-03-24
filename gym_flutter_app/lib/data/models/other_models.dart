class Gym {
  final String id;
  final String name;
  Gym({required this.id, required this.name});
  factory Gym.fromJson(Map<String, dynamic> json) => Gym(id: json['id'], name: json['name']);
}

class Plan {
  final String id;
  final String name;
  final double price;
  Plan({required this.id, required this.name, required this.price});
  factory Plan.fromJson(Map<String, dynamic> json) => Plan(id: json['id'], name: json['name'], price: (json['price'] ?? 0).toDouble());
}

class DashboardStats {
  final int activeMembers;
  final double totalRevenue;
  final double monthlyGrowth;
  final int todayCheckins;

  DashboardStats({
    required this.activeMembers,
    required this.totalRevenue,
    required this.monthlyGrowth,
    required this.todayCheckins,
  });

  factory DashboardStats.fromJson(Map<String, dynamic> json) {
    return DashboardStats(
      activeMembers: json['activeMembers'] ?? 0,
      totalRevenue: (json['totalRevenue'] ?? 0).toDouble(),
      monthlyGrowth: (json['monthlyGrowth'] ?? 0).toDouble(),
      todayCheckins: json['todayCheckins'] ?? 0,
    );
  }
}
