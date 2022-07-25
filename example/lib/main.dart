import 'dart:developer';

import 'package:flutter/material.dart';

import 'package:social_network_share/social_network_share.dart';
import 'package:image_picker/image_picker.dart';

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
              TextButton(
                child: const Text("Share photos to facebook"),
                onPressed: () {
                  sharePhotosToFacebook();
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

  void sharePhotosToFacebook() async {
    final ImagePicker _picker = ImagePicker();
    List<XFile>? images = await _picker.pickMultiImage();
    List<String> paths = [];
    if (images != null) {
      for (var item in images) {
        paths.add(item.path);
      }
      log(paths.join('|'));
      final result =
          await SocialNetworkShare.sharePhotosToFacebook(paths: paths);
      log(result.toString());
    } else {
      log("Something Wrong");
    }
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
