import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'auto_otp_platform_interface.dart';

/// An implementation of [AutoOtpPlatform] that uses method channels.
class MethodChannelAutoOtp extends AutoOtpPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('com.technikb.auto_otp');

  @override
  Future<String?> getSmsCode() async {
    final version = await methodChannel.invokeMethod<String>('getSmsCode');
    return version;
  }

  @override
  Future<void> removeSmsListener() async {
    await methodChannel.invokeMethod<void>('removeSmsListener');
  }

  @override
  Future<String?> getAppSignature() async {
    final signature = await methodChannel.invokeMethod<String>(
      'getAppSignature',
    );
    return signature;
  }
}
