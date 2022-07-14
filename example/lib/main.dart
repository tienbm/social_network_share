import 'dart:developer';

import 'package:flutter/material.dart';

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
    SocialNetworkShare.shareLinkToFacebook(
        url: "https://www.google.com",
        quote: "captions",
        hashTag: "#FlutterIsAwsome",
        onSuccess: onSuccess,
        onCancel: onCancel,
        onError: onError);
  }

  void onSuccess(String? postId) {
    log("onSuccess: $postId");
  }

  void onCancel() {
    log("onCancel");
  }

  void onError(String? error) {
    log("onSucconErroress: $error");
  }
}
