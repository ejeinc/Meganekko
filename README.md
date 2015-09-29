# Meganekko

VR rendering framework built on Oculus Mobile SDK. Forked from [GearVRf](http://www.gearvrf.org/).

The [MeganekkoSample](https://github.com/ejeinc/MeganekkoSample) project is simple-sample for Android Studio.

## Using Meganekko

Modify your **project's root** build.gradle to add custom repository.

```gradle
allprojects {
    repositories {
        jcenter()
        maven { url = 'http://ejeinc.github.io/Meganekko/repository' } // Add this line!
    }
}
```

Modify your **app/build.gradle** to include dependency.

```gradle
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.eje_c:meganekko:0.6.1-SNAPSHOT' // Add this line!
}
```

and build it.

## Setup (for local development)

*Note: Migrating to newer SDK is in progress. These instructions would be changed.*

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
