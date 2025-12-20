import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'auth_service.dart';

class AdminService {
  final Dio _dio = Dio(BaseOptions(
    baseUrl: kIsWeb ? 'http://localhost:8080/api' : 'http://10.0.2.2:8080/api',
  ));
  final _authService = AuthService();

  Future<Options> _getAuthOptions() async {
    final token = await _authService.getToken();
    return Options(headers: {
      'Authorization': 'Bearer $token',
      'Content-Type': 'application/json',
    });
  }

  // --- ORGANISATIONS ---

  Future<List<dynamic>> getAllOrganisations() async {
    try {
      final response = await _dio.get('/organisations', options: await _getAuthOptions());
      return response.data;
    } catch (e) {
      if (kDebugMode) print('Get Orgs Error: $e');
      return [];
    }
  }

  Future<bool> createOrganisation(String name, String type) async {
    try {
      await _dio.post(
        '/organisations',
        data: {'nom': name, 'typeOrganisation': type, 'adresse': 'Adresse par défaut'},
        options: await _getAuthOptions(),
      );
      return true;
    } catch (e) {
      if (kDebugMode) print('Create Org Error: $e');
      return false;
    }
  }

  // --- USERS ---

  Future<List<dynamic>> getAllUsers() async {
    try {
      final response = await _dio.get('/users', options: await _getAuthOptions());
      return response.data;
    } catch (e) {
      if (kDebugMode) print('Get Users Error: $e');
      return [];
    }
  }

  Future<bool> createUser({
    required String username,
    required String password,
    required String nomComplet,
    required String email,
    required String role,
    required int orgId,
  }) async {
    try {
      await _dio.post(
        '/auth/register', // Using register endpoint as it does the job
        data: {
          'username': username,
          'password': password,
          'nomComplet': nomComplet,
          'email': email,
          'role': role,
          'organisationId': orgId
        },
        options: await _getAuthOptions(),
      );
      return true;
    } catch (e) {
      if (kDebugMode) print('Create User Error: $e');
      return false;
    }
  }
}
