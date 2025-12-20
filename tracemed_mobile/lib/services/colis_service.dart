import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'auth_service.dart';

class ColisService {
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

  // Marquer un colis comme reçu (déjà fait via TransfertService souvent, mais endpoint spécifique existe)
  Future<bool> receptionColis(String identifiant) async {
    try {
      final username = await _authService.getUsername();
      await _dio.post(
        '/colis/reception',
        queryParameters: {'identifiant': identifiant, 'username': username},
        options: await _getAuthOptions(),
      );
      return true;
    } catch (e) {
      if (kDebugMode) print('Reception Error: $e');
      return false;
    }
  }

  // Marquer comme administré au patient
  Future<bool> administrerMedicament(String identifiant) async {
    try {
      final username = await _authService.getUsername();
      await _dio.post(
        '/colis/administration',
        queryParameters: {'identifiant': identifiant, 'username': username},
        options: await _getAuthOptions(),
      );
      return true;
    } catch (e) {
      if (kDebugMode) print('Administration Error: $e');
      return false;
    }
  }

  // Signaler un incident
  Future<bool> signalerIncident(String identifiant, String details) async {
    try {
      final username = await _authService.getUsername();
      await _dio.post(
        '/colis/incident',
        queryParameters: {
          'identifiant': identifiant,
          'username': username,
          'details': details
        },
        options: await _getAuthOptions(),
      );
      return true;
    } catch (e) {
      if (kDebugMode) print('Incident Error: $e');
      return false;
    }
  }
}
