package com.example.social_network_share

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.annotation.NonNull
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

/** SocialNetworkSharePlugin */
class SocialNetworkSharePlugin : FlutterPlugin, MethodCallHandler, ActivityAware,
    PluginRegistry.ActivityResultListener {
    private lateinit var activity: Activity
    private lateinit var channel: MethodChannel
    private lateinit var callbackManager: CallbackManager

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "shareLinkToFacebook") {
            var quote: String? = call.argument("quote")
            var url: String? = call.argument("url")
            var requiredApp: Boolean? = call.argument("requiredApp")
            var required = requiredApp ?: true;
            shareLinkToFacebook(url, quote, required, result)
        } else {
            result.notImplemented()
        }
    }

    private fun shareLinkToFacebook(
        url: String?,
        quote: String?,
        requiredAppInstalled: Boolean,
        result: MethodChannel.Result
    ) {
        if (requiredAppInstalled) {
            val pm = activity.packageManager
            val packageName = getSocialAppPackage(SocialNetworkApp.Facebook)
            try {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
                shareToFacebook(quote, url, result)
            } catch (e: PackageManager.NameNotFoundException) {
                openPlayStore(SocialNetworkApp.Facebook)
                result.success(false)
            }
        } else {
            shareToFacebook(quote, url, result)
        }
    }

    private fun shareToFacebook(quote: String?, url: String?, result: MethodChannel.Result) {
        val uri = Uri.parse(url)
        val content: ShareLinkContent =
            ShareLinkContent.Builder().setContentUrl(uri).setQuote(quote).build()
        val shareDialog = ShareDialog(activity)
        shareDialog.registerCallback(
            callbackManager,
            object : FacebookCallback<Sharer.Result> {
                override fun onSuccess(result: Sharer.Result) {
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

    private fun openPlayStore(app: SocialNetworkApp) {
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

    private fun getSocialAppPackage(app: SocialNetworkApp): String {
        return when (app) {
            SocialNetworkApp.Facebook -> "com.facebook.katana"
            SocialNetworkApp.Instagram -> "com.instagram.android"
            SocialNetworkApp.Twitter -> "com.twitter.android"
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
        TODO("Not yet implemented")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        binding.addActivityResultListener(this)
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        TODO("Not yet implemented")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}

enum class SocialNetworkApp {
    Facebook, Instagram, Twitter
}

