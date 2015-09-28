# Meganekko

VR rendering framework built on Oculus Mobile SDK. Forked from [GearVRf](http://www.gearvrf.org/).

## Setup

I'm working to migrate new Oculus Mobile SDK. These instructions would be changed. 

1. Download Oculus Mobile SDK 0.6.1.0
2. Extract Oculus Mobile SDK 0.6.1.0
3. Set environment variable `OVR_MOBILE_SDK` to point extracted Oculus Mobile SDK directory
4. Import these projects to Eclipse workspace (*Do not* check to Copy projects into workspace)
  * VrApi_Prebuilt
  * VrAppFramework_Prebuilt
  * VrGUI
  * VrLocale
  * VrModel
  * VrSound
5. Import Meganekko project to Eclipse workspace
6. Right click Meganekko project and select Properties
7. Navigate to Android section.
8. In Library section, select cross icon of list and click remove.
9. Click Add
10. Select each project and click OK
  * VrAppFramework_Prebuilt
  * VrGUI
  * VrLocale
  * VrSound
11. Build All