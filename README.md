# social_network_share

A plugin for sharing contents to social media such as Facebook, Instargram, Twister

## Introduction 

Works on both platforms Android and iOS
The plugin is gradually improving, towards the goal of sharing content on famous social networks such as facebook, instagram, twister...

## Getting Started
add social_network_share as a dependency in your pubspec.yaml file.

Please use the latest version of package

dependencies:
  ...
  
  social_network_share: ^latest_version
## Requrired Setup
### Android Configuration
#### Add "facebook app id" to the application tag of AndroidManifest.xml:
```xml
        <application>
       ...
       //add this 
        <meta-data
            android:name="com.facebook.sdk.ApplicationId"
            android:value="@string/facebook_app_id" />
            
        <provider
            android:name="com.facebook.FacebookContentProvider{APP_ID}"
            android:authorities="com.facebook.app.FacebookContentProvider[facebook_app_id]"
            android:exported="false" />

		<provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.com.social_network_share"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/filepaths" />
		</provider>	
    </application>
```

#### Create a xml file named `styles.xml` in the `app/src/main/res/values/styles.xml` folder and paste this code in the file :

```xml
<string name="facebook_app_id">xxxxxxxxxx</string>
```
#### Create a xml file named `filepaths.xml` in the `app/src/main/res/xml` folder and paste this code in the file :
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <cache-path name="image" path="/"/>
</paths>
```

#### Read more update at: https://developers.facebook.com/docs/android/getting-started

### IOS Configuration 
#### Facebook
#### Add info into info.plist file.


```
<key>FacebookAppID</key>
<string>your-fb-id</string>
<key>CFBundleURLTypes</key>
	<array>
		<dict>
			<key>CFBundleURLSchemes</key>
			<array>
				<string>fb-your-fb-id</string>
			</array>
		</dict>
	</array>

```
Note-: Make sure you add fb in  at start of your fb Id in CFBundleURLSchemes.

Add below value in url scheme(Refer to example).


```<key>LSApplicationQueriesSchemes</key>
	<array>
		<string>fbauth2</string>
		<string>fbapi</string>
        <string>fbshareextension</string>
		<string>fbapi20130214</string>
		<string>fbapi20130410</string>
		<string>fbapi20130702</string>
		<string>fbapi20131010</string>
		<string>fbapi20131219</string>
		<string>fbapi20140410</string>
		<string>fbapi20140116</string>
		<string>fbapi20150313</string>
		<string>fbapi20150629</string>
		<string>fbapi20160328</string>
		<string>fbauth</string>
		<string>fb-messenger-share-api</string>
		<string>fbauth2</string>
		<string>fbshareextension</string>
		<string>tg</string>
	</array>
```

#### Read more update at: https://developers.facebook.com/docs/ios/getting-started