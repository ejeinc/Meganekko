# Meganekko

VR rendering framework built on Oculus Mobile SDK. Forked from [GearVRf](http://www.gearvrf.org/).

## Using Meganekko

Modify your **app/build.gradle** to include dependency.

```gradle
apply plugin: 'com.android.application'

android {
    ...
}

// 1. Add this block
repositories {
    maven { url = 'http://ejeinc.github.io/Meganekko/repository' }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.eje_c:meganekko:1.0-SNAPSHOT' // 2. Add this line
}
```

Add required permissions, attributes and elements.

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

```xml
<application
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen">

    <meta-data
        android:name="com.samsung.android.vr.application.mode"
        android:value="vr_only" />

    <activity
        android:name=".MainActivity"
        android:configChanges="orientation|screenSize|keyboard|keyboardHidden"
        android:excludeFromRecents="true"
        android:label="@string/app_name"
        android:launchMode="singleTask"
        android:screenOrientation="landscape">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />

            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

## Setup (for local development)

1. Download Oculus Mobile SDK 1.0.0.0
2. Extract Oculus Mobile SDK 1.0.0.0
3. Set environment variable `OVR_SDK_MOBILE` to point extracted Oculus Mobile SDK directory
4. Open Android Studio
5. Open Meganekko directory