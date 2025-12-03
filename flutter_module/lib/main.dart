import 'package:flutter/material.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Expense Visualizer',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        brightness: Brightness.dark,
        useMaterial3: true,
      ),
      home: const VisualizerScreen(),
    );
  }
}

class VisualizerScreen extends StatefulWidget {
  const VisualizerScreen({super.key});

  @override
  State<VisualizerScreen> createState() => _VisualizerScreenState();
}

class _VisualizerScreenState extends State<VisualizerScreen> {
  final TextEditingController _foodController = TextEditingController();
  final TextEditingController _transportController = TextEditingController();
  final TextEditingController _funController = TextEditingController();

  double _foodHeight = 0;
  double _transportHeight = 0;
  double _funHeight = 0;

  @override
  void initState() {
    super.initState();
    _foodController.addListener(_updateHeights);
    _transportController.addListener(_updateHeights);
    _funController.addListener(_updateHeights);
  }

  void _updateHeights() {
    setState(() {
      _foodHeight = double.tryParse(_foodController.text) ?? 0;
      _transportHeight = double.tryParse(_transportController.text) ?? 0;
      _funHeight = double.tryParse(_funController.text) ?? 0;
    });
  }

  @override
  void dispose() {
    _foodController.dispose();
    _transportController.dispose();
    _funController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    // Max height for the bars to scale relative to screen or fixed
    const double maxHeight = 300.0;
    // Normalize values to fit in maxHeight if they are large, 
    // or just let them grow (clamped to maxHeight).
    // For simplicity, we'll assume input is roughly 0-300 or scale it.
    // Let's scale: if max value > 300, scale down.
    
    double maxVal = [_foodHeight, _transportHeight, _funHeight].reduce((a, b) => a > b ? a : b);
    double scaleFactor = 1.0;
    if (maxVal > maxHeight) {
      scaleFactor = maxHeight / maxVal;
    } else if (maxVal == 0) {
      scaleFactor = 1.0;
    }

    return Scaffold(
      appBar: AppBar(title: const Text('Interactive Visualizer')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            // Input Section
            Row(
              children: [
                Expanded(child: _buildInput(_foodController, 'Food')),
                const SizedBox(width: 8),
                Expanded(child: _buildInput(_transportController, 'Transport')),
                const SizedBox(width: 8),
                Expanded(child: _buildInput(_funController, 'Fun')),
              ],
            ),
            const SizedBox(height: 40),
            
            // Visualization Section
            Expanded(
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  _buildBar(_foodHeight * scaleFactor, Colors.red, 'Food'),
                  _buildBar(_transportHeight * scaleFactor, Colors.green, 'Transport'),
                  _buildBar(_funHeight * scaleFactor, Colors.blue, 'Fun'),
                ],
              ),
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }

  Widget _buildInput(TextEditingController controller, String label) {
    return TextField(
      controller: controller,
      keyboardType: TextInputType.number,
      decoration: InputDecoration(
        labelText: label,
        border: const OutlineInputBorder(),
      ),
    );
  }

  Widget _buildBar(double height, Color color, String label) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        AnimatedContainer(
          duration: const Duration(milliseconds: 500),
          curve: Curves.easeInOut,
          width: 50,
          height: height,
          decoration: BoxDecoration(
            color: color,
            borderRadius: const BorderRadius.vertical(top: Radius.circular(8)),
          ),
        ),
        const SizedBox(height: 8),
        Text(label, style: const TextStyle(fontWeight: FontWeight.bold)),
      ],
    );
  }
}
