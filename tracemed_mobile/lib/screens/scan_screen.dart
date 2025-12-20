import 'package:flutter/material.dart';
import 'package:mobile_scanner/mobile_scanner.dart';
import 'result_screen.dart';
import 'administer_screen.dart';
import 'incident_report_screen.dart';

enum ScanMode { READ, ADMINISTER, INCIDENT }

class ScanScreen extends StatelessWidget {
  final ScanMode mode;

  const ScanScreen({super.key, this.mode = ScanMode.READ});

  @override
  Widget build(BuildContext context) {
    String title = 'Scan Code QR';
    if (mode == ScanMode.ADMINISTER) title = 'Scan pour Administration';
    if (mode == ScanMode.INCIDENT) title = 'Scan pour Incident';

    return Scaffold(
      appBar: AppBar(title: Text(title), backgroundColor: _getAppBarColor()),
      body: MobileScanner(
        onDetect: (capture) {
          final List<Barcode> barcodes = capture.barcodes;
          for (final barcode in barcodes) {
            if (barcode.rawValue != null) {
              final String url = barcode.rawValue!;
              final String id = url.split('/').last;

              if (ModalRoute.of(context)?.isCurrent ?? false) {
                  _handleScan(context, id);
              }
            }
          }
        },
      ),
      floatingActionButton: _buildSimuloButtons(context),
    );
  }

  Color _getAppBarColor() {
    switch (mode) {
      case ScanMode.ADMINISTER: return Colors.blueAccent;
      case ScanMode.INCIDENT: return Colors.orange;
      default: return Colors.teal;
    }
  }

  void _handleScan(BuildContext context, String id) {
    switch (mode) {
      case ScanMode.ADMINISTER:
        Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => AdministerScreen(idColis: id)));
        break;
      case ScanMode.INCIDENT:
        Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => IncidentReportScreen(idColis: id)));
        break;
      case ScanMode.READ:
      default:
        Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => ResultScreen(idColis: id)));
        break;
    }
  }

  Widget _buildSimuloButtons(BuildContext context) {
    return Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
            FloatingActionButton.extended(
              heroTag: 'sim_ok',
              onPressed: () => _handleScan(context, 'TEST-VALID'),
              label: const Text('Simuler OK'),
              icon: const Icon(Icons.check_circle),
              backgroundColor: Colors.green,
            ),
            const SizedBox(height: 10),
            if (mode == ScanMode.READ) ...[
              FloatingActionButton.extended(
                heroTag: 'sim_ko',
                onPressed: () => _handleScan(context, 'TEST-CORRUPTED'),
                label: const Text('Simuler KO'),
                icon: const Icon(Icons.dangerous),
                backgroundColor: Colors.red,
              ),
              const SizedBox(height: 10),
            ],
            FloatingActionButton.extended(
              heroTag: 'sim_expired',
              onPressed: () => _handleScan(context, 'TEST-EXPIRED'),
              label: const Text('Simuler Périmé'),
              icon: const Icon(Icons.history),
              backgroundColor: Colors.orange,
            ),
             const SizedBox(height: 10),
             // Real Integration Test
             FloatingActionButton.extended(
              heroTag: 'sim_real',
              onPressed: () => _handleScan(context, 'COLIS-TEST-2'),
              label: const Text('Test Réel'),
              icon: const Icon(Icons.wifi_tethering),
              backgroundColor: _getAppBarColor(),
            ),
        ],
      );
  }
}
