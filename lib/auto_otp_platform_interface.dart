import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'auto_otp_method_channel.dart';

abstract class AutoOtpPlatform extends PlatformInterface {
  /// Constructs a AutoOtpPlatform.
  AutoOtpPlatform() : super(token: _token);

  static final Object _token = Object();

  static AutoOtpPlatform _instance = MethodChannelAutoOtp();

  /// The default instance of [AutoOtpPlatform] to use.
  ///
  /// Defaults to [MethodChannelAutoOtp].
  static AutoOtpPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [AutoOtpPlatform] when
  /// they register themselves.
  static set instance(AutoOtpPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getSmsCode() {
    throw UnimplementedError('smsCode() has not been implemented.');
  }

  Future<void> removeSmsListener() {
    throw UnimplementedError('removeSmsListener() has not been implemented.');
  }

  Future<String?> getAppSignature() {
    throw UnimplementedError('appSignature() has not been implemented.');
  }
}
