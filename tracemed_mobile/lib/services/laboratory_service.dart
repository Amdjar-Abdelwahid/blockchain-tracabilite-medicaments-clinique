import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'auth_service.dart';

class LaboratoryService {
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

  // --- MEDICAMENTS ---

  Future<List<dynamic>> getAllMedicaments() async {
    try {
      final response = await _dio.get('/medicaments', options: await _getAuthOptions());
      return response.data;
    } catch (e) {
      if (kDebugMode) print('Get Meds Error: $e');
      return [];
    }
  }

  Future<bool> createMedicament(String nom, String description, String manufacturer) async {
    try {
      await _dio.post(
        '/medicaments',
        data: {
          'nom': nom,
          'description': description,
          'fabricant': manufacturer,
        },
        options: await _getAuthOptions(),
      );
      return true;
    } catch (e) {
      if (kDebugMode) print('Create Med Error: $e');
      return false;
    }
  }

  // --- LOTS ---

  Future<List<dynamic>> getAllLots() async {
    try {
      final response = await _dio.get('/lots', options: await _getAuthOptions());
      return response.data;
    } catch (e) {
      if (kDebugMode) print('Get Lots Error: $e');
      return [];
    }
  }

  Future<bool> createLot({
    required String numeroLot,
    required String dateFabrication, // YYYY-MM-DD
    required String datePeremption,  // YYYY-MM-DD
    required int quantite,
    required int medicamentId,
  }) async {
    try {
      await _dio.post(
        '/lots',
        data: {
          'numeroLot': numeroLot,
          'dateFabrication': dateFabrication,
          'datePeremption': datePeremption,
          'quantite': quantite,
          'medicamentId': medicamentId
        },
        options: await _getAuthOptions(),
      );
      return true;
    } catch (e) {
      if (kDebugMode) print('Create Lot Error: $e');
      return false;
    }
  }
}
