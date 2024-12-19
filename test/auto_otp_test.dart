import 'package:auto_otp/auto_otp.dart';
import 'package:auto_otp/auto_otp_method_channel.dart';
import 'package:auto_otp/auto_otp_platform_interface.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockAutoOtpPlatform
    with MockPlatformInterfaceMixin
    implements AutoOtpPlatform {
  @override
  Future<String?> getSmsCode() => Future.value('12345');

  @override
  Future<String?> getAppSignature() => Future.value('12345');

  @override
  Future<void> removeSmsListener() => Future.value(null);
}

void main() {
  final AutoOtpPlatform initialPlatform = AutoOtpPlatform.instance;

  test('$MethodChannelAutoOtp is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelAutoOtp>());
  });

  test('getSms', () async {
    AutoOtp autoOtpPlugin = AutoOtp();
    MockAutoOtpPlatform fakePlatform = MockAutoOtpPlatform();
    AutoOtpPlatform.instance = fakePlatform;

    expect(await autoOtpPlugin.getSmsCode(), '12345');
  });

  test('getAppSignature', () async {
    AutoOtp autoOtpPlugin = AutoOtp();
    MockAutoOtpPlatform fakePlatform = MockAutoOtpPlatform();
    AutoOtpPlatform.instance = fakePlatform;

    expect(await autoOtpPlugin.getAppSignature(), '12345');
  });

  test('removeSmsListener', () async {
    AutoOtp autoOtpPlugin = AutoOtp();
    MockAutoOtpPlatform fakePlatform = MockAutoOtpPlatform();
    AutoOtpPlatform.instance = fakePlatform;
    await autoOtpPlugin.removeSmsListener();
  });
}
