# auto_otp

Flutter plugin for Android Sms Retriever Api, automatically read sms from Android device.

## Getting Started

Example usage:
```dart
final _autoOtp = AutoOtp();

final appSignature = await _autoOtp.getAppSignature();
final smsCode = await _autoOtp.getSmsCode(codeLenght: 5);

override onDispose() {
  _autoOtp.removeSmsListener();
  super.onDispose();
}
```

### Benefits
- No additional permission requests are needed
- Eliminates the need for the user to manually type the otp