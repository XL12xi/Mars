# Setup

Use Android Studio to open the repository root:

```text
D:\XueXi\JAVA\project\Mars_remote
```

Required local configuration:

- Android SDK path in `local.properties`.
- JDK/JBR compatible with the Android Gradle plugin.
- Gradle wrapper from this repository.

Command-line build:

```powershell
$env:JAVA_HOME='D:\Tool\Jetbrain\IntelliJ IDEA Community Edition 2025.2.1\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
$env:GRADLE_USER_HOME='C:\Users\haha\.gradle'
.\gradlew.bat assembleDebug
```

Expected result:

```text
BUILD SUCCESSFUL
```

