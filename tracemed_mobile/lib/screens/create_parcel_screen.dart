import 'package:flutter/material.dart';
import 'package:qr_flutter/qr_flutter.dart';
import '../services/audit_service.dart';

class CreateParcelScreen extends StatefulWidget {
  const CreateParcelScreen({super.key});

  @override
  State<CreateParcelScreen> createState() => _CreateParcelScreenState();
}

class _CreateParcelScreenState extends State<CreateParcelScreen> {
  final _auditService = AuditService();
  final _lotController = TextEditingController();
  final _expiryController = TextEditingController();
  
  String? _selectedDrug;
  bool _isLoading = false;
  String? _generatedId;

  final List<String> _drugs = ['Paracetamol 500mg', 'Ibuprofène 400mg', 'Amoxicilline 1g', 'Vaccin Grippe'];

  Future<void> _generateParcel() async {
    if (_selectedDrug == null || _lotController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Veuillez remplir tous les champs')),
      );
      return;
    }

    setState(() => _isLoading = true);
    
    // Call Mock Service
    final id = await _auditService.createParcel(_selectedDrug!, _lotController.text);

    setState(() {
      _isLoading = false;
      _generatedId = id;
    });

    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Colis $id créé avec succès !'), backgroundColor: Colors.green),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Départ Usine')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const Text(
              'Création Nouveau Colis',
              style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 30),
            
            DropdownButtonFormField<String>(
              initialValue: _selectedDrug,
              items: _drugs.map((drug) => DropdownMenuItem(value: drug, child: Text(drug))).toList(),
              onChanged: (val) => setState(() => _selectedDrug = val),
              decoration: const InputDecoration(labelText: 'Nom du Médicament', border: OutlineInputBorder()),
            ),
            const SizedBox(height: 15),
            TextField(
              controller: _lotController,
              decoration: const InputDecoration(labelText: 'Numéro de Lot', border: OutlineInputBorder()),
            ),
            const SizedBox(height: 15),
            TextField(
              controller: _expiryController,
              decoration: const InputDecoration(labelText: 'Date d\'expiration (JJ/MM/AAAA)', border: OutlineInputBorder()),
              onTap: () async {
                FocusScope.of(context).requestFocus(FocusNode()); // Hide keyboard
                final date = await showDatePicker(
                  context: context,
                  initialDate: DateTime.now().add(const Duration(days: 365)),
                  firstDate: DateTime.now(),
                  lastDate: DateTime(2030),
                );
                if (date != null) {
                  _expiryController.text = "${date.day}/${date.month}/${date.year}";
                }
              },
            ),
            const SizedBox(height: 30),
            _isLoading
                ? const Center(child: CircularProgressIndicator())
                : ElevatedButton.icon(
                    onPressed: _generateParcel,
                    icon: const Icon(Icons.qr_code),
                    label: const Text('GÉNÉRER & IMPRIMER QR'),
                    style: ElevatedButton.styleFrom(
                      padding: const EdgeInsets.all(15),
                      textStyle: const TextStyle(fontSize: 18),
                    ),
                  ),

            if (_generatedId != null) ...[
              const SizedBox(height: 40),
              const Divider(),
              const SizedBox(height: 20),
              Center(
                child: Column(
                  children: [
                    Text('ID: $_generatedId', style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 10),
                    Container(
                      padding: const EdgeInsets.all(10),
                      decoration: BoxDecoration(
                        border: Border.all(color: Colors.black),
                        color: Colors.white,
                      ),
                      child: QrImageView(
                        data: 'http://localhost:8080/api/audit/$_generatedId',
                        version: QrVersions.auto,
                        size: 200.0,
                      ),
                    ),
                    const SizedBox(height: 10),
                    const Text('Imprimez ce code et collez-le sur la boîte.', style: TextStyle(color: Colors.grey)),
                  ],
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}
