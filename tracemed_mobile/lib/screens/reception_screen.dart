import 'package:flutter/material.dart';
import '../services/audit_service.dart';
import 'home_screen.dart';

class ReceptionScreen extends StatefulWidget {
  final String idColis;
  final String currentOwner;

  const ReceptionScreen({
    super.key,
    required this.idColis,
    required this.currentOwner,
  });

  @override
  State<ReceptionScreen> createState() => _ReceptionScreenState();
}

class _ReceptionScreenState extends State<ReceptionScreen> {
  final _auditService = AuditService();
  bool _isLoading = false;

  Future<void> _confirmReception() async {
    setState(() => _isLoading = true);
    
    // Simulate Blockchain call
    final success = await _auditService.transferPackage(widget.idColis);

    setState(() => _isLoading = false);

    if (success && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Succès : Responsabilité Transférée !'),
          backgroundColor: Colors.green,
          duration: Duration(seconds: 3),
        ),
      );
      // Back to Home
      Navigator.pushAndRemoveUntil(
        context,
        MaterialPageRoute(builder: (_) => const HomeScreen()),
        (route) => false,
      );
    } else if (mounted) {
       ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Erreur lors du transfert')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Réception Colis')),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
             const SizedBox(height: 20),
             const Icon(Icons.handshake, size: 80, color: Colors.blueAccent),
             const SizedBox(height: 30),
             Text(
               'Colis : ${widget.idColis}',
               textAlign: TextAlign.center,
               style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
             ),
             const SizedBox(height: 40),
             Card(
               color: Colors.orange.shade50,
               child: Padding(
                 padding: const EdgeInsets.all(16.0),
                 child: Column(
                   children: [
                     const Text('ACTUELLEMENT POSSÉDÉ PAR :', style: TextStyle(color: Colors.orange, fontWeight: FontWeight.bold)),
                     const SizedBox(height: 10),
                     Text(
                       widget.currentOwner,
                       style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                       textAlign: TextAlign.center,
                     ),
                   ],
                 ),
               ),
             ),
             const Spacer(),
             _isLoading
                 ? const Center(child: CircularProgressIndicator())
                 : SizedBox(
                    height: 60,
                    child: ElevatedButton(
                      onPressed: _confirmReception,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.blueAccent,
                         shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                      ),
                      child: const Text(
                        'CONFIRMER LA RÉCEPTION',
                        style: TextStyle(fontSize: 18, color: Colors.white, fontWeight: FontWeight.bold),
                      ),
                    ),
                 ),
              const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }
}
