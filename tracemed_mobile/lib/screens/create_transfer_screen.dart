import 'package:flutter/material.dart';
import '../services/transfer_service.dart';
import 'home_screen.dart';

class CreateTransferScreen extends StatefulWidget {
  const CreateTransferScreen({super.key});

  @override
  State<CreateTransferScreen> createState() => _CreateTransferScreenState();
}

class _CreateTransferScreenState extends State<CreateTransferScreen> {
  final _transferService = TransferService();
  final List<String> _scannedColis = [];
  bool _isLoading = false;
  
  // Mock Organizations
  final List<Map<String, dynamic>> _orgs = [
    {'id': 2, 'name': 'Clinique St. Louis'},
    {'id': 3, 'name': 'Grossiste Lyon'},
    {'id': 4, 'name': 'Pharmacie Centrale'},
  ];
  int? _selectedOrgId;

  void _addMockScan() {
    setState(() {
      _scannedColis.add('COLIS-TEST-${_scannedColis.length + 1}');
    });
  }
  
  // In a real app, this would use MobileScanner
  void _startScan() {
     _addMockScan();
     ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Colis Ajouté (Simulé)')));
  }

  Future<void> _submitTransfer() async {
    if (_scannedColis.isEmpty || _selectedOrgId == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Veuillez ajouter des colis et choisir un destinataire')),
      );
      return;
    }

    setState(() => _isLoading = true);
    
    final success = await _transferService.createDemande(_scannedColis, _selectedOrgId!);

    setState(() => _isLoading = false);

    if (success && mounted) {
       showDialog(
        context: context,
        builder: (_) => AlertDialog(
          title: const Text('Expédition Créée'),
          content: Text('${_scannedColis.length} colis envoyés vers Org #$_selectedOrgId'),
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
    } else if (mounted) {
       ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Erreur lors de la création')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Nouvelle Expédition')),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            DropdownButtonFormField<int>(
              initialValue: _selectedOrgId,
              items: _orgs.map((org) => DropdownMenuItem<int>(
                value: org['id'], 
                child: Text(org['name']),
              )).toList(),
              onChanged: (val) => setState(() => _selectedOrgId = val),
              decoration: const InputDecoration(labelText: 'Destinataire', border: OutlineInputBorder()),
            ),
            const SizedBox(height: 20),
            
            Expanded(
              child: Card(
                child: ListView.builder(
                  padding: const EdgeInsets.all(10),
                  itemCount: _scannedColis.length,
                  itemBuilder: (ctx, i) => ListTile(
                    leading: const Icon(Icons.inventory_2),
                    title: Text(_scannedColis[i]),
                    trailing: IconButton(
                      icon: const Icon(Icons.delete, color: Colors.red),
                      onPressed: () => setState(() => _scannedColis.removeAt(i)),
                    ),
                  ),
                ),
              ),
            ),
            
            const SizedBox(height: 10),
            OutlinedButton.icon(
              onPressed: _startScan,
              icon: const Icon(Icons.qr_code_scanner),
              label: const Text('SCANNER UN COLIS (+1)'),
              style: OutlinedButton.styleFrom(padding: const EdgeInsets.all(15)),
            ),
            const SizedBox(height: 20),

            _isLoading 
              ? const Center(child: CircularProgressIndicator())
              : ElevatedButton.icon(
                  onPressed: _submitTransfer, 
                  icon: const Icon(Icons.send), 
                  label: const Text('CRÉER EXPÉDITION'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.blueAccent, 
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.all(15),
                    textStyle: const TextStyle(fontSize: 18),
                  ),
                ),
          ],
        ),
      ),
    );
  }
}
