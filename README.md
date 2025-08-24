# sms_reader

A Flutter plugin to read SMS messages from Android devices, with **detailed fields** and **runtime permission handling**.

---

## Features

- Read messages from **Inbox (Android only)  
- Returns a **Message class** with typed fields:  
  - `id`, `address`, `body`, `date`, `threadId`, `type`, `read`, `serviceCenter`, `subject`, `locked`  
- Automatically requests **runtime SMS permission**  
- Easy to use in Flutter apps  

> **Note:** iOS does not allow reading SMS, so this plugin works **only on Android**.

---

## Usage

``` dart
import 'package:sms_reader/sms_reader.dart';
List<Message> messages = await SmsReader.getInboxSms();
```

## Message Class

``` dart
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
}
```

## Installation

Add this to your `pubspec.yaml`:

```yaml
dependencies:
  sms_reader: ^0.1.0
