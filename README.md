# Meganekko

VR rendering framework built on Oculus Mobile SDK. Forked from [GearVRf](http://www.gearvrf.org/).

## Using Meganekko

**Version 1.0 is not yet available with this steps. Please see below.**

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
    compile 'com.eje_c:meganekko:0.6.2-SNAPSHOT' // 2. Add this line
}
```

### For 1.0 (or local development)

1. Download Oculus Mobile SDK 1.0.0.0
2. Extract Oculus Mobile SDK 1.0.0.0
3. Set environment variable `OVR_SDK_MOBILE` to point extracted Oculus Mobile SDK directory
4. Open Android Studio
5. Open Meganekko directory

Run sample module. (confirmation for correct setup)

To create your app:

1. File->New->New Module...
2. Select *Phone & Tablt Module*
3. Next
4. Enter your app name, module name and package name
5. Next
6. Select *Empty Activity*
7. Enter *Activiy Name* and **uncheck** *Genarate Layout File*
8. Finish
9. Open `yourappmodule/build.gradle`
10. Edit `dependencies` section to include `compile project(':library')`

## Common setup

### MeganekkoActivity

You have to extend `MeganekkoActivity`.

```java
import com.eje_c.meganekko.MeganekkoActivity;

public class MainActivity extends MeganekkoActivity {

    @Override
    protected void oneTimeInit(VrContext context) {
        // Do something on initial setup
        // In most cases create scene here
    }

    @Override
    protected void frame() {
        // Do something on frame update
    }
}
```

### AndroidManifest.xml

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

### osig

You must put your osig file to debug Gear VR app. See also https://developer.oculus.com/osig/

1. Create 'YourAppModuleDirectory/src/debug/assets' directory.
2. Put your osig file into 'YourAppModuleDirectory/src/debug/assets' directory.
