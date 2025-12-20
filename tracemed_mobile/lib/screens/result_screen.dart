import 'package:flutter/material.dart';
import '../services/audit_service.dart';
import 'package:intl/intl.dart';
import 'package:intl/intl.dart';
import 'reception_screen.dart';
import 'report_screen.dart';

class ResultScreen extends StatefulWidget {
  final String idColis;

  const ResultScreen({super.key, required this.idColis});

  @override
  State<ResultScreen> createState() => _ResultScreenState();
}

class _ResultScreenState extends State<ResultScreen> {
  final _auditService = AuditService();
  late Future<Map<String, dynamic>> _auditFuture;

  @override
  void initState() {
    super.initState();
    _auditFuture = _auditService.getAudit(widget.idColis);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Résultat Détection')),
      body: FutureBuilder<Map<String, dynamic>>(
        future: _auditFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Erreur: ${snapshot.error}'));
          } else if (!snapshot.hasData) {
            return const Center(child: Text('Aucune donnée trouvée'));
          }

          final data = snapshot.data!;
          final status = data['status'] as String? ?? 'UNKNOWN';
          final blockId = data['blockId'];
          final merkleRoot = data['merkleHash'];

          return SingleChildScrollView(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                if (status == 'VALID')
                  _buildValidHeader(data['currentOwner'])
                else if (status == 'CORRUPTED')
                  _buildCorruptedHeader()
                else
                  Center(child: Text('Statut inconnu: $status')),

                const SizedBox(height: 30),
                
                // Info Colis
                Card(
                  elevation: 4,
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      children: [
                        Text(
                          widget.idColis,
                          style: const TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          'Scanné le ${DateFormat('dd/MM/yyyy HH:mm').format(DateTime.now())}',
                          style: const TextStyle(color: Colors.grey),
                        ),
                      ],
                    ),
                  ),
                ),

                const SizedBox(height: 20),

                // Blockchain Proof Expansion
                Card(
                  elevation: 2,
                  child: ExpansionTile(
                    title: const Text('Preuve Blockchain', style: TextStyle(fontWeight: FontWeight.bold)),
                    leading: const Icon(Icons.verified_user),
                    children: [
                      Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            _buildDetailRow('Dernier Bloc Validé', '#${blockId ?? "N/A"}'),
                            const SizedBox(height: 10),
                            const Text('Hash Merkle :', style: TextStyle(fontWeight: FontWeight.bold)),
                            SelectableText(
                              merkleRoot ?? "N/A",
                              style: const TextStyle(fontFamily: 'Courier', fontSize: 12),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildDetailRow(String label, String value) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: const TextStyle(fontWeight: FontWeight.bold)),
        Text(value),
      ],
    );
  }

  Widget _buildValidHeader(String? currentOwner) {
    return Column(
      children: [
        const Icon(Icons.check_circle, color: Colors.green, size: 80),
        const SizedBox(height: 10),
        const Text(
          'Certifié Authentique',
          style: TextStyle(color: Colors.green, fontSize: 24, fontWeight: FontWeight.bold),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 20),
        ElevatedButton.icon(
          onPressed: () {
             print('DEBUG: Button Clicked');
             Navigator.push(
               context,
               MaterialPageRoute(
                 builder: (_) => ReceptionScreen(
                   idColis: widget.idColis,
                   currentOwner: currentOwner ?? 'Inconnu',
                 ),
               ),
             );
          },
          icon: const Icon(Icons.handshake),
          label: const Text('RÉCEPTIONNER MAINTENANT', style: TextStyle(fontWeight: FontWeight.bold)),
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.orange, 
            foregroundColor: Colors.white,
            padding: const EdgeInsets.symmetric(horizontal: 30, vertical: 15),
          ),
        ),
        const SizedBox(height: 20),
        TextButton.icon(
          onPressed: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => ReportScreen(idColis: widget.idColis)),
            );
          },
          icon: const Icon(Icons.report_problem, color: Colors.red),
          label: const Text('Signaler une Anomalie', style: TextStyle(color: Colors.red)),
        ),
      ],
    );
  }

  Widget _buildCorruptedHeader() {
    return Column(
      children: [
        const Icon(Icons.warning_amber_rounded, color: Colors.red, size: 80),
        const SizedBox(height: 10),
        const Text(
          'ATTENTION\nNon Conforme',
          style: TextStyle(color: Colors.red, fontSize: 24, fontWeight: FontWeight.bold),
          textAlign: TextAlign.center,
        ),
      ],
    );
  }
}
