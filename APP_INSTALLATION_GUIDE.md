# App Installation Guide

Build the debug APK:

```powershell
.\gradlew.bat assembleDebug
```

APK output:

```text
app\build\outputs\apk\debug\app-debug.apk
```

Install with Android Studio or with `adb install` if `adb` is available in the SDK platform tools.
