# 应用安装和启动指南

## 当前状态
✅ 编译成功 - app-debug.apk 已生成
❌ 应用无法打开 - 需要重新安装

## 快速修复步骤（3 选 1）

### 方案 A：使用 ADB 命令行（最快）

**前提条件：** 已连接 Android 设备或启动模拟器

```powershell
cd c:\Users\XL\Desktop\mars

# 1. 卸载旧应用
adb uninstall com.mars.colony

# 2. 安装新 APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 3. 启动应用
adb shell am start -n com.mars.colony/com.mars.colony.ui.MainActivity

# 4. 查看实时日志（可选，用于调试）
adb logcat -s AndroidRuntime,mars.colony
```

### 方案 B：使用 Android Studio（推荐新手）

1. 在 Android Studio 中打开项目
2. 连接 Android 设备或启动模拟器
3. 点击菜单 `Build` → `Clean Project`
4. 点击菜单 `Build` → `Rebuild Project`
5. 按 Shift+F10（或点击 Run 按钮）
6. 选择要运行的设备

### 方案 C：手动安装

```powershell
# 1. 生成最新 APK
cd c:\Users\XL\Desktop\mars
./gradlew clean assembleDebug

# 2. 找到 APK 文件
$apk = "app/build/outputs/apk/debug/app-debug.apk"
Write-Host "APK 位置: $((Get-Item $apk).FullName)"

# 3. 在设备上手动卸载旧应用
#    设置 → 应用 → 火星殖民地 → 卸载

# 4. 使用 ADB 安装
adb install -r $apk
```

## 常见问题排查

### 错误：adb: command not found
**解决方案：** 需要配置 Android SDK 环境变量
```powershell
# 查找 adb 路径
Get-Command adb
# 或手动指定完整路径
"$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb" install -r app/build/outputs/apk/debug/app-debug.apk
```

### 错误：没有找到连接的设备
**解决方案：** 
- 检查设备是否通过 USB 连接
- 启用开发者选项和 USB 调试（设置 → 关于手机 → 连续点击构建号7次 → 开发者选项 → USB 调试）
- 或在 Android Studio 中启动模拟器

### 错误：应用在启动后立即崩溃
**解决方案：** 查看应用日志
```powershell
# 查看实时日志
adb logcat -s AndroidRuntime

# 或导出完整日志
adb logcat > app_crash.log

# 然后查看日志
Get-Content app_crash.log
```

### 错误：ClassNotFoundException
**解决方案：** 说明 APK 中缺少某个类
- 确认构建成功（BUILD SUCCESSFUL）
- 卸载设备上的旧应用
- 重新安装新 APK

## 验证安装成功

安装完成后，应该看到：
```
Success
```

然后应用应该在设备上自动启动，或在启动器中出现"火星殖民地"应用图标。

## 完整的故障排查流程

```powershell
# 1. 重新构建项目
cd c:\Users\XL\Desktop\mars
./gradlew clean assembleDebug

# 2. 检查 APK 是否生成
Get-Item app/build/outputs/apk/debug/app-debug.apk

# 3. 卸载旧应用
adb uninstall com.mars.colony

# 4. 安装新 APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 5. 启动应用
adb shell am start -n com.mars.colony/com.mars.colony.ui.MainActivity

# 6. 查看启动日志
adb logcat -s AndroidRuntime | Select-String -Pattern "FATAL|Exception|Error" -Context 5
```

## 最后步骤清单

- [ ] 确认构建成功（BUILD SUCCESSFUL）
- [ ] APK 文件存在于 `app/build/outputs/apk/debug/app-debug.apk`
- [ ] 设备已连接（运行 `adb devices` 检查）
- [ ] 旧应用已卸载
- [ ] 新 APK 已安装
- [ ] 应用已启动
- [ ] 看到主菜单界面

## 获取帮助

如果应用仍然无法打开，请提供以下信息：

1. 构建日志（运行 `./gradlew assembleDebug` 的完整输出）
2. 设备日志（运行 `adb logcat -d` 的输出）
3. 错误信息的屏幕截图或完整堆栈跟踪

## 下一步

应用成功启动后：
- 点击"🏛️ 宿舍"进入乘员管理
- 点击"⚔️ 任务控制"进行战斗任务
- 点击"✨ 训练中心"升级技能
- 点击"⚙️ 模拟训练器"进行模拟对战
