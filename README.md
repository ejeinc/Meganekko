# Meganekko

VR rendering framework built on Oculus Mobile SDK. Forked from [GearVRf](http://www.gearvrf.org/).

## Setup (local development)

I'm working to migrate new Oculus Mobile SDK. These instructions would be changed. 

1. Download Oculus Mobile SDK 0.6.1.0
2. Extract Oculus Mobile SDK 0.6.1.0
3. Set environment variable `OVR_MOBILE_SDK` to point extracted Oculus Mobile SDK directory
4. Import these projects to Eclipse workspace (*Do not* check to Copy projects into workspace)
  * VrGUI
  * VrLocale
  * VrSound
5. Import Meganekko project to Eclipse workspace
6. Right click Meganekko project and select Properties
7. Navigate to Android section.
8. In Library section, select cross icon of list and click remove.
9. Click Add
10. Select each project and click OK
  * VrGUI
  * VrLocale
  * VrSound
11. Build All

## Use with gradle

Modify your **project's root** build.gradle to add custom repository.

```gradle
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.3.0'

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        jcenter()
        maven { url = 'http://ejeinc.github.io/Meganekko/repository' } // Add this line!
    }
}
```

Modify your **app/build.gradle** to include dependency.

```gradle
apply plugin: 'com.android.application'

android {
    compileSdkVersion 23
    buildToolsVersion "23.0.1"

    defaultConfig {
        applicationId "com.eje_c.test"
        minSdkVersion 21
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.eje_c:meganekko:0.6.1-SNAPSHOT' // Add this line!
}
```
