import 'package:flutter/material.dart';
import 'scan_screen.dart';
import 'create_transfer_screen.dart';
import 'pending_transfer_screen.dart';
import 'admin_dashboard_screen.dart';
import 'laboratory_dashboard_screen.dart';
import '../services/auth_service.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  String? _role;
  final _authService = AuthService();

  @override
  void initState() {
    super.initState();
    _checkRole();
  }

  Future<void> _checkRole() async {
    final role = await _authService.getRole();
    setState(() {
      _role = role;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey.shade100,
      body: Column(
        children: [
          // Custom Header
          Container(
            padding: const EdgeInsets.only(top: 60, left: 24, right: 24, bottom: 30),
            decoration: const BoxDecoration(
              color: Color(0xFF009688),
              borderRadius: BorderRadius.only(
                bottomLeft: Radius.circular(30),
                bottomRight: Radius.circular(30),
              ),
            ),
            child: Row(
              children: [
                const CircleAvatar(
                  radius: 30,
                  backgroundColor: Colors.white,
                  child: Icon(Icons.person, size: 35, color: Color(0xFF009688)),
                ),
                const SizedBox(width: 15),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Bonjour, ${_role ?? "Invité"}',
                      style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const Text(
                      'Bienvenue sur TraceMed',
                      style: TextStyle(color: Colors.white70, fontSize: 14),
                    ),
                  ],
                ),
                const Spacer(),
                IconButton(
                  onPressed: () {
                     // Logout Logic
                     Navigator.of(context).pop(); 
                  }, 
                  icon: const Icon(Icons.logout, color: Colors.white)
                ),
              ],
            ),
          ),
          
          Expanded(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(24),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    'Tableau de Bord', 
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.black54)
                  ),
                  const SizedBox(height: 15),
                  
                  // Dynamic Grid based on Role
                  GridView.count(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    crossAxisCount: 2,
                    crossAxisSpacing: 15,
                    mainAxisSpacing: 15,
                    children: _buildCardsForRole(),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  List<Widget> _buildCardsForRole() {
    List<Widget> cards = [];

    // --- COMMON: Scanner ---
    cards.add(_buildDashboardCard(
      context,
      title: 'Scanner',
      icon: Icons.qr_code_scanner,
      color: Colors.blueAccent,
      onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const ScanScreen(mode: ScanMode.READ))),
    ));

    // --- ADMIN ---
    if (_role == 'ADMIN') {
      cards.add(_buildDashboardCard(
        context,
        title: 'Admin Dashboard',
        icon: Icons.admin_panel_settings,
        color: Colors.purpleAccent,
        onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const AdminDashboardScreen())),
      ));
      cards.add(_buildDashboardCard(
        context,
        title: 'Labo Dashboard',
        icon: Icons.science,
        color: Colors.indigoAccent,
        onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const LaboratoryDashboardScreen())),
      ));
    }

    // --- LABORATOIRE ---
    if (_role == 'LABORATOIRE' || _role == 'ADMIN') {
        // Admin also gets lab access for demo, but specific lab cards for Lab role
        if (_role == 'LABORATOIRE') {
           cards.add(_buildDashboardCard(
            context,
            title: 'Labo Dashboard',
            icon: Icons.science,
            color: Colors.indigoAccent,
            onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const LaboratoryDashboardScreen())),
          ));
        }
    }

    // --- CLINIQUE / PHARMACIEN ---
    if (_role == 'CLINIQUE' || _role == 'PHARMACIEN' || _role == 'ADMIN') {
       cards.add(_buildDashboardCard(
        context,
        title: 'Réceptions / Stock',
        icon: Icons.move_to_inbox,
        color: Colors.orange,
        onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const PendingTransferScreen())),
      ));
      cards.add(_buildDashboardCard(
        context,
        title: 'Administrer Patient',
        icon: Icons.medical_services,
        color: Colors.teal,
        onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const ScanScreen(mode: ScanMode.ADMINISTER))),
      ));
      cards.add(_buildDashboardCard(
        context,
        title: 'Signaler Incident',
        icon: Icons.warning_amber,
        color: Colors.redAccent,
        onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const ScanScreen(mode: ScanMode.INCIDENT))),
      ));
      cards.add(_buildDashboardCard(
        context,
        title: 'Nouvel Envoi',
        icon: Icons.send,
        color: Colors.green,
        onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const CreateTransferScreen())),
      ));
    }

    // --- TRANSPORTEUR ---
    if (_role == 'TRANSPORTEUR' || _role == 'ADMIN') {
        // Only show if not already shown (Admin logic duplications avoided by list add)
        // But for Transporter specific:
       cards.add(_buildDashboardCard(
        context,
        title: 'Mes Livraisons',
        icon: Icons.local_shipping,
        color: Colors.brown,
        onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const PendingTransferScreen())),
      ));
      if (_role != 'ADMIN') { // Admin already has "Nouvel Envoi" from Clinique logic, avoid duplicate if role admin
         cards.add(_buildDashboardCard(
          context,
          title: 'Nouvelle Livraison',
          icon: Icons.add_road,
          color: Colors.deepOrange,
          onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const CreateTransferScreen())),
        ));
      }
    }

    return cards;
  }

  Widget _buildDashboardCard(BuildContext context, {required String title, required IconData icon, required Color color, required VoidCallback onTap}) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(20),
          boxShadow: [
            BoxShadow(color: color.withOpacity(0.1), blurRadius: 10, offset: const Offset(0, 4)),
          ],
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              padding: const EdgeInsets.all(15),
              decoration: BoxDecoration(
                color: color.withOpacity(0.1),
                shape: BoxShape.circle,
              ),
              child: Icon(icon, color: color, size: 30),
            ),
            const SizedBox(height: 15),
            Text(
              title,
              style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }
}
