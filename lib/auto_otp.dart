import 'auto_otp_platform_interface.dart';

class AutoOtp {
  Future<String?> getSmsCode({int codeLength = 5}) async {
    final code = await AutoOtpPlatform.instance.getSmsCode();
    if (code != null) {
      final exp = RegExp(r'(\d{5})');
      return exp.stringMatch(code);
    }

    return null;
  }

  Future<void> removeSmsListener() {
    return AutoOtpPlatform.instance.removeSmsListener();
  }

  Future<String?> getAppSignature() {
    return AutoOtpPlatform.instance.getAppSignature();
  }
}
