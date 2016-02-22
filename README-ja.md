# Meganekko

Oculus Mobile SDKの上に構築された、Gear VR用の3Dレンダリングフレームワークです。

## 使い方

Android Studioで新規プロジェクトを作成します。

### ライブラリの追加

プロジェクトのルートの**build.gradle**にリポジトリURLの追記をします。

```gradle
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.5.0'

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        jcenter()
        maven { url = 'http://ejeinc.github.io/Meganekko/repository' } // この行を追加
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

そして、モジュールの中の**build.gradle**に依存ライブラリとして追加します。

```gradle
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.eje_c:meganekko:2.0.0' // この行を追加
}
```

`allprojects`ブロックをいじりたくない場合は、`repositories`ブロックをモジュールの中の**build.gradle**に記述することもできます。

画面右上に表示されるのSync Nowをクリックします。正しく設定できていればプロジェクトにMeganekkoライブラリーが追加されます。

### Hello World

Meganekkoのアプリケーションは`MeganekkoApp`を継承したクラスがメインになります。

```java
import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;

public class MyApp extends MeganekkoApp {

    public MyApp(Meganekko meganekko) {
        super(meganekko);
    }
}
```

次に、VRのシーンを作成します。シーンはAndroidのViewを記述するのと似た方法で、XMLを使って記述します。XMLファイルはres/xml/scene.xmlに作成します。
ファイル名は任意です。複数用意することもできます。

```xml:res/xml/scene.xml
<scene>
    <!-- 前方 5.0 の距離に、@layout/hello_worldで指定するViewを表示する。 -->
    <object
        layout="@layout/hello_world"
        z="-5.0" />
</scene>
```

`@layout/hello_world`の中身は通常のAndroidアプリで使用するレイアウト指定方法と同じです。

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- Hello World!と白色で表示する -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        android:textColor="#fff" />
</FrameLayout>
```

作成したXMLシーンを利用するために`MyApp`の中で`setSceneFromXML`を呼び出します。

```java
import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;

public class MyApp extends MeganekkoApp {

    public MyApp(Meganekko meganekko) {
        super(meganekko);
        setSceneFromXML(R.xml.scene); // この行を追加
    }
}
```

最後に、AndroidアプリはActivityがないと起動できないので、エントリーポイントとなるActivityを作成します。
Meganekkoアプリでは通常のActivityの代わりに`MeganekkoActivity`を継承します。そして`createMeganekkoApp`メソッドで`MyApp`のインスタンスを作成します。

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

Meganekkoを使うためにはAndroidManifestにも手を加える必要があります。

`android.permission.ACCESS_NETWORK_STATE`パーミッションがOculus SDK Mobileを使うために必要なので、指定します。

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

その他に[Oculus developer document](https://developer.oculus.com/documentation/mobilesdk/latest/concepts/mobile-new-apps-intro/#mobile-native-manifest)で推奨されている属性値があるので、追記します。

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

        <!-- デバッグ時にホームアプリから起動できるようにするために付ける。Oculusストアへリリースする場合は削除する。 -->
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />

            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>

    </activity>
</application>
```

Gear VRのアプリケーションを動作させるにはosigファイルが必要です。osigファイルについては[Oculusデベロッパードキュメント](https://developer.oculus.com/osig/)を読んでください。

osigファイルが用意できたら、`app/src/main/assets`ディレクトリを作成して、その中にosigファイルをコピーします。

ここまでできたらUSBでGalaxy端末を接続してアプリをインストール、実行してください。ここまでの手順を正しくこなしていれば、Hello Worldという白い文字が表示されるはずです。

## Meganekkoをビルドする

Meganekko自体をカスタマイズして利用したい場合は、以下の手順で開発環境を整えてください。

1. [Oculus Mobile SDK 1.0.0.0](https://developer.oculus.com/downloads/)をダウンロードする。
2. Oculus Mobile SDKのZIPファイルを任意の場所に展開する。
3. 環境変数`OVR_SDK_MOBILE`を作成して、Oculus Mobile SDKを展開したディレクトリを指すようにする。
4. Android Studioを起動する。
5. git clone したMeganekkoリポジトリのディレクトリを開く。

**sample/src/main/assets**の中にosigファイルをコピーしてからsampleモジュールをビルド、実行してください。正しく設定できていればサンプルアプリケーションが起動します。

Meganekkoの機能を改善した場合は、Pull Requestしてくれると嬉しいです。
