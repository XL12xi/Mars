# 🎯 快速参考 (Quick Reference Card)

## 升级系统 API 速查表

### SkillUpgradeManager - 核心方法

```java
// 创建升级管理器
SkillUpgradeManager mgr = new SkillUpgradeManager(crewId, "Nova");

// ========== 晶体操作 ==========
mgr.addCrystals(10);              // 添加10晶体
bool success = mgr.spendCrystals(5); // 消耗5晶体

// ========== 升级操作 ==========
bool upgraded = mgr.upgradeSkill("Evasion");  // 升级闪避
int level = mgr.getSkillLevel("Evasion");     // 获取当前等级
int cost = mgr.getUpgradeCost("Evasion");     // 获取升级成本

// ========== 信息查询 ==========
int crystals = mgr.getSkillCrystalsOwned();   // 拥有的晶体
int spent = mgr.getTotalCrystalsSpent();      // 总消耗
String effect = mgr.getSkillEffectDisplay("Evasion");  // 效果文本
String report = mgr.getUpgradeReport();       // 完整报告

// ========== 设置 ==========
mgr.setSkillLevel("Shield", 3);   // 直接设置等级
```

### 技能名称列表

```java
"Evasion"        // 飞行员 - 闪避
"Shield"         // 工程师 - 护盾
"Healing"        // 医生 - 治疗
"Analysis"       // 科学家 - 分析
"CriticalStrike" // 士兵 - 暴击
"SelfRepair"     // 机器人 - 修复
```

### LootSystem - 掉落计算

```java
// 计算掉落晶体
int crystals = LootSystem.calculateCrystalReward(threat, survivors);
// survivors: List<CrewMember> 存活队员列表

// 获取掉落报告
String report = LootSystem.getCrystalRewardReport(threat, survivors);
// 输出: [掉落报告] 难度: Hard (7 crystals base) | ...

// 获取难度分类
MissionDifficulty diff = LootSystem.getMissionDifficulty(threat);
// EASY, NORMAL, HARD, EXTREME

// 掉落规则
// EASY (≤5):      2晶 + 存活者 + 经验者
// NORMAL (6-10):  4晶 + 存活者 + 经验者
// HARD (11-15):   7晶 + 存活者 + 经验者
// EXTREME (16+):  12晶 + 存活者 + 经验者
```

---

## 升级成本公式

```
Lv.1 → Lv.2: 5晶
Lv.2 → Lv.3: 10晶
Lv.3 → Lv.4: 15晶
Lv.4 → Lv.5: 20晶
总计:        50晶
```

**公式:** `cost = 5 × currentLevel`

---

## 技能效果升级表

| 技能 | Lv.1 | Lv.2 | Lv.3 | Lv.4 | Lv.5 | 公式 |
|------|------|------|------|------|------|------|
| **闪避** | 20% | 24% | 28.8% | 34.6% | 41.5% | 20% × 1.2^(lv-1) |
| **护盾** | 20 | 30 | 40 | 50 | 60 | 20 + 10×(lv-1) |
| **治疗** | 12 | 14 | 16 | 18 | 20 | 12 + 2×(lv-1) |
| **分析** | 1.3x | 1.56x | 1.87x | 2.25x | 2.70x | 1.3x × 1.2^(lv-1) |
| **暴击** | 40%×2.0x | 48%×2.2x | 57.6%×2.4x | 69%×2.6x | 82.9%×2.8x | 40% × 1.2^(lv-1) ‖ 2.0x + 0.2×(lv-1) |
| **修复** | 5/3回 | 7/3回 | 10/3回 | 13/3回 | 16/2回 | [5,7,10,13,16] / [3,3,3,3,2] |

---

## 常用代码片段

### 初始化升级管理器

```java
Map<Integer, SkillUpgradeManager> managers = new HashMap<>();

for (CrewMember crew : colony.getAllCrew()) {
    SkillUpgradeManager manager = new SkillUpgradeManager(crew.getId(), crew.getName());
    managers.put(crew.getId(), manager);
}
```

### 战斗后分配奖励

```java
// 启动战斗
boolean success = mission.startMission(crewA, crewB, threat);

if (success) {
    List<CrewMember> survivors = new ArrayList<>();
    if (crewA.isAlive()) survivors.add(crewA);
    if (crewB.isAlive()) survivors.add(crewB);
    
    // 计算掉落
    int totalCrystals = LootSystem.calculateCrystalReward(threat, survivors);
    
    // 分配给每个存活者
    for (CrewMember survivor : survivors) {
        int share = totalCrystals / survivors.size();
        managers.get(survivor.getId()).addCrystals(share);
    }
}
```

### 升级技能

```java
SkillUpgradeManager manager = managers.get(crewId);
String skillName = "Evasion";

// 检查是否可以升级
if (manager.getSkillLevel(skillName) < 5) {
    int cost = manager.getUpgradeCost(skillName);
    
    if (manager.getSkillCrystalsOwned() >= cost) {
        if (manager.upgradeSkill(skillName)) {
            // 升级成功
            String newEffect = manager.getSkillEffectDisplay(skillName);
            showMessage("升级成功: " + newEffect);
        }
    } else {
        showMessage("晶体不足，需要" + cost + "晶");
    }
} else {
    showMessage("技能已满级");
}
```

### 显示升级报告

```java
System.out.println(manager.getUpgradeReport());

// 输出示例:
// === Nova's Skill Upgrades ===
// Crystals: 8 | Total Spent: 5
//
// Skill Status:
//   Evasion Lv.2 [●●○○○] Cost: 10
//   Shield Lv.1 [●○○○○] Cost: 5
//   ...
```

### 获取所有技能的效果

```java
String[] skills = {"Evasion", "Shield", "Healing", "Analysis", "CriticalStrike", "SelfRepair"};

for (String skill : skills) {
    String effect = manager.getSkillEffectDisplay(skill);
    int level = manager.getSkillLevel(skill);
    System.out.println(skill + " Lv." + level + ": " + effect);
}
```

---

## 难度计算

```java
// 威胁难度 = skill + resilience + (maxEnergy / 10)
int difficulty = threat.getSkill()
               + threat.getResilience()
               + (threat.getMaxEnergy() / 10);

// 难度分类
if (difficulty <= 5) {
    // EASY: 掉落2晶
} else if (difficulty <= 10) {
    // NORMAL: 掉落4晶
} else if (difficulty <= 15) {
    // HARD: 掉落7晶
} else {
    // EXTREME: 掉落12晶
}
```

---

## 编译和运行

### 一键编译

```bash
cd c:\Users\XL\Desktop\mars
compile.bat
```

### 单独编译各包

```bash
cd src/main/java

# Model
javac -d ../../../bin -encoding UTF-8 com/mars/colony/model/*.java

# Ability
javac -d ../../../bin -cp ../../../bin -encoding UTF-8 com/mars/colony/ability/*.java

# Upgrade
javac -d ../../../bin -cp ../../../bin -encoding UTF-8 com/mars/colony/upgrade/*.java

# Game
javac -d ../../../bin -cp ../../../bin -encoding UTF-8 com/mars/colony/game/*.java

# Demo & Tests
javac -d ../../../bin -cp ../../../bin -encoding UTF-8 com/mars/colony/*.java
```

### 运行演示

```bash
cd c:\Users\XL\Desktop\mars

# 升级系统演示
java -cp bin com.mars.colony.UpgradeSystemDemo

# 升级系统测试
java -cp bin com.mars.colony.UpgradeSystemTest

# 游戏基础演示
java -cp bin com.mars.colony.GameDemo
```

---

## 常见问题速解

### 问：如何添加更多晶体？
```java
manager.addCrystals(50);  // 立即添加50晶体
```

### 问：如何检查技能是否满级？
```java
if (manager.getSkillLevel("Evasion") >= 5) {
    // 技能已满级
}
```

### 问：升级失败的原因？
```java
// 原因1: 晶体不足
if (!manager.upgradeSkill("Evasion")) {
    int cost = manager.getUpgradeCost("Evasion");
    int have = manager.getSkillCrystalsOwned();
    // 需要cost晶体，但只有have晶体
}

// 原因2: 技能已满级
if (manager.getSkillLevel("Evasion") >= 5) {
    // 技能已是Lv.5，无法再升级
}
```

### 问：如何计算升级所有技能到Lv.5的成本？
```java
// 每个技能从Lv.1→Lv.5: 50晶
// 6个技能总计: 6 × 50 = 300晶
int totalCostForAllSkills = 6 * 50;
```

### 问：如何显示升级进度？
```java
SkillUpgradeManager manager = ...;

// 显示进度条
int level = manager.getSkillLevel("Evasion");
System.out.print("[");
for (int i = 0; i < 5; i++) {
    System.out.print(i < level ? "●" : "○");
}
System.out.println("]");
// 输出示例: [●●●○○]
```

---

## 数据结构

### SkillUpgradeManager 内部结构

```java
public class SkillUpgradeManager {
    int crewId;                           // 宇航员ID
    String crewName;                      // 宇航员名字
    int skillCrystalsOwned;               // 拥有的晶体
    int totalCrystalsSpent;               // 总消耗的晶体
    HashMap<String, Integer> skillLevels; // 每个技能的等级 (1-5)
}
```

### LootSystem.MissionDifficulty 枚举

```java
enum MissionDifficulty {
    EASY(2),       // 基础2晶
    NORMAL(4),     // 基础4晶
    HARD(7),       // 基础7晶
    EXTREME(12);   // 基础12晶
}
```

---

## 关键时间点

| 事件 | 处理方法 |
|------|---------|
| 战斗开始 | `mission.startMission(crewA, crewB, threat)` |
| 战斗结束 | 业计算掉落: `LootSystem.calculateCrystalReward()` |
| 获得晶体 | `manager.addCrystals(amount)` |
| 升级技能 | `manager.upgradeSkill(skillName)` |
| 显示报告 | `manager.getUpgradeReport()` |
| 保存进度 | 使用 Gson 和 SharedPreferences |

---

## 性能提示

- ✅ 缓存升级效果值避免重复计算
- ✅ 批量提交 SharedPreferences 更新
- ✅ 使用 RecyclerView 显示长列表
- ✅ 异步加载升级数据
- ✅ 限制实时日志输出

---

**快速参考卡片 v1.0**  
**最后更新:** 2026年4月8日

