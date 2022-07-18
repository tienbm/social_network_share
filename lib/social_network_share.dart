import 'dart:async';

import 'package:flutter/services.dart';

typedef OnCancel = Function();
typedef OnError = Function(String? error);
typedef OnSuccess = Function(String? postId);

class SocialNetworkShare {
  static const MethodChannel _channel = MethodChannel('social_network_share');

  static Future<bool> shareLinkToFacebook({
    String? quote,
    String? url,
    String? hashTag,
    bool requiredApp = false,
    OnSuccess? onSuccess,
    OnCancel? onCancel,
    OnError? onError,
  }) async {
    final Map<String, dynamic> params = <String, dynamic>{
      "quote": quote,
      "url": url,
      "hashTag": hashTag,
      "requiredApp": requiredApp
    };

    _channel.setMethodCallHandler((call) {
      switch (call.method) {
        case "onSuccess":
          return onSuccess?.call(call.arguments);
        case "onCancel":
          return onCancel?.call();
        case "onError":
          return onError?.call(call.arguments);
        default:
          throw UnsupportedError("Unknown method called");
      }
    });
    return await _channel.invokeMethod('shareLinkToFacebook', params);
  }

  static Future<bool> sharePhotosToFacebook({
    required List<String> paths,
    bool requiredApp = false,
    OnSuccess? onSuccess,
    OnCancel? onCancel,
    OnError? onError,
  }) async {
    final Map<String, dynamic> params = <String, dynamic>{
      "paths": paths,
      "requiredApp": requiredApp
    };

    _channel.setMethodCallHandler((call) {
      switch (call.method) {
        case "onSuccess":
          return onSuccess?.call(call.arguments);
        case "onCancel":
          return onCancel?.call();
        case "onError":
          return onError?.call(call.arguments);
        default:
          throw UnsupportedError("Unknown method called");
      }
    });
    return await _channel.invokeMethod('sharePhotosToFacebook', params);
  }
}
