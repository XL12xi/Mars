# Android 应用崩溃诊断和修复

## 问题分析

**错误信息:**
```
java.lang.ClassNotFoundException: Didn't find class "com.mars.colony.ui.MainActivity" 
on path: DexPathList[[zip file "/data/app/...base.apk"]...]
```

**根本原因:**
该错误表示 APK 文件中不包含 MainActivity 类。这通常由以下原因导致：
1. ✓ 编译问题（已排除 - 构建显示成功）
2. ✓ 混淆配置问题（已排除 - Debug 不混淆）
3. ✓ 源文件问题（已排除 - MainActivity.java 存在且语法正确）
4. **可能原因：旧的 APK 版本仍在设备上**

## 修复步骤

### 步骤 1: 完全卸载旧应用
```bash
adb uninstall com.mars.colony
```

### 步骤 2: 清理并重新构建
```bash
cd c:\Users\XL\Desktop\mars
./gradlew clean assembleDebug
```

### 步骤 3: 安装新的 APK
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 步骤 4: 启动应用
```bash
adb shell am start -n com.mars.colony/.ui.MainActivity
```

## 如果问题依然存在

### 检查清单文件
确保 `src/main/AndroidManifest.xml` 中的 Activity 声明正确：
```xml
<activity
    android:name=".ui.MainActivity"
    android:exported="true"
    android:screenOrientation="portrait">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```
✓ 已验证 - 配置正确

### 检查 Java 编译
验证所有 Activity 都能编译：
```bash
./gradlew assembleDebug --info 2>&1 | grep -i "compiling\|error"
```

### 检查 Dex 文件
如果应用很大，可能需要启用 MultiDex：
```gradle
android {
    defaultConfig {
        multiDexEnabled true  // 如果需要的话
    }
}
```

### 检查 AndroidStudio Build 缓存
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

## 验证构建完整性

当前构建状态：
✅ BUILD SUCCESSFUL in 1m 14s
✅ 32 actionable tasks

## ProGuard 配置验证

配置文件位置: `app/proguard-rules.pro`

保护规则：
```
-keep class com.mars.colony.** { *; }           ✓ 保持所有包
-keep public class * extends android.app.Activity  ✓ 保持所有 Activity
-keep public class * extends android.view.View    ✓ 保持所有 View
```

## 推荐的完整解决方案

**快速修复（最可能有效）：**
1. 在 Android Studio 中点击 `Build` → `Clean Project`
2. 点击 `Build` → `Rebuild Project`
3. 在 Android 设备上进入 `设置` → `应用` → `火星殖民地` → 卸载
4. 重新运行应用（Run 或 Debug）

**或者使用命令行：**
```bash
# 完全重置
./gradlew clean
./gradlew assembleDebug

# 卸载旧应用
adb uninstall com.mars.colony

# 安装新应用
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 启动应用
adb shell am start -n com.mars.colony/com.mars.colony.ui.MainActivity
```

## 其他可能的问题

### DeadObjectException (日志中出现)
- 原因：设备进程通信问题
- 解决：重启设备或模拟器

### EmojiCompat 初始化问题 (日志中出现)
- 原因：Google Keyboard 问题，非应用问题
- 解决：使用系统输入法

### BluetoothActivityEnergyInfo 错误 (日志中出现)
- 原因：系统级服务问题，非应用问题
- 解决：重启设备

## 检查清单

- [ ] 在 Android Studio 中执行 `Build → Clean Project`
- [ ] 执行 `Build → Rebuild Project`
- [ ] 卸载设备上的旧应用
- [ ] 重新运行应用
- [ ] 检查 logcat 中的启动日志

## 验证日志

成功启动应该显示：
```
2026-04-15 18:xx:xx.xxx  xxxx-xxxx  ActivityManager  system_server  I  Start proc xxxx for activity com.mars.colony/.ui.MainActivity
```

而不是：
```
java.lang.ClassNotFoundException: Didn't find class "com.mars.colony.ui.MainActivity"
```
