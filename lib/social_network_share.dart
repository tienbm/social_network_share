import 'dart:async';

import 'package:flutter/services.dart';

typedef OnCancel = Future<void> Function();
typedef OnError = Future<void> Function(String error);
typedef OnSuccess = Future<void> Function(String postId);

class SocialNetworkShare {
  static const MethodChannel _channel = MethodChannel('social_network_share');

  static Future<bool> shareLinkToFacebook({
    String? quote,
    String? url,
    bool requiredApp = false,
    OnSuccess? onSuccess,
    OnCancel? onCancel,
    OnError? onError,
  }) async {
    final Map<String, dynamic> params = <String, dynamic>{
      "quote": quote,
      "url": url,
      "requiredApp": requiredApp
    };

    _channel.setMethodCallHandler((call) {
      switch (call.method) {
        case "onSuccess":
          return onSuccess?.call(call.arguments) ?? Future.value();
        case "onCancel":
          return onCancel?.call() ?? Future.value();
        case "onError":
          return onError?.call(call.arguments) ?? Future.value();
        default:
          throw UnsupportedError("Unknown method called");
      }
    });
    return await _channel.invokeMethod('shareLinkToFacebook', params);
  }
}
