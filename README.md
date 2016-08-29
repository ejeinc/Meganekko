# Meganekko

3D rendering framework for Gear VR built on Oculus Mobile SDK.

## How to Use

Make new project with Android Studio.

### Add dependency

Add repository URL in project's root **build.gradle**.

```gradle
allprojects {
    repositories {
        jcenter()
        maven { url = 'http://ejeinc.github.io/Meganekko/repository' } // Add this
    }
}
```

Add dependency in module's **build.gradle**.

```gradle
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.eje_c:meganekko:2.3.3' // Add this
}
```

Click "Sync Now".

Note: Since 2.3.0 Meganekko uses multi-view rendering. This feature is not working on prior Android M devices. See also https://developer3.oculus.com/documentation/mobilesdk/latest/concepts/release/

### Hello World

Meganekko app is started from subclass of `MeganekkoApp`.

```java
import com.eje_c.meganekko.MeganekkoApp;

public class MyApp extends MeganekkoApp {

    @Override
    public void init() {
        super.init();
        // Init application here
    }
}
```

Create VR scene with XML. XML file is localed at*res/xml/scene.xml*. File name is arbitrary.

```xml
<scene>
    <object
        layout="@layout/hello_world"
        z="-5.0" />
</scene>
```

`@layout/hello_world` is normal layout file.

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
import com.eje_c.meganekko.MeganekkoApp;

public class MyApp extends MeganekkoApp {

    @Override
    public void init() {
        super.init();
        setSceneFromXML(R.xml.scene);
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


    <meta-data
        android:name="com.eje_c.meganekko.App"
        android:value="com.eje_c.meganekko.sample.MyApp"/> <!-- This is your App class name -->

    <activity
        android:name="com.eje_c.meganekko.gearvr.MeganekkoActivity"
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

`<meta-data android:name="com.samsung.android.vr.application.mode" android:value="vr_only" />` is added by Meganekko so you don't have to put it in AndroidManifest. 

osig file is required to launch Meganekko app in Gear VR. See [Oculus developer document](https://developer.oculus.com/osig/) for more information.

Put your osig file in `app/src/main/assets`.

That's all! Build, Connect Galaxy device to PC, install APK, and launch app. You will see white text "Hello World!".

## Build Meganekko

If you wish customize Meganekko, follow these steps:

1. Launch Android Studio.
2. Open Meganekko repository's root directory.

Put your osig file in **sample/src/main/assets** and build sample module.

Pull request is welcome!
