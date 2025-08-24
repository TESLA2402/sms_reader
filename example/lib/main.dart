// ignore_for_file: library_private_types_in_public_api

import 'package:flutter/material.dart';
import 'package:sms_reader/sms_reader.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) => MaterialApp(home: SmsPage());
}

class SmsPage extends StatefulWidget {
  const SmsPage({super.key});

  @override
  _SmsPageState createState() => _SmsPageState();
}

class _SmsPageState extends State<SmsPage> {
  List<Message> messages = [];

  void loadSms() async {
    try {
      List<Message> messages = await SmsReader.getInboxSms();
      setState(() {
        this.messages = messages;
      });
    } catch (e) {
      print(e);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('SMS Reader Example')),
      body: Column(
        children: [
          ElevatedButton(onPressed: loadSms, child: const Text('Load SMS')),
          Expanded(
            child: ListView.builder(
              itemCount: messages.length,
              itemBuilder: (_, i) => ListTile(
                title: Text(messages[i].address),
                subtitle: Text(messages[i].body),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
