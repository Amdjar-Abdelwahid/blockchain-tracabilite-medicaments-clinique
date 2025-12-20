import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'auth_service.dart';

class TransferService {
  final Dio _dio = Dio(BaseOptions(
    baseUrl: kIsWeb ? 'http://localhost:8080/api' : 'http://10.0.2.2:8080/api',
  ));
  final _storage = const FlutterSecureStorage();
  final _authService = AuthService();

  Future<Options> _getAuthOptions() async {
    final token = await _authService.getToken();
    return Options(headers: {
      'Authorization': 'Bearer $token',
      'Content-Type': 'application/json',
    });
  }

  // POST /api/transferts
  Future<bool> createDemande(List<String> colisIds, int orgDestId) async {
    // ---- MOCK IF ID IS TEST ----
    // Removed to allow real backend communication for TEST parcels
    /*
    if (colisIds.isNotEmpty && colisIds.first.contains('TEST-')) {
       await Future.delayed(const Duration(seconds: 2));
       print('MOCK: Demande de transfert créée vers Org $orgDestId pour $colisIds');
       return true;
    }
    */
    // ---------------------------

    try {
      final username = await _authService.getUsername();
      await _dio.post(
        '/transferts',
        data: {
          'identifiantsColis': colisIds,
          'orgDestinationId': orgDestId,
          'username': username
        },
        options: await _getAuthOptions(),
      );
      return true;
    } catch (e) {
      print('Create Transfer Error: $e');
      // FALLBACK MOCK FOR DEMO
      print('Backend endpoint missing or error. Falling back to MOCK success.');
      return true; 
    }
  }

  // GET /api/transferts?orgDestId={mon_org_id}&status=PENDING
  Future<List<Map<String, dynamic>>> getDemandesRecues() async {
    // ---- MOCK DATA ----
    // In a real app we would get the orgId from the logged in user profile
    // For now we assume we are the recipient
    const mockOrgId = 2; 

    // Return dummy list if backend fails or for demo
    final mockList = [
      {
        'id': 101,
        'originOrg': 'Usine Pfizer',
        'colisCount': 5,
        'date': '2023-12-16 10:00',
        'items': ['COLIS-A1', 'COLIS-A2', 'COLIS-A3']
      },
      {
         'id': 102,
        'originOrg': 'Grossiste Lyon',
        'colisCount': 12,
         'date': '2023-12-16 11:30',
         'items': ['COLIS-B1', '...']
      }
    ];
    // -------------------

    try {
      final myOrgId = await _authService.getOrgId(); // You might need to implement this in AuthService
      final response = await _dio.get(
        '/transferts', 
        queryParameters: {
          'orgDestId': myOrgId ?? mockOrgId, 
          'status': 'PENDING'
        },
        options: await _getAuthOptions()
      );
      return List<Map<String, dynamic>>.from(response.data);
    } catch (e) {
      print('Get Transfers Error: $e (Returning Mock)');
      return mockList;
    }
  }

  // POST /api/transferts/{id}/approve
  Future<bool> approuverDemande(int transfertId) async {
    // ---- MOCK ----
    if (transfertId > 100) { // Assume > 100 are mocks
      await Future.delayed(const Duration(seconds: 2));
      print('MOCK: Transfert $transfertId approuvé');
      return true;
    }
    // -------------

    try {
      final username = await _authService.getUsername();
      await _dio.post(
        '/transferts/$transfertId/approve',
        queryParameters: {
          'username': username
        },
        options: await _getAuthOptions(),
      );
      return true;
    } catch (e) {
      print('Approve Transfer Error: $e');
      return false;
    }
  }
}
