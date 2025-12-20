import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class AuditService {
  final Dio _dio = Dio(BaseOptions(
    baseUrl: kIsWeb ? 'http://localhost:8080/api' : 'http://10.0.2.2:8080/api',
  ));
  final _storage = const FlutterSecureStorage();

  Future<Options> _getAuthOptions() async {
    final token = await _storage.read(key: 'jwt_token');
    return Options(headers: {
      'Authorization': 'Bearer $token',
      'Content-Type': 'application/json',
    });
  }

  Future<Map<String, dynamic>> getAudit(String idColis) async {
    // ---- MOCK DATA FOR TESTING ----
    if (idColis == 'TEST-VALID') {
      await Future.delayed(const Duration(seconds: 1)); // Simulate network
      return {
        'status': 'VALID',
        'blockId': '45',
        'merkleHash': 'a1b2c3d4e5f6g7h8i9j0',
        'currentOwner': 'Transporteur Express (DHL)',
      };
    }
    if (idColis == 'TEST-CORRUPTED') {
      await Future.delayed(const Duration(seconds: 1));
      return {
        'status': 'CORRUPTED',
        'blockId': null,
        'merkleHash': null,
      };
    }
    if (idColis == 'TEST-EXPIRED') {
      await Future.delayed(const Duration(seconds: 1));
      return {
        'status': 'VALID', // Technically valid on chain, but...
        'drugName': 'Vaccin Grippe',
        'expiryDate': '2020-01-01', // Expired
        'isRecalled': true,
        'currentOwner': 'Hôpital St. Louis',
      };
    }
    // -------------------------------

    final response = await _dio.get('/audit/$idColis');
    return response.data;
  }

  Future<bool> transferPackage(String idColis) async {
    // ---- MOCK FOR TEST IDs ----
    if (idColis.startsWith('TEST-')) {
       await Future.delayed(const Duration(seconds: 2));
       return true;
    }
    // ---------------------------

    try {
      final username = await _storage.read(key: 'username');
      await _dio.post(
        '/colis/reception',
        queryParameters: {
          'identifiant': idColis,
          'username': username,
        },
        options: await _getAuthOptions(),
      );
      return true;
    } catch (e) {
      print('Transfer Error: $e');
      return false;
    }
  }

  Future<String> createParcel(String drugName, String lotNumber) async {
    // ---- MOCK GENESIS TRANSACTION ----
    await Future.delayed(const Duration(seconds: 2));
    final newId = 'COLIS-${DateTime.now().millisecondsSinceEpoch.toString().substring(8)}';
    print('Transaction GENESIS pour $newId ($drugName) envoyée à la blockchain');
    return newId;
    // ---------------------------------
  }

  Future<bool> consumePackage(String idColis) async {
    // ---- MOCK FOR TEST IDs ----
    if (idColis.startsWith('TEST-')) {
       await Future.delayed(const Duration(seconds: 2));
       return true;
    }
    // ---------------------------

    try {
      final username = await _storage.read(key: 'username');
      await _dio.post(
        '/colis/administration',
         queryParameters: {
          'identifiant': idColis,
          'username': username,
        },
        options: await _getAuthOptions(),
      );
      return true;
    } catch (e) {
       print('Consume Error: $e');
       return false;
    }
  }

  Future<bool> reportIncident(String idColis, String type, String comment) async {
    // ---- MOCK FOR TEST IDs ----
    if (idColis.startsWith('TEST-')) {
       await Future.delayed(const Duration(seconds: 2));
       return true;
    }
    // ---------------------------

    try {
      final username = await _storage.read(key: 'username');
      await _dio.post(
        '/colis/incident',
         queryParameters: {
          'identifiant': idColis,
          'username': username,
          'details': '$type: $comment',
        },
        options: await _getAuthOptions(),
      );
      return true;
    } catch (e) {
       print('Incident Error: $e');
       return false;
    }
  }
}
