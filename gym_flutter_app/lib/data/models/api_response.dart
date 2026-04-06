class ApiResponse<T> {
  final bool status;
  final String message;
  final T? data;

  ApiResponse({required this.status, required this.message, this.data});

  factory ApiResponse.fromJson(
    Map<String, dynamic> json,
    T Function(dynamic json) fromJsonData,
  ) {
    return ApiResponse(
      status: json['status'] ?? false,
      message: json['message'] ?? '',
      data: json['data'] != null ? fromJsonData(json['data']) : null,
    );
  }
}
