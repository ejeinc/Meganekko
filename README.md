# Meganekko

3D rendering framework for Gear VR built on Oculus Mobile SDK.

## How to Use

Make new project with Android Studio.

### Add dependency

Add repository URL in project's root **build.gradle**.

```gradle
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.0.0-beta5'

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        jcenter()
        maven { url = 'http://ejeinc.github.io/Meganekko/repository' } // Add this line
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

Add dependency in module's **build.gradle**.

```gradle
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.eje_c:meganekko:2.0.0' // Add this line
}
```

(You can put `repositories` block in module's **build.gradle**)

Click "Sync Now".

### Hello World

Meganekko app is started from subclass of `MeganekkoApp`.

```java
import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;

public class MyApp extends MeganekkoApp {

    public MyApp(Meganekko meganekko) {
        super(meganekko);
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
import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;

public class MyApp extends MeganekkoApp {

    public MyApp(Meganekko meganekko) {
        super(meganekko);
        setSceneFromXML(R.xml.scene); // Add this line
    }
}
```

Finally, create `MainActivity` extends `MeganekkoActivity` and implement abstract `createMeganekkoApp` method.

```java
import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.gearvr.MeganekkoActivity;

public class MainActivity extends MeganekkoActivity {
    @Override
    public MeganekkoApp createMeganekkoApp(Meganekko meganekko) {
        return new MyApp(meganekko);
    }
}
```

You have to modify AndroidManifest.

Add `android.permission.ACCESS_NETWORK_STATE` permission.

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

And other recommended attributes and elements. See [Oculus developer document](https://developer.oculus.com/documentation/mobilesdk/latest/concepts/mobile-new-apps-intro/#mobile-native-manifest).

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

## Build Meganekko

If you wish customize Meganekko, follow these steps:

1. Download [Oculus Mobile SDK 1.0.0.0](https://developer.oculus.com/downloads/).
2. Extract Oculus Mobile SDK.
3. Create environment variable `OVR_SDK_MOBILE` and point it to Oculus Mobile SDK directory.
4. Launch Android Studio.
5. Open Meganekko repository's root directory.

Put your osig file in **sample/src/main/assets** and build sample module.

Pull request is welcome!
