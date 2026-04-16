# 编译和运行指南 (Build & Run Guide)

## 📦 环境要求

- **Java版本**: JDK 8 或以上
- **操作系统**: Windows / macOS / Linux
- **编译器**: javac (Java编译器)

## 📂 项目结构

```
mars/
├── src/main/java/com/mars/colony/
│   ├── model/           # 模型层 (宇航员、威胁等)
│   ├── ability/         # 特殊能力层
│   ├── game/            # 游戏逻辑层
│   ├── upgrade/         # 升级系统层
│   ├── GameDemo.java    # 演示程序 (入口点)
│   └── *.java           # 所有主类
└── README.md            # 详细说明
```

## 🔨 编译步骤

### 第一步: 打开命令行/终端

**Windows (CMD或PowerShell):**
```bash
cd c:\Users\XL\Desktop\mars
```

**macOS/Linux (Terminal):**
```bash
cd ~/Desktop/mars
# 或根据实际路径调整
```

### 第二步: 编译所有Java文件

**方式1: 逐个编译各个包**

```bash
# 编译model包
javac -d bin src/main/java/com/mars/colony/model/*.java

# 编译ability包
javac -d bin -cp bin src/main/java/com/mars/colony/ability/*.java

# 编译upgrade包
javac -d bin -cp bin src/main/java/com/mars/colony/upgrade/*.java

# 编译game包
javac -d bin -cp bin src/main/java/com/mars/colony/game/*.java

# 编译根目录
javac -d bin -cp bin src/main/java/com/mars/colony/*.java
```

**方式2: 一键编译所有文件**

```bash
# 创建bin目录（如果不存在）
mkdir bin

# Windows
javac -d bin -encoding UTF-8 src/main/java/com/mars/colony/**/*.java src/main/java/com/mars/colony/*.java

# macOS/Linux
javac -d bin -encoding UTF-8 src/main/java/com/mars/colony/**/*.java src/main/java/com/mars/colony/*.java
```

**方式3: 使用build脚本**

如果编译失败，试试这种分阶段方式：

```bash
@echo off
cd src/main/java
javac -d ..\..\..\bin -encoding UTF-8 com/mars/colony/model/*.java
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com/mars/colony/ability/*.java
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com/mars/colony/upgrade/*.java
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com/mars/colony/game/*.java
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com/mars/colony/*.java
```

## ▶️ 运行程序

### 编译成功后运行演示

```bash
# 在mars目录下运行
java -cp bin com.mars.colony.GameDemo
```

### 预期输出

```
=== Space Colony Game Demo ===

[1] Creating colony...
[2] Recruiting crew members...
✓ Recruited 4 crew members

Crew Members:
  - Nova (Skill:5, Res:4, Energy:20/20)
  - Rex (Skill:9, Res:0, Energy:16/16)
  - Maya (Skill:6, Res:3, Energy:19/19)
  - Unit-7 (Skill:7, Res:2, Energy:22/22)

[3] Preparing for mission...
Generated threat: RadiationStorm (Skill:8, Res:3, Energy:35/35)

[4] Starting mission...

=== Battle Log ===
[R1] Nova attacks! Deals 5 damage. RadiationStorm health: 30/35
[R1] RadiationStorm counterattacks! Deals 6 damage. Nova health: 14/20
[R1] Rex attacks! Deals 9 damage (CRITICAL 2.1x!). RadiationStorm health: 21/35
[R1] RadiationStorm counterattacks! Deals 4 damage. Rex health: 12/16
... (更多回合) ...

=== Mission Results ===
Success: YES
Nova: HP 14/20, XP 1, Crystals 6
Rex: HP 12/16, XP 1, Crystals 6

[5] Skill Upgrade Demo...
Before upgrade: Soldier crit rate = 40%
After upgrade to Lv.2: Soldier crit rate = 48%
Crystals remaining: 1
```

## 🐛 常见错误和解决方案

### 错误1: "找不到命令javac"
```
'javac' is not recognized as an internal or external command
```

**解决方案:**
- 检查JDK是否已安装 (`java -version`)
- 将JDK的bin目录添加到PATH环境变量
  - Windows: 系统变量 → PATH → 添加 `C:\Program Files\Java\jdk1.8.0_xxx\bin`
  - macOS/Linux: `export PATH=${PATH}:/Library/Java/JavaVirtualMachines/jdk-xxx/Contents/Home/bin`

### 错误2: "找不到符号" (Cannot find symbol)
```
error: cannot find symbol
  symbol: class CrewMember
```

**解决方案:**
- 确保所有包都正确编译
- 使用 `-cp bin` 参数告诉编译器在bin目录查找已编译的类
- 按正确的包依赖顺序编译 (model → ability → upgrade → game → demo)

### 错误3: "编码错误" (Encoding issues)
```
error: unmappable character for encoding UTF-8
```

**解决方案:**
- 添加 `-encoding UTF-8` 参数
- 例: `javac -encoding UTF-8 -d bin src/main/java/...`

### 错误4: "找不到主类"
```
Exception in thread "main" java.lang.ClassNotFoundException: com.mars.colony.GameDemo
```

**解决方案:**
- 确保使用 `-cp bin` 指定classpath
- 使用完整的包名: `java -cp bin com.mars.colony.GameDemo`
- 检查GameDemo.java是否在正确的目录

## 🎯 快速启动脚本

### Windows (build.bat)

```batch
@echo off
echo === Building Mars Colony Game ===
cd src\main\java
echo [1/5] Compiling model...
javac -d ..\..\..\bin -encoding UTF-8 com\mars\colony\model\*.java
echo [2/5] Compiling ability...
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com\mars\colony\ability\*.java
echo [3/5] Compiling upgrade...
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com\mars\colony\upgrade\*.java
echo [4/5] Compiling game...
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com\mars\colony\game\*.java
echo [5/5] Compiling demo...
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com\mars\colony\*.java

echo.
echo === Build Complete ===
echo Running demo...
cd ..\..\..\
java -cp bin com.mars.colony.GameDemo

pause
```

保存为 `build.bat` 在mars根目录，然后双击运行。

### macOS/Linux (build.sh)

```bash
#!/bin/bash
echo "=== Building Mars Colony Game ==="
cd src/main/java

echo "[1/5] Compiling model..."
javac -d ../../../bin -encoding UTF-8 com/mars/colony/model/*.java

echo "[2/5] Compiling ability..."
javac -d ../../../bin -cp ../../../bin -encoding UTF-8 com/mars/colony/ability/*.java

echo "[3/5] Compiling upgrade..."
javac -d ../../../bin -cp ../../../bin -encoding UTF-8 com/mars/colony/upgrade/*.java

echo "[4/5] Compiling game..."
javac -d ../../../bin -cp ../../../bin -encoding UTF-8 com/mars/colony/game/*.java

echo "[5/5] Compiling demo..."
javac -d ../../../bin -cp ../../../bin -encoding UTF-8 com/mars/colony/*.java

echo ""
echo "=== Build Complete ==="
echo "Running demo..."
cd ../../../
java -cp bin com.mars.colony.GameDemo
```

保存为 `build.sh` 在mars根目录，然后运行:
```bash
chmod +x build.sh
./build.sh
```

## 📊 编译文件列表 (需要编译的所有文件)

### 模型层 (Model - 8个文件)
```
com/mars/colony/model/
  ├── CrewMember.java      (160行) - 宇航员基类
  ├── Pilot.java           (20行)  - 飞行员
  ├── Engineer.java        (25行)  - 工程师
  ├── Medic.java           (20行)  - 医生
  ├── Scientist.java       (22行)  - 科学家
  ├── Soldier.java         (21行)  - 士兵
  ├── Robot.java           (23行)  - 机器人
  └── Threat.java          (110行) - 威胁/敌人
```

### 能力层 (Ability - 8个文件)
```
com/mars/colony/ability/
  ├── SpecialAbility.java          (30行)  - 接口
  ├── PilotEvasion.java            (80行)  - 飞行员闪避
  ├── EngineerShield.java          (100行) - 工程师护盾
  ├── MedicHealing.java            (90行)  - 医生治疗
  ├── ScientistAnalysis.java       (120行) - 科学家分析
  ├── SoldierCriticalStrike.java   (110行) - 士兵暴击
  └── RobotSelfRepair.java         (100行) - 机器人自修复
```

### 升级层 (Upgrade - 1个文件)
```
com/mars/colony/upgrade/
  └── SkillUpgrade.java    (60行) - 升级基类
```

### 游戏层 (Game - 4个文件)
```
com/mars/colony/game/
  ├── Storage.java       (80行)  - 数据存储
  ├── Colony.java        (180行) - 殖民地
  ├── MissionControl.java (220行) - 任务控制/战斗
  └── LootSystem.java    (100行) - 掉落系统
```

### 根目录 (Root - 1个文件)
```
com/mars/colony/
  └── GameDemo.java      (100行) - 演示程序 ✓ 入口点
```

**总计:** 24个文件，约1,500行Java代码

## ✅ 验证编译成功

编译成功后，应该在 `bin/` 目录中看到以下结构:

```
bin/
└── com/mars/colony/
    ├── model/
    │   ├── CrewMember.class
    │   ├── Pilot.class
    │   ├── Engineer.class
    │   ├── Medic.class
    │   ├── Scientist.class
    │   ├── Soldier.class
    │   ├── Robot.class
    │   └── Threat.class
    ├── ability/
    │   ├── SpecialAbility.class
    │   ├── PilotEvasion.class
    │   ├── EngineerShield.class
    │   ├── MedicHealing.class
    │   ├── ScientistAnalysis.class
    │   ├── SoldierCriticalStrike.class
    │   └── RobotSelfRepair.class
    ├── upgrade/
    │   └── SkillUpgrade.class
    ├── game/
    │   ├── Storage.class
    │   ├── Colony.class
    │   ├── MissionControl.class
    │   └── LootSystem.class
    └── GameDemo.class
```

如果所有 `.class` 文件都生成了，说明编译成功！

## 🚀 进阶用法

### 生成javadoc文档

```bash
javadoc -d docs -sourcepath src/main/java -encoding UTF-8 \
    -subpackages com.mars.colony
```

然后用浏览器打开 `docs/index.html`

### 打包成JAR文件

```bash
cd bin
jar cfm ../MarsColony.jar ../MANIFEST.MF com/
cd ..

# 运行JAR
java -jar MarsColony.jar
```

其中 `MANIFEST.MF` 内容为:
```
Manifest-Version: 1.0
Main-Class: com.mars.colony.GameDemo
```

## 📚 后续步骤

1. **验证编译**: 运行 `java -cp bin com.mars.colony.GameDemo` 看输出
2. **修改配置**: 编辑 `GameDemo.java` 改变宇航员或战斗参数
3. **添加新功能**: 在相应的包中创建新类
4. **集成Android**: 将bin目录中的.class文件复制到Android项目

---

**提示**: 如果遇到问题，请检查:
- ✓ Java已安装 (`java -version`)
- ✓ 所有源文件都存在
- ✓ 包结构正确
- ✓ classpath设置 (`-cp bin`)
- ✓ 字符编码 (`-encoding UTF-8`)

