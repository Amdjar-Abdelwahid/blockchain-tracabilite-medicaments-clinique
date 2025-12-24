import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../services/laboratory_service.dart';

class LaboratoryDashboardScreen extends StatefulWidget {
  const LaboratoryDashboardScreen({super.key});

  @override
  State<LaboratoryDashboardScreen> createState() => _LaboratoryDashboardScreenState();
}

class _LaboratoryDashboardScreenState extends State<LaboratoryDashboardScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  final LaboratoryService _labService = LaboratoryService();

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Tableau de Bord Laboratoire'),
        backgroundColor: Colors.indigo,
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: Colors.white,
          tabs: const [
            Tab(icon: Icon(Icons.medication), text: 'Catalogue'),
            Tab(icon: Icon(Icons.factory), text: 'Production (Lots)'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildCatalogueTab(),
          _buildProductionTab(),
        ],
      ),
    );
  }

  // --- CATALOGUE TAB ---

  Widget _buildCatalogueTab() {
    return FutureBuilder<List<dynamic>>(
      future: _labService.getAllMedicaments(),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }
        final meds = snapshot.data ?? [];
        return Scaffold(
          body: ListView.builder(
            itemCount: meds.length,
            itemBuilder: (context, index) {
              final med = meds[index];
              return Card(
                margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                child: ListTile(
                  leading: const Icon(Icons.medication_liquid, color: Colors.indigo),
                  title: Text(med['nom'] ?? 'Inconnu'),
                  subtitle: Text(med['fabricant'] ?? ''),
                  trailing: Text('ID: ${med['id']}'),
                ),
              );
            },
          ),
          floatingActionButton: FloatingActionButton(
            backgroundColor: Colors.indigo,
            onPressed: _showCreateMedicamentDialog,
            child: const Icon(Icons.add),
          ),
        );
      },
    );
  }

  void _showCreateMedicamentDialog() {
    final nameController = TextEditingController();
    final descController = TextEditingController();
    final manufController = TextEditingController(text: "Laboratoire Central");
    
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Nouveau Médicament'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(controller: nameController, decoration: const InputDecoration(labelText: 'Nom (ex: Doliprane)')),
            TextField(controller: descController, decoration: const InputDecoration(labelText: 'Description')),
            TextField(controller: manufController, decoration: const InputDecoration(labelText: 'Fabricant')),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Annuler')),
          ElevatedButton(
            onPressed: () async {
              final success = await _labService.createMedicament(
                nameController.text, 
                descController.text, 
                manufController.text
              );
              Navigator.pop(context);
              if (success) setState(() {});
            },
            child: const Text('Créer'),
          ),
        ],
      ),
    );
  }

  // --- PRODUCTION TAB (LOTS) ---

  Widget _buildProductionTab() {
    return FutureBuilder<List<dynamic>>(
      future: _labService.getAllLots(),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }
        final lots = snapshot.data ?? [];
        return Scaffold(
          body: ListView.builder(
            itemCount: lots.length,
            itemBuilder: (context, index) {
               final lot = lots[index];
               final medName = lot['medicament'] != null ? lot['medicament']['nom'] : '??';
               return Card(
                margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                child: ListTile(
                  leading: const Icon(Icons.qr_code_2, color: Colors.deepPurple),
                  title: Text('Lot: ${lot['numeroLot']}'),
                  subtitle: Text('$medName - Qty: ${lot['quantite']}'),
                  trailing: Text(lot['datePeremption'] ?? ''),
                ),
              );
            },
          ),
          floatingActionButton: FloatingActionButton(
             backgroundColor: Colors.deepPurple,
            onPressed: () async {
              final meds = await _labService.getAllMedicaments();
              _showCreateLotDialog(meds);
            },
            child: const Icon(Icons.add),
          ),
        );
      },
    );
  }

  void _showCreateLotDialog(List<dynamic> meds) {
    if (meds.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Aucun médicament dans le catalogue. Créez-en un d\'abord.')));
      return;
    }

    // Generate a cleaner ID: LOT-YYYYMMDD-XXXX
    final now = DateTime.now();
    final String datePart = "${now.year}${now.month.toString().padLeft(2, '0')}${now.day.toString().padLeft(2, '0')}";
    final String randomPart = now.millisecondsSinceEpoch.toString().substring(9); // Last digits
    final batchCtrl = TextEditingController(text: "LOT-$datePart-$randomPart");
    
    final qtyCtrl = TextEditingController(text: "1000");
    int? selectedMedId = meds.first['id'];
    DateTime selectedDate = DateTime.now().add(const Duration(days: 365 * 2)); // 2 years default

    showDialog(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setState) {
          return AlertDialog(
            title: const Text('Fabriquer un Lot'),
            content: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  DropdownButtonFormField<int>(
                    initialValue: selectedMedId,
                    items: meds.map<DropdownMenuItem<int>>((m) => DropdownMenuItem(value: m['id'], child: Text(m['nom']))).toList(),
                    onChanged: (v) => setState(() => selectedMedId = v),
                    decoration: const InputDecoration(labelText: 'Médicament'),
                  ),
                  const SizedBox(height: 10),
                  TextField(
                    controller: batchCtrl, 
                    readOnly: true,
                    decoration: InputDecoration(
                      labelText: 'Numéro de Lot (Généré)', 
                      filled: true,
                      fillColor: Colors.grey.shade200,
                      suffixIcon: const Icon(Icons.lock_outline, color: Colors.grey),
                    )
                  ),
                  const SizedBox(height: 10),
                  TextField(controller: qtyCtrl, keyboardType: TextInputType.number, decoration: const InputDecoration(labelText: 'Quantité')),
                  const SizedBox(height: 10),
                  Row(
                    children: [
                      Text('Péremption: ${DateFormat('yyyy-MM-dd').format(selectedDate)}'),
                      IconButton(
                        icon: const Icon(Icons.calendar_today),
                        onPressed: () async {
                          final d = await showDatePicker(
                            context: context,
                            initialDate: selectedDate,
                            firstDate: DateTime.now(),
                            lastDate: DateTime.now().add(const Duration(days: 365 * 10)),
                          );
                          if (d != null) setState(() => selectedDate = d);
                        },
                      )
                    ],
                  )
                ],
              ),
            ),
            actions: [
              TextButton(onPressed: () => Navigator.pop(context), child: const Text('Annuler')),
              ElevatedButton(
                onPressed: () async {
                  if (selectedMedId == null) return;
                  final success = await _labService.createLot(
                    numeroLot: batchCtrl.text,
                    quantite: int.tryParse(qtyCtrl.text) ?? 0,
                    medicamentId: selectedMedId!,
                    dateFabrication: DateFormat('yyyy-MM-dd').format(DateTime.now()),
                    datePeremption: DateFormat('yyyy-MM-dd').format(selectedDate),
                  );
                  Navigator.pop(context);
                  if (success) {
                    // Update parent widget to refresh list
                    // Since we are in a dialog, 'this' refers to the dialog state if used blindly, 
                    // but here we need to trigger rebuild of the FutureBuilder in the parent.
                    // The easiest way is to set state on the parent *after* closing dialog. 
                  }
                  // We need to trigger a rebuild of the main screen
                  // This callback (onPressed) is closure-bound. 
                  // calling this.setState from the parent method context
                  // But we are inside a static method _showCreateLotDialog... no, it's an instance method.
                  // However, 'setState' inside StatefulBuilder updates the dialog.
                  // We need to update the screen.
                  // We will return a result from the dialog or just use the parent setState.
                  // Let's use the parent setState.
                  // NOTE: "setState" here refers to the StatefulBuilder's setState.
                  // So we need to call _LaboratoryDashboardScreenState's setState.
                  // But that is not easily accessible unless we pass it or reuse the outer scope.
                },
                child: const Text('Lancer Production'),
              ),
            ],
          );
        }
      ),
    ).then((_) => setState((){})); // Trigger rebuild of parent when dialog closes
  }
}
