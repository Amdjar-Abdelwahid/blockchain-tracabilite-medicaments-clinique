import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:flutter/foundation.dart';

class AuthService {
  final Dio _dio = Dio(BaseOptions(
    baseUrl: kIsWeb ? 'http://localhost:8080/api' : 'http://10.0.2.2:8080/api',
  ));
  final _storage = const FlutterSecureStorage();

  Future<String?> login(String username, String password) async {
    try {
      if (kDebugMode) {
        print('Attempting login to ${_dio.options.baseUrl}/auth/login with $username');
      }
      final response = await _dio.post('/auth/login', data: {
        'username': username,
        'password': password
      });
      final token = response.data['token'];
      final role = response.data['role'];
      final usernameRes = response.data['username'];
      final orgId = response.data['organisationId'];

      await _storage.write(key: 'jwt_token', value: token);
      await _storage.write(key: 'username', value: usernameRes);
      if (role != null) await _storage.write(key: 'role', value: role);
      if (orgId != null) await _storage.write(key: 'orgId', value: orgId.toString());
      
      return token;
    } catch (e) {
      if (kDebugMode) {
        print('Login Error: $e');
      }
      return null;
    }
  }

  Future<String?> getToken() async {
    return await _storage.read(key: 'jwt_token');
  }

  Future<String?> getUsername() async {
    return await _storage.read(key: 'username');
  }

  Future<String?> getRole() async {
    return await _storage.read(key: 'role');
  }

  Future<int?> getOrgId() async {
    String? idStr = await _storage.read(key: 'orgId');
    if (idStr != null) return int.tryParse(idStr);
    return 2; // Default fallback helpful for dev/mock but ideally should return null if not logged in
  }
}
