# 技能升级系统 - 完整实现指南

## 📋 概述

技能升级系统是火星殖民地游戏的核心创新功能，允许玩家通过完成任务获得技能水晶，并用这些水晶升级宇航员的特殊能力。

---

## 🎯 核心机制

### 1. 技能水晶掉落

任务完成后，根据威胁难度掉落技能水晶：

```
难度计算: skill + resilience + (energy/10)

EASY (≤5)：      基础2晶  + 存活者奖励 + 经验奖励
NORMAL (6-10)：  基础4晶  + 存活者奖励 + 经验奖励
HARD (11-15)：   基础7晶  + 存活者奖励 + 经验奖励
EXTREME (16+)：  基础12晶 + 存活者奖励 + 经验奖励

额外奖励：
- 存活者奖励: +1晶/存活队员
- 经验奖励:   +1晶/获得经验的队员
```

**示例计算：**
- 威胁难度：12 (HARD等级)
- 存活人数：2人
- 经验获得：2人
- 总掉落：7 + 2 + 2 = **11晶**

### 2. 技能升级系统

每个宇航员拥有6个特殊技能，各自可独立升级到5级：

**升级成本（线性递增）：**
```
Lv.1 → Lv.2: 5晶
Lv.2 → Lv.3: 10晶
Lv.3 → Lv.4: 15晶
Lv.4 → Lv.5: 20晶
总成本：50晶 (从Lv.1升到Lv.5)
```

### 3. 能力升级效果

每个能力升级公式不同，反映其独特特性：

| 技能 | 升级公式 | Lv.1 | Lv.5 | 变化 |
|------|---------|------|------|------|
| **闪避** | 20% × 1.2^(lv-1) | 20% | 41.5% | ×2.07 |
| **护盾** | 20 + 10×(lv-1) | 20 | 60 | +40 |
| **治疗** | 12 + 2×(lv-1) | 12 | 20 | +8 |
| **分析** | 1.3x × 1.2^(lv-1) | 1.3x | 1.62x | ×1.25 |
| **暴击** | 40% × 1.2^(lv-1) ‖ 2.0x + 0.2×(lv-1) | 40%×2.0x | 82.9%×2.8x | ×5.8 |
| **修复** | [5,7,10,13,16] / [3,3,3,3,2] | 5/3回 | 16/2回 | ×3.2 |

---

## 📁 新增代码文件

### 1. SkillUpgradeManager.java (新文件)

**位置:** `src/main/java/com/mars/colony/upgrade/SkillUpgradeManager.java`

**职责:** 管理单个宇航员的所有6个技能升级状态

**关键方法：**
```java
// 添加/消耗水晶
void addCrystals(int amount)
boolean spendCrystals(int amount)

// 升级技能
boolean upgradeSkill(String skillName)

// 查询
int getSkillLevel(String skillName)
int getUpgradeCost(String skillName)
String getSkillEffectDisplay(String skillName)

// 报告
String getUpgradeReport()
```

**使用示例：**
```java
SkillUpgradeManager manager = new SkillUpgradeManager(1, "Nova");

// 收集水晶
manager.addCrystals(30);

// 升级闪避技能
if (manager.upgradeSkill("Evasion")) {
    System.out.println("✓ 升级成功");
} else {
    System.out.println("✗ 可用晶体不足");
}

// 显示当前效果
System.out.println(manager.getSkillEffectDisplay("Evasion"));
// 输出: 闪避率: 24.0%
```

### 2. LootSystem.java (升级)

**改进内容：**
- 新增 `calculateCrystalReward()` 改进版本
- 新增 `getCrystalRewardReport()` 显示掉落细节

**关键方法：**
```java
// 计算掉落晶体
static int calculateCrystalReward(Threat threat, List<CrewMember> survivors)

// 获取详细报告
static String getCrystalRewardReport(Threat threat, List<CrewMember> survivors)

// 示例：
// "[掉落报告] 难度: Hard (7 crystals base) | 基础: 7晶 | 存活奖励: +2晶 | 经验奖励: +2晶 | 总计: 11晶"
```

### 3. UpgradeSystemDemo.java (新文件)

演示程序展示：
1. 创建宇航员并分配技能
2. 创建升级管理器
3. 模拟战斗和掉落
4. 分配水晶给存活者
5. 展示升级前后对比
6. 连续升级演示

**运行方式：**
```bash
javac -d bin src/main/java/com/mars/colony/**/*.java src/main/java/com/mars/colony/*.java
java -cp bin com.mars.colony.UpgradeSystemDemo
```

### 4. UpgradeSystemTest.java (新文件)

单元测试验证：
- ✓ 升级成本计算
- ✓ 所有技能效果公式
- ✓ 掉落系统分类
- ✓ 升级管理器逻辑

**运行方式：**
```bash
java -cp bin com.mars.colony.UpgradeSystemTest
```

---

## 🛠️ 集成指南

### 在 CrewMember 中集成

```java
// 在 CrewMember 中添加
private SkillUpgradeManager upgradeManager;

// 构造函数中初始化
public CrewMember(...) {
    ...
    this.upgradeManager = new SkillUpgradeManager(id, name);
}

// 提供getter
public SkillUpgradeManager getUpgradeManager() {
    return upgradeManager;
}
```

### 在战斗结束后分配水晶

```java
// MissionControl.finalizeMission()
List<CrewMember> survivors = ...;
int crystals = LootSystem.calculateCrystalReward(threat, survivors);

for (CrewMember crew : survivors) {
    int share = crystals / survivors.size();
    crew.getUpgradeManager().addCrystals(share);
}
```

### 在 UI 中显示升级界面

```java
// TrainingCenterActivity.java
public class TrainingCenterActivity extends AppCompatActivity {
    private RecyclerView skillRecyclerView;
    private SkillUpgradeAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_center);
        
        // 获取当前宇航员
        SkillUpgradeManager manager = crew.getUpgradeManager();
        
        // 创建adapter
        adapter = new SkillUpgradeAdapter(manager);
        skillRecyclerView.setAdapter(adapter);
    }
}
```

---

## 📊 数据持久化

### SharedPreferences 保存格式

```java
// 保存升级数据
SharedPreferences prefs = context.getSharedPreferences("crew_upgrades", MODE_PRIVATE);
SharedPreferences.Editor editor = prefs.edit();

// 使用JSON格式存储完整升级状态
String upgradeJson = gson.toJson(upgradeManager);
editor.putString("crew_" + crewId + "_upgrades", upgradeJson);
editor.apply();
```

### Gson 序列化示例

```java
// 序列化
Gson gson = new Gson();
String json = gson.toJson(upgradeManager);

// 反序列化
SkillUpgradeManager restored = gson.fromJson(json, SkillUpgradeManager.class);
```

---

## 🎮 游戏流程集成

### 完整的玩家操作流程

```
1. [首页] 开始游戏
   ↓
2. [宿舍] 招募/管理宇航员
   ↓
3. [模拟器] 训练宇航员
   ↓
4. [任务控制] 选择2人 → 开始战斗
   ↓
5. [战斗界面] 回合制战斗
   ↓
6. [战斗结果] 
   - 获胜 → 分配水晶 + 经验
   - 失败 → 损失HP
   ↓
7. [训练中心] 使用晶体升级技能
   - 查看当前等级和效果
   - 选择技能
   - 消耗晶体升级
   - 预览升级后效果
   ↓
8. 返回步骤3继续游戏
```

---

## 📈 性能优化建议

### 1. 缓存计算结果

```java
// 缓存升级公式结果，避免重复计算
private double cachedEvasionRate;
private long cacheTimestamp;

public double getEvasionRate() {
    if (System.currentTimeMillis() - cacheTimestamp < 1000) {
        return cachedEvasionRate;
    }
    cachedEvasionRate = 0.20 * Math.pow(1.2, level - 1);
    cacheTimestamp = System.currentTimeMillis();
    return cachedEvasionRate;
}
```

### 2. 批量操作优化

```java
// 避免多次 SharedPreferences 写入
SharedPreferences.Editor editor = prefs.edit();
for (CrewMember crew : allCrew) {
    String json = gson.toJson(crew.getUpgradeManager());
    editor.putString("crew_" + crew.getId() + "_upgrades", json);
}
editor.apply();  // 一次性写入
```

### 3. RecyclerView 优化

```java
// 在 SkillUpgradeAdapter 中使用 ViewHolder 模式
public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView skillName;
    ProgressBar levelBar;
    Button upgradeBtn;
    
    public void bind(SkillUpgradeManager manager, String skillName) {
        // 只更新改变的属性
        int level = manager.getSkillLevel(skillName);
        levelBar.setProgress(level * 20);  // 20% per level
    }
}
```

---

## 🧪 测试清单

- [ ] 升级成本计算正确 (5, 10, 15, 20)
- [ ] 所有技能效果公式验证
  - [ ] 闪避: 20% → 41.5%
  - [ ] 护盾: 20 → 60
  - [ ] 治疗: 12 → 20
  - [ ] 分析: 1.3x → 1.62x
  - [ ] 暴击: 40-82.9% x 2.0-2.8x
  - [ ] 修复: 5-16能量, 3-2回合
- [ ] 掉落系统
  - [ ] EASY: 2晶 (+奖励)
  - [ ] NORMAL: 4晶 (+奖励)
  - [ ] HARD: 7晶 (+奖励)
  - [ ] EXTREME: 12晶 (+奖励)
- [ ] 升级管理器
  - [ ] 添加晶体
  - [ ] 升级技能
  - [ ] 晶体消耗
  - [ ] 满级行为
- [ ] 数据持久化
  - [ ] 升级状态保存
  - [ ] 升级状态恢复
- [ ] UI 集成
  - [ ] 显示当前等级
  - [ ] 显示升级成本
  - [ ] 升级按钮工作
  - [ ] 实时显示效果

---

## 📝 代码示例库

### 示例1: 初始化升级系统

```java
// 为所有宇航员创建升级管理器
Map<Integer, SkillUpgradeManager> managers = new HashMap<>();

for (CrewMember crew : colony.getAllCrew()) {
    SkillUpgradeManager manager = new SkillUpgradeManager(crew.getId(), crew.getName());
    managers.put(crew.getId(), manager);
}
```

### 示例2: 战斗后分配奖励

```java
// 启动战斗
boolean success = mission.startMission(crewA, crewB, threat);

if (success) {
    // 计算掉落
    List<CrewMember> survivors = Arrays.asList(crewA, crewB);
    int totalCrystals = LootSystem.calculateCrystalReward(threat, survivors);
    
    // 分配给每个存活者
    for (CrewMember survivor : survivors) {
        int share = totalCrystals / survivors.size();
        managers.get(survivor.getId()).addCrystals(share);
    }
}
```

### 示例3: 升级一个技能

```java
SkillUpgradeManager manager = managers.get(crewId);

// 检查是否可以升级
if (manager.getSkillLevel("Evasion") < 5) {
    int cost = manager.getUpgradeCost("Evasion");
    
    if (manager.getSkillCrystalsOwned() >= cost) {
        if (manager.upgradeSkill("Evasion")) {
            System.out.println("✓ 升级成功");
            
            // 显示新效果
            String effect = manager.getSkillEffectDisplay("Evasion");
            System.out.println("新效果: " + effect);
        }
    } else {
        System.out.println("晶体不足 (需要" + cost + "晶)");
    }
} else {
    System.out.println("技能已满级");
}
```

### 示例4: 显示完整的升级报告

```java
System.out.println(manager.getUpgradeReport());
// 输出:
// === Nova's Skill Upgrades ===
// Crystals: 8 | Total Spent: 5
// 
// Skill Status:
//   Evasion Lv.2 [●●○○○] Cost: 10
//   Shield Lv.1 [●○○○○] Cost: 5
//   Healing Lv.1 [●○○○○] Cost: 5
//   Analysis Lv.1 [●○○○○] Cost: 5
//   CriticalStrike Lv.1 [●○○○○] Cost: 5
//   SelfRepair Lv.1 [●○○○○] Cost: 5
```

---

## 🚀 下一步工作

1. **UI 集成** - 创建 TrainingCenterActivity
2. **RecyclerView 适配器** - SkillUpgradeAdapter
3. **数据持久化** - Gson + SharedPreferences 集成
4. **特殊事件** - 掉落动画、升级音效
5. **平衡调整** - 根据游戏测试调整掉落和成本
6. **新手教程** - 升级系统引导

---

**版本:** 1.0  
**最后更新:** 2026年4月8日

