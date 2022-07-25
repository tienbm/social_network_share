package com.example.social_network_share

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.ShareHashtag
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry
import java.io.File

/** SocialNetworkSharePlugin */
class SocialNetworkSharePlugin : FlutterPlugin, MethodCallHandler, ActivityAware,
    PluginRegistry.ActivityResultListener {
    private lateinit var activity: Activity
    private lateinit var channel: MethodChannel
    private lateinit var callbackManager: CallbackManager

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        if (call.method == "shareLinkToFacebook") {
            val quote: String? = call.argument("quote")
            val url: String? = call.argument("url")
            val hashTag: String? = call.argument("hashTag")
            val requiredApp: Boolean? = call.argument("requiredApp")
            val required = requiredApp ?: true
            shareLinkToFacebook(url, quote, hashTag, required, result)
        } else if (call.method == "sharePhotosToFacebook") {
            val paths: List<String> = call.argument("paths")!!
            val requiredApp: Boolean? = call.argument("requiredApp")
            val required = requiredApp ?: true
            sharePhotosToFacebook(paths, required, result)
        } else {
            result.notImplemented()
        }
    }


    private fun shareLinkToFacebook(
        url: String?,
        quote: String?, hashTag: String?,
        requiredAppInstalled: Boolean,
        result: MethodChannel.Result
    ) {
        if (requiredAppInstalled) {
            val pm = activity.packageManager
            val packageName = getSocialAppPackage(SocialApp.Facebook)
            try {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
                shareLinkToFB(quote, url, hashTag, result)
            } catch (e: PackageManager.NameNotFoundException) {
                openPlayStore(SocialApp.Facebook)
                result.success(false)
            }
        } else {
            shareLinkToFB(quote, url, hashTag, result)
        }
    }

    private fun shareLinkToFB(
        quote: String?,
        url: String?,
        hashTag: String?,
        result: MethodChannel.Result
    ) {
        val uri = Uri.parse(url)
        val shareHashtag = ShareHashtag.Builder().setHashtag(hashTag).build()
        val content: ShareLinkContent =
            ShareLinkContent.Builder().setContentUrl(uri).setQuote(quote)
                .setShareHashtag(shareHashtag).build()
        val shareDialog = ShareDialog(activity)
        shareDialog.registerCallback(
            callbackManager,
            object : FacebookCallback<Sharer.Result> {
                override fun onSuccess(result: Sharer.Result) {
                    print("onSuccess" + result.postId)
                    channel.invokeMethod("onSuccess", result.postId)
                }

                override fun onCancel() {
                    channel.invokeMethod("onCancel", null)
                }

                override fun onError(error: FacebookException) {
                    channel.invokeMethod("onError", error.message)
                }
            })
        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            shareDialog.show(content)
            result.success(true)
        } else {
            result.success(false)
        }
    }

    private fun sharePhotosToFacebook(
        paths: List<String>,
        requiredAppInstalled: Boolean,
        result: MethodChannel.Result
    ) {
        if (requiredAppInstalled) {
            val pm = activity.packageManager
            val packageName = getSocialAppPackage(SocialApp.Facebook)
            try {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
                sharePhotosToFB(paths, result)
            } catch (e: PackageManager.NameNotFoundException) {
                openPlayStore(SocialApp.Facebook)
                result.success(false)
            }
        } else {
            sharePhotosToFB(paths, result)
        }
    }

    private fun sharePhotosToFB(paths: List<String>, result: MethodChannel.Result) {
        val content: SharePhotoContent
        var photos = arrayListOf<SharePhoto>()
        for (path in paths) {
            val media = File(path)
            val uri = FileProvider.getUriForFile(
                activity, activity.packageName + ".com.social_network_share",
                media
            )
            val photo: SharePhoto = SharePhoto.Builder().setImageUrl(uri).build()
            photos.add(photo)
        }
        content = SharePhotoContent.Builder().setPhotos(photos).build()

        val shareDialog = ShareDialog(activity)
        shareDialog.registerCallback(
            callbackManager,
            object : FacebookCallback<Sharer.Result> {
                override fun onSuccess(result: Sharer.Result) {
                    print("onSuccess" + result.postId)
                    channel.invokeMethod("onSuccess", result.postId)
                }

                override fun onCancel() {
                    channel.invokeMethod("onCancel", null)
                }

                override fun onError(error: FacebookException) {
                    channel.invokeMethod("onError", error.message)
                }
            })
        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            shareDialog.show(content)
            result.success(true)
        } else {
            result.success(false)
        }
    }

    private fun openPlayStore(app: SocialApp) {
        val packageName = getSocialAppPackage(app)
        try {
            val playStoreUri = Uri.parse("market://details?id=$packageName")
            val intent = Intent(Intent.ACTION_VIEW, playStoreUri)
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val playStoreUri =
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            val intent = Intent(Intent.ACTION_VIEW, playStoreUri)
            activity.startActivity(intent)
        }
    }

    private fun getSocialAppPackage(app: SocialApp): String {
        return when (app) {
            SocialApp.Facebook -> "com.facebook.katana"
            SocialApp.Instagram -> "com.instagram.android"
            SocialApp.Twitter -> "com.twitter.android"
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "social_network_share")
        channel.setMethodCallHandler(this)
        callbackManager = CallbackManager.Factory.create()
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        binding.addActivityResultListener(this)
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        binding.addActivityResultListener(this)
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}

enum class SocialApp {
    Facebook, Instagram, Twitter
}

