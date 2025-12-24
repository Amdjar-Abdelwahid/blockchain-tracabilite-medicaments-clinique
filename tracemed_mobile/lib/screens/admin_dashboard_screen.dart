import 'package:flutter/material.dart';
import '../services/admin_service.dart';

class AdminDashboardScreen extends StatefulWidget {
  const AdminDashboardScreen({super.key});

  @override
  State<AdminDashboardScreen> createState() => _AdminDashboardScreenState();
}

class _AdminDashboardScreenState extends State<AdminDashboardScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  final AdminService _adminService = AdminService();

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Tableau de Bord Admin'),
        backgroundColor: const Color(0xFF009688),
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: Colors.white,
          tabs: const [
            Tab(icon: Icon(Icons.business), text: 'Organisations'),
            Tab(icon: Icon(Icons.people), text: 'Utilisateurs'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildOrganisationsTab(),
          _buildUsersTab(),
        ],
      ),
    );
  }

  // --- ORGANISATIONS TAB ---

  Widget _buildOrganisationsTab() {
    return FutureBuilder<List<dynamic>>(
      future: _adminService.getAllOrganisations(),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }
        if (snapshot.hasError) {
          return Center(child: Text('Erreur: ${snapshot.error}'));
        }
        final orgs = snapshot.data ?? [];
        return Scaffold(
          body: ListView.builder(
            itemCount: orgs.length,
            itemBuilder: (context, index) {
              final org = orgs[index];
              return Card(
                margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                child: ListTile(
                  leading: const Icon(Icons.business, color: Colors.blueGrey),
                  title: Text(org['nom'] ?? 'Sans Nom'),
                  subtitle: Text(org['typeOrganisation'] ?? 'Type Inconnu'),
                  trailing: Text('ID: ${org['id']}'),
                ),
              );
            },
          ),
          floatingActionButton: FloatingActionButton(
            onPressed: _showCreateOrgDialog,
            child: const Icon(Icons.add),
          ),
        );
      },
    );
  }

  void _showCreateOrgDialog() {
    final nameController = TextEditingController();
    final typeController = TextEditingController();
    
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Nouvelle Organisation'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(controller: nameController, decoration: const InputDecoration(labelText: 'Nom')),
            TextField(controller: typeController, decoration: const InputDecoration(labelText: 'Type (ex: USINE, CLINIQUE)')),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Annuler')),
          ElevatedButton(
            onPressed: () async {
              final success = await _adminService.createOrganisation(nameController.text, typeController.text);
              Navigator.pop(context);
              if (success) setState(() {});
            },
            child: const Text('Créer'),
          ),
        ],
      ),
    );
  }

  // --- USERS TAB ---

  Widget _buildUsersTab() {
     return FutureBuilder<List<dynamic>>(
      future: _adminService.getAllUsers(),
      builder: (context, snapshot) {
         if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }
        final users = snapshot.data ?? [];
        return Scaffold(
          body: ListView.builder(
            itemCount: users.length,
            itemBuilder: (context, index) {
              final user = users[index];
              final orgName = user['organisation'] != null ? user['organisation']['nom'] : 'Aucune';
              return Card(
                margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                child: ListTile(
                  leading: const Icon(Icons.person, color: Colors.teal),
                  title: Text(user['nomComplet'] ?? user['username']),
                  subtitle: Text('${user['role']} - $orgName'),
                ),
              );
            },
          ),
          floatingActionButton: FloatingActionButton(
            onPressed: () async {
               // We need orgs list for dropdown
               final orgs = await _adminService.getAllOrganisations();
               _showCreateUserDialog(orgs);
            },
            child: const Icon(Icons.person_add),
          ),
        );
      }
     );
  }

  void _showCreateUserDialog(List<dynamic> orgs) {
    final usernameCtrl = TextEditingController();
    final passwordCtrl = TextEditingController();
    final nameCtrl = TextEditingController();
    final emailCtrl = TextEditingController(); // Just use username or dummy if not needed
    String role = 'USER';
    int? selectedOrgId;

    showDialog(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setState) {
          return AlertDialog(
            title: const Text('Nouvel Utilisateur'),
            content: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  TextField(controller: usernameCtrl, decoration: const InputDecoration(labelText: 'Identifiant')),
                  TextField(controller: passwordCtrl, obscureText: true, decoration: const InputDecoration(labelText: 'Mot de passe')),
                  TextField(controller: nameCtrl, decoration: const InputDecoration(labelText: 'Nom Complet')),
                  DropdownButtonFormField<String>(
                    initialValue: role,
                    items: ['USER', 'ADMIN', 'PHARMACIEN', 'TRANSPORTEUR', 'LABORATOIRE', 'CLINIQUE'].map((r) => DropdownMenuItem(value: r, child: Text(r))).toList(),
                    onChanged: (v) => setState(() => role = v!),
                    decoration: const InputDecoration(labelText: 'Rôle'),
                  ),
                  DropdownButtonFormField<int>(
                    initialValue: selectedOrgId,
                    items: orgs.map<DropdownMenuItem<int>>((org) => DropdownMenuItem(value: org['id'], child: Text(org['nom']))).toList(),
                    onChanged: (v) => setState(() => selectedOrgId = v),
                    decoration: const InputDecoration(labelText: 'Organisation'),
                  ),
                ],
              ),
            ),
            actions: [
              TextButton(onPressed: () => Navigator.pop(context), child: const Text('Annuler')),
              ElevatedButton(
                onPressed: () async {
                  if (selectedOrgId == null) return;
                  final success = await _adminService.createUser(
                    username: usernameCtrl.text,
                    password: passwordCtrl.text,
                    nomComplet: nameCtrl.text,
                    email: '${usernameCtrl.text}@tracemed.com', // Auto-gen email
                    role: role,
                    orgId: selectedOrgId!,
                  );
                  Navigator.pop(context);
                  if (success) {
                    // Refresh main screen state if possible or just show snackbar
                    // For now we rely on the parent FutureBuilder rebuilding if we trigger setState on parent?
                    // Actually we need to callback or just let user pull to refresh.
                    // simpler: just force rebuild of this widget calling setState
                    this.setState(() {}); 
                  }
                },
                child: const Text('Créer'),
              ),
            ],
          );
        }
      ),
    );
  }
}
