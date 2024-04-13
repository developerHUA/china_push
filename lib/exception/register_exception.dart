class RegisterException implements Exception {
  const RegisterException({
    this.errorCode,
    this.error,
  });

  final String? errorCode;

  final String? error;

  @override
  String toString() {
    return "RegisterException: code: $errorCode message:$error}";
  }
}
