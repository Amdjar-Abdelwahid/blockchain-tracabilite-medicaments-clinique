import 'package:flutter/material.dart';
import '../services/audit_service.dart';
import '../services/colis_service.dart';
import 'home_screen.dart';

class AdministerScreen extends StatefulWidget {
  final String idColis;

  const AdministerScreen({super.key, required this.idColis});

  @override
  State<AdministerScreen> createState() => _AdministerScreenState();
}

class _AdministerScreenState extends State<AdministerScreen> {
  final _auditService = AuditService();
  final _colisService = ColisService();
  late Future<Map<String, dynamic>> _auditFuture;
  bool _isConsuming = false;

  @override
  void initState() {
    super.initState();
    _auditFuture = _auditService.getAudit(widget.idColis);
  }

  Future<void> _markAsConsumed() async {
    setState(() => _isConsuming = true);
    final success = await _colisService.administrerMedicament(widget.idColis);
    setState(() => _isConsuming = false);

    if (success && mounted) {
      showDialog(
        context: context,
        barrierDismissible: false,
        builder: (_) => AlertDialog(
          title: const Text('Administration Réussie'),
          content: const Column(
            mainAxisSize: MainAxisSize.min,
            children: [
               Icon(Icons.check_circle, color: Colors.green, size: 60),
               SizedBox(height: 10),
               Text('Le médicament a été marqué comme consommé. Stock mis à jour.'),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pushAndRemoveUntil(
                context, 
                MaterialPageRoute(builder: (_) => const HomeScreen()), 
                (route) => false
              ),
              child: const Text('OK'),
            ),
          ],
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Administration Patient')),
      body: FutureBuilder<Map<String, dynamic>>(
        future: _auditFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (!snapshot.hasData) {
            return const Center(child: Text('Erreur scan'));
          }

          final data = snapshot.data!;
          // Simple logic check for mock
          final bool isRecalled = data['isRecalled'] == true;
          final String? expiryDateStr = data['expiryDate']; // YYYY-MM-DD
          bool isExpired = false;
          
          if (expiryDateStr != null) {
             try {
               final expiry = DateTime.parse(expiryDateStr);
               if (DateTime.now().isAfter(expiry)) {
                 isExpired = true;
               }
             } catch (_) {}
          }

          final bool isDanger = isRecalled || isExpired;

          return Padding(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  'Colis : ${widget.idColis}',
                  style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 30),
                
                if (isDanger)
                  _buildDangerAlert(isRecalled, isExpired)
                else
                  _buildSafeSummary(data),

                const Spacer(),

                if (!isDanger)
                  _isConsuming
                      ? const Center(child: CircularProgressIndicator())
                      : ElevatedButton.icon(
                          onPressed: _markAsConsumed,
                          icon: const Icon(Icons.local_pharmacy),
                          label: const Text('MARQUER COMME CONSOMMÉ'),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.blueAccent,
                            foregroundColor: Colors.white,
                            padding: const EdgeInsets.all(16),
                          ),
                        ),
                 if (isDanger)
                   ElevatedButton.icon(
                      onPressed: () => Navigator.pop(context),
                      icon: const Icon(Icons.cancel),
                      label: const Text('ANNULER L\'ADMINISTRATION'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.grey,
                        foregroundColor: Colors.white,
                        padding: const EdgeInsets.all(16),
                      ),
                   ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildDangerAlert(bool isRecalled, bool isExpired) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.red.shade50,
        border: Border.all(color: Colors.red, width: 2),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Column(
        children: [
          const Icon(Icons.warning, color: Colors.red, size: 80),
          const SizedBox(height: 20),
          const Text(
            'DANGER !\nNE PAS ADMINISTRER',
            style: TextStyle(color: Colors.red, fontSize: 26, fontWeight: FontWeight.bold),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 20),
          if (isRecalled)
            const Text('⚠️ Ce lot fait l\'objet d\'un RAPPEL SANITAIRE.', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold), textAlign: TextAlign.center),
          if (isExpired)
            const Text('⏳ Ce médicament est PÉRIMÉ.', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold), textAlign: TextAlign.center),
        ],
      ),
    );
  }

  Widget _buildSafeSummary(Map<String, dynamic> data) {
    return Column(
      children: [
        const Icon(Icons.check_circle_outline, color: Colors.green, size: 80),
        const SizedBox(height: 10),
        const Text(
          'Conforme',
          style: TextStyle(color: Colors.green, fontSize: 24, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 20),
        ListTile(
          title: const Text('Médicament'),
          subtitle: Text(data['drugName'] ?? 'Paracetamol 500mg (Mock)'),
          leading: const Icon(Icons.medication),
        ),
        ListTile(
          title: const Text('Expiration'),
          subtitle: Text(data['expiryDate'] ?? '2030-12-31'),
          leading: const Icon(Icons.calendar_today),
        ),
      ],
    );
  }
}
