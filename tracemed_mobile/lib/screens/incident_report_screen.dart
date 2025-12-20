import 'package:flutter/material.dart';
import '../services/colis_service.dart';

class IncidentReportScreen extends StatefulWidget {
  final String idColis;

  const IncidentReportScreen({super.key, required this.idColis});

  @override
  State<IncidentReportScreen> createState() => _IncidentReportScreenState();
}

class _IncidentReportScreenState extends State<IncidentReportScreen> {
  final _detailsController = TextEditingController();
  final _colisService = ColisService();
  bool _isLoading = false;

  Future<void> _submitIncident() async {
    if (_detailsController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Veuillez décrire l\'incident')));
      return;
    }

    setState(() => _isLoading = true);
    
    final success = await _colisService.signalerIncident(widget.idColis, _detailsController.text);
    
    setState(() => _isLoading = false);

    if (success && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Incident signalé avec succès'), backgroundColor: Colors.orange));
      Navigator.pop(context); // Go back to Home
    } else if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Erreur lors du signalement'), backgroundColor: Colors.red));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('⚠ Incident Colis ${widget.idColis}'), backgroundColor: Colors.orange),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            const Icon(Icons.warning_amber_rounded, size: 80, color: Colors.orange),
            const SizedBox(height: 20),
            Text('Signalement d\'un problème sur le colis ${widget.idColis}', textAlign: TextAlign.center, style: const TextStyle(fontSize: 18)),
            const SizedBox(height: 30),
            TextField(
              controller: _detailsController,
              maxLines: 5,
              decoration: const InputDecoration(
                labelText: 'Description de l\'incident',
                hintText: 'Ex: Colis ouvert, flacons cassés, température non respectée...',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 30),
            _isLoading 
              ? const CircularProgressIndicator()
              : ElevatedButton.icon(
                  onPressed: _submitIncident,
                  icon: const Icon(Icons.send),
                  label: const Text('ENVOYER LE RAPPORT'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.redAccent,
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(horizontal: 30, vertical: 15),
                  ),
                )
          ],
        ),
      ),
    );
  }
}
