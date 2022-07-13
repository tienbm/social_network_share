import Flutter
import UIKit
import FBSDKShareKit
import FBSDKCoreKit

public class SwiftSocialNetworkSharePlugin: NSObject, FlutterPlugin , SharingDelegate{
    var result: FlutterResult?
    var _channel: FlutterMethodChannel
    
    init(fromChannel channel: FlutterMethodChannel) {
        _channel = channel
    }
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        ApplicationDelegate.initialize()
        let channel = FlutterMethodChannel(name: "social_network_share", binaryMessenger: registrar.messenger())
        let instance = SwiftSocialNetworkSharePlugin(fromChannel: channel)
        registrar.addMethodCallDelegate(instance, channel: channel)
        registrar.addApplicationDelegate(instance)
    }
    
    public func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [AnyHashable : Any] = [:]) -> Bool {
        let launchOptionsForFacebook = launchOptions as? [UIApplication.LaunchOptionsKey: Any]
        ApplicationDelegate.shared.application(
            application,
            didFinishLaunchingWithOptions:
                launchOptionsForFacebook
        )
        return true
    }
    
    public func application(_ application: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        return ApplicationDelegate.shared.application(application, open: url, sourceApplication: options[UIApplication.OpenURLOptionsKey.sourceApplication] as? String, annotation: options[UIApplication.OpenURLOptionsKey.annotation])
    }
    
    public func application(_ application: UIApplication, open url: URL, sourceApplication: String, annotation: Any) -> Bool {
        return ApplicationDelegate.shared.application(application, open: url, sourceApplication: sourceApplication, annotation: annotation)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if call.method == "shareLinkToFacebook" {
            if let arguments = call.arguments as? [String:Any] {
                let shareQuote = arguments["quote"] as! String?
                let shareUrl = arguments["url"] as! String?
                let requiredApp = arguments ["requiredApp"] as? Bool ?? false
                shareLinkToFacebook(withQuote: shareQuote, withUrl:shareUrl, withApp:requiredApp)
            }
        }
    }
    
    private func shareLinkToFacebook(withQuote quote: String?, withUrl urlString: String?, withApp requiredApp: Bool) {
        
        if requiredApp {
            let installed = checkAppInstalled(SocialNetworkApp.Facebook)
            if installed {
                shareToFacebook(withQuote:quote,urlString:urlString)
            } else {
                openAppStore(SocialNetworkApp.Facebook)
            }
        }else{
            shareToFacebook(withQuote:quote,urlString:urlString)
        }
    }
    
    private func shareToFacebook(withQuote quote: String?, urlString: String?)
    {
        let shareContent = ShareLinkContent()
        if let url = urlString {
            shareContent.contentURL = URL.init(string: url)!
        }
        if let quoteString = quote {
            shareContent.quote = quoteString
        }
        
        let dialog = ShareDialog(
            viewController: UIApplication.shared.delegate?.window??.rootViewController,
            content: shareContent,
            delegate: self
        )
        dialog.show()
    }
    
    
    func checkAppInstalled(_ app:SocialNetworkApp) -> Bool {
        var appScheme = "";
        switch app {
        case .Facebook:
            appScheme = "fbapi://"
        case .Instagram:
            appScheme = "instagram://app"
        case .Twitter:
            appScheme = "twitter://"
        }
        let appUrl = URL(string: appScheme)!
        if UIApplication.shared.canOpenURL(appUrl) {
            return true;
        }
        return false;
    }
    
    func getAppStoreLink(_ app:SocialNetworkApp) -> String {
        switch app {
        case .Facebook:
            return "itms-apps://itunes.apple.com/us/app/apple-store/id284882215"
        case .Instagram:
            return "itms-apps://itunes.apple.com/us/app/apple-store/id389801252"
        case .Twitter:
            return "itms-apps://itunes.apple.com/us/app/apple-store/id333903271"
        }
    }
    
    func openAppStore(_ app:SocialNetworkApp)  {
        let appStoreLink = getAppStoreLink(app)
        if #available(iOS 10.0, *) {
            if let url = URL(string: appStoreLink) {
                UIApplication.shared.open(url, options: [:]) { _ in
                }
            }
        } else {
            if let url = URL(string: appStoreLink) {
                UIApplication.shared.openURL(url)
            }
        }
    }
    
    public func sharer(_ sharer: Sharing, didCompleteWithResults results: [String : Any]) {
        _channel.invokeMethod("onSuccess", arguments: nil)
    }
    
    public func sharer(_ sharer: Sharing, didFailWithError error: Error) {
        _channel.invokeMethod("onError", arguments: error.localizedDescription)
    }
    
    public func sharerDidCancel(_ sharer: Sharing) {
        _channel.invokeMethod("onCancel", arguments: nil)
    }
    
    @available(iOS 13.0, *)
    func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        guard let url = URLContexts.first?.url else {
            return
        }
        
        ApplicationDelegate.shared.application(
            UIApplication.shared,
            open: url,
            sourceApplication: nil,
            annotation: [UIApplication.OpenURLOptionsKey.annotation]
        )
    }
}

enum SocialNetworkApp {
    case Facebook
    case Instagram
    case Twitter
}
