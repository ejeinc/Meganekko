# Meganekko

Gear VR Application Framework.

## How to Use

Make new project with Android Studio.

Add dependency in module's **build.gradle**.

```gradle
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'org.meganekkovr:meganekko:3.0.1'
}
```

Click "Sync Now".

Note: Meganekko uses multi-view rendering. This feature is not working on prior Android M devices. See also https://developer3.oculus.com/documentation/mobilesdk/latest/concepts/release/

### Hello World

Meganekko app is started from subclass of `MeganekkoApp`.

```java
import org.meganekkovr.MeganekkoApp;

public class MyApp extends MeganekkoApp {

    @Override
    public void init() {
        super.init();
        // Init application here
    }
}
```

Create VR scene with XML. XML file can be localed in asset directory. For example: *assets/scene.xml*.

```xml
<scene>
    <view src="@layout/hello_world" position="0 0 -5" />
</scene>
```

`@layout/hello_world` is normal layout file put in *res/layout/hello_world.xml*.

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        android:textColor="#fff" />
</FrameLayout>
```

Call `setSceneFromXML` in `MyApp`.

```java
public class MyApp extends MeganekkoApp {

    @Override
    public void init() {
        super.init();
        setSceneFromXmlAsset("scene.xml"); // Set scene
    }
}
```

You have to modify AndroidManifest.
Add recommended attributes and elements. See [Oculus developer document](https://developer.oculus.com/documentation/mobilesdk/latest/concepts/mobile-new-apps-intro/#mobile-native-manifest).

```xml
<application
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen">

    <!-- You have to declare to require Gear VR -->
    <meta-data
        android:name="com.samsung.android.vr.application.mode"
        android:value="vr_only" />

    <!-- Declare your App class extends MeganekkoApp -->
    <meta-data
        android:name="org.meganekkovr.App"
        android:value="org.meganekkovr.sample.MyApp"/>

    <activity
        android:name="org.meganekkovr.GearVRActivity"
        android:configChanges="orientation|screenSize|keyboard|keyboardHidden"
        android:excludeFromRecents="true"
        android:label="@string/app_name"
        android:launchMode="singleTask"
        android:screenOrientation="landscape">

        <!-- Only in debugging. Remove this when upload to Oculus Store. -->
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />

            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>

    </activity>
</application>
```

osig file is required to launch Meganekko app in Gear VR. See [Oculus developer document](https://developer.oculus.com/osig/) for more information.

Put your osig file in `app/src/main/assets`.

That's all! Build, Connect Galaxy device to PC, install APK, and launch app. You will see white text "Hello World!".
