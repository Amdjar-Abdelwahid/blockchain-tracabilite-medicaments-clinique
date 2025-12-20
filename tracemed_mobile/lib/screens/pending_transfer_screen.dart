import 'package:flutter/material.dart';
import '../services/transfer_service.dart';

class PendingTransferScreen extends StatefulWidget {
  const PendingTransferScreen({super.key});

  @override
  State<PendingTransferScreen> createState() => _PendingTransferScreenState();
}

class _PendingTransferScreenState extends State<PendingTransferScreen> {
  final _transferService = TransferService();
  late Future<List<Map<String, dynamic>>> _transfersFuture;

  @override
  void initState() {
    super.initState();
    _refresh();
  }

  void _refresh() {
    setState(() {
      _transfersFuture = _transferService.getDemandesRecues();
    });
  }

  Future<void> _acceptTransfer(int id) async {
    final success = await _transferService.approuverDemande(id);
    if (success && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Transfert Accepté avec Succès !'), backgroundColor: Colors.green),
      );
      _refresh(); // Reload list
    } else if (mounted) {
       ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Erreur lors de l\'approbation')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Réceptions en Attente')),
      body: FutureBuilder<List<Map<String, dynamic>>>(
        future: _transfersFuture,
        builder: (context, snapshot) {
           if (snapshot.connectionState == ConnectionState.waiting) {
             return const Center(child: CircularProgressIndicator());
           } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
             return const Center(child: Text('Aucune demande en attente'));
           }

           final transfers = snapshot.data!;
           return ListView.builder(
             padding: const EdgeInsets.all(16),
             itemCount: transfers.length,
             itemBuilder: (ctx, i) {
                final t = transfers[i];
                return Card(
                  margin: const EdgeInsets.only(bottom: 16),
                  elevation: 3,
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                             Text('Expédition #${t['id']}', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                             Chip(label: const Text('PENDING'), backgroundColor: Colors.orange.shade100),
                          ],
                        ),
                        const SizedBox(height: 10),
                        Text('De : ${t['originOrg']}', style: const TextStyle(fontSize: 18)),
                        Text('${t['colisCount']} Colis - ${t['date']}', style: const TextStyle(color: Colors.grey)),
                        const SizedBox(height: 10),
                        Wrap(
                          spacing: 8.0,
                          children: (t['items'] as List).map<Widget>((item) => Chip(label: Text(item.toString()), materialTapTargetSize: MaterialTapTargetSize.shrinkWrap)).toList(),
                        ),
                        const SizedBox(height: 20),
                        SizedBox(
                          width: double.infinity,
                          child: ElevatedButton.icon(
                            onPressed: () => _acceptTransfer(t['id']),
                            icon: const Icon(Icons.check),
                            label: const Text('ACCEPTER LE TRANSFERT'),
                            style: ElevatedButton.styleFrom(
                              backgroundColor: Colors.green, 
                              foregroundColor: Colors.white,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                );
             },
           );
        },
      ),
    );
  }
}
