import 'package:flutter/material.dart';
import '../services/audit_service.dart';
import 'home_screen.dart';

class ReportScreen extends StatefulWidget {
  final String idColis;

  const ReportScreen({super.key, required this.idColis});

  @override
  State<ReportScreen> createState() => _ReportScreenState();
}

class _ReportScreenState extends State<ReportScreen> {
  final _auditService = AuditService();
  final _commentController = TextEditingController();
  String? _selectedType;
  bool _isLoading = false;

  final List<String> _incidentTypes = ['Casse', 'Vol/Disparition', 'Température', 'Emballage Endommagé', 'Autre'];

  Future<void> _submitReport() async {
    if (_selectedType == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Veuillez sélectionner un type d\'incident')),
      );
      return;
    }

    setState(() => _isLoading = true);
    
    // Simulate Blockchain call
    final success = await _auditService.reportIncident(
      widget.idColis, 
      _selectedType!, 
      _commentController.text
    );

    setState(() => _isLoading = false);

    if (success && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Incident SIGNALÉ. Colis marqué comme SUSPECT.'),
          backgroundColor: Colors.redAccent,
          duration: Duration(seconds: 4),
        ),
      );
      // Back to Home
      Navigator.pushAndRemoveUntil(
        context,
        MaterialPageRoute(builder: (_) => const HomeScreen()),
        (route) => false,
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Signaler une Anomalie'), backgroundColor: Colors.redAccent, foregroundColor: Colors.white),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const Icon(Icons.report_problem, size: 60, color: Colors.orange),
            const SizedBox(height: 20),
            Text(
              'Signalement pour ${widget.idColis}',
              textAlign: TextAlign.center,
              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 30),
            
            Theme(
              data: Theme.of(context).copyWith(primaryColor: Colors.red),
              child: DropdownButtonFormField<String>(
                initialValue: _selectedType,
                items: _incidentTypes.map((t) => DropdownMenuItem(value: t, child: Text(t))).toList(),
                onChanged: (val) => setState(() => _selectedType = val),
                 decoration: const InputDecoration(
                  labelText: 'Type d\'incident', 
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.category),
                ),
              ),
            ),
            const SizedBox(height: 20),
            
            TextField(
              controller: _commentController,
              maxLines: 3,
              decoration: const InputDecoration(
                labelText: 'Commentaire / Détails',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.comment),
              ),
            ),
            const SizedBox(height: 20),

            OutlinedButton.icon(
              onPressed: () {
                ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Photo simulée ajoutée')));
              },
              icon: const Icon(Icons.camera_alt),
              label: const Text('Ajouter une Photo (Preuve)'),
              style: OutlinedButton.styleFrom(
                padding: const EdgeInsets.all(15),
              ),
            ),

            const SizedBox(height: 40),

            _isLoading
                ? const Center(child: CircularProgressIndicator())
                : ElevatedButton.icon(
                    onPressed: _submitReport,
                    icon: const Icon(Icons.send),
                    label: const Text('SIGNALER L\'INCIDENT'),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.red,
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.all(15),
                      textStyle: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                    ),
                  ),
          ],
        ),
      ),
    );
  }
}
