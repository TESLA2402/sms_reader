import 'package:flutter/services.dart';

class SmsReader {
  static const _channel = MethodChannel('sms_reader_channel');

  /// Returns a list of SMS messages from inbox (Android only)
  static Future<List<Message>> getInboxSms() async {
    try {
      final List<dynamic> messages = await _channel.invokeMethod('getInboxSms');
      return messages.map((msg) => Message.fromMap(msg)).toList();
    } on PlatformException catch (e) {
      throw Exception('Failed to get SMS: ${e.message}');
    }
  }
}

class Message {
  final String id;
  final String address;
  final String body;
  final String date; // Timestamp as String
  final int threadId;
  final int type; // 1 = inbox, 2 = sent, etc.
  final bool read;
  final String? serviceCenter;
  final String? subject;
  final bool? locked;

  Message({
    required this.id,
    required this.address,
    required this.body,
    required this.date,
    required this.threadId,
    required this.type,
    required this.read,
    this.serviceCenter,
    this.subject,
    this.locked,
  });

  factory Message.fromMap(Map<dynamic, dynamic> map) {
    return Message(
      id: map['id'] ?? '',
      address: map['address'] ?? '',
      body: map['body'] ?? '',
      date: map['date'] ?? '',
      threadId: map['threadId'] ?? 0,
      type: map['type'] ?? 1,
      read: (map['read'] ?? 0) == 1,
      serviceCenter: map['serviceCenter'],
      subject: map['subject'],
      locked: (map['locked'] != null) ? map['locked'] == 1 : null,
    );
  }
}
