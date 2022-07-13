import 'dart:developer';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:social_network_share/social_network_share.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: ListView(
            children: [
              TextButton(
                child: const Text("Share link to facebook"),
                onPressed: () {
                  shareLinkToFacebook();
                },
              ),
            ],
          ),
        ),
      ),
    );
  }

  void shareLinkToFacebook() async {
    await SocialNetworkShare.shareLinkToFacebook(
        url: "https://www.google.com",
        quote: "captions",
        onSuccess: onSuccess,
        onCancel: onCancel,
        onError: onError);
  }

  Future<void> onSuccess(String postId) {
    log("onSuccess:" + postId);
    return Future.value();
  }

  Future<void> onCancel() {
    log("onCancel");
    return Future.value();
  }

  Future<void> onError(String error) {
    log("onSucconErroress:" + error);

    return Future.value();
  }
}
