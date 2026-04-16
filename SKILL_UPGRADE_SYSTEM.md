# 技能升级系统设计文档

## 1. 概述

玩家通过完成任务掉落**技能水晶**，然后在**训练中心**升级各个角色的特殊技能。

### 核心机制
- **掉落来源**：仅从完成任务的奖励
- **升级消耗**：技能水晶，成本线性递增
- **升级效果**：根据技能类型提升不同属性
- **上限**：各技能最多升级到5级

---

## 2. 技能水晶系统

### 2.1 掉落规则

根据任务完成难度和击败威胁类型，掉落不同数量的技能水晶。

```java
public class LootSystem {
    private static final int BASE_CRYSTAL = 1;
    
    /**
     * 计算任务完成后的技能水晶奖励
     */
    public static int calculateCrystalReward(Threat threat, int missionCount, 
                                             List<CrewMember> survivors) {
        // 难度计算：基础值 + 任务数 + 存活队员数
        int difficulty = threat.calculateDifficulty();  
        // difficulty = threat.skill + threat.resilience + (threat.energy / 10)
        
        // 基础奖励：难度 = 5 → 1晶，难度 = 10 → 2晶，难度 = 20 → 4晶
        int crystals = (difficulty / 5) * BASE_CRYSTAL;
        
        // 存活奖励加成：每个存活队员 +1晶
        crystals += survivors.size();
        
        // 经验加成：如果队员升级了，+1晶
        for (CrewMember crew : survivors) {
            if (crew.getExperienceGained() > 0) {
                crystals += 1;  // +1晶
            }
        }
        
        return crystals;
    }
}

/**
 * 改进的掉落表
 */
public enum MissionDifficulty {
    EASY(1, 2),      // 难度: 1~5    掉落: 2晶
    NORMAL(2, 4),    // 难度: 6~10   掉落: 4晶
    HARD(3, 7),      // 难度: 11~15  掉落: 7晶
    EXTREME(4, 12);  // 难度: 16+    掉落: 12晶
    
    private final int baseDifficulty;
    private final int crystalReward;
    
    MissionDifficulty(int baseDifficulty, int crystalReward) {
        this.baseDifficulty = baseDifficulty;
        this.crystalReward = crystalReward;
    }
}
```

### 2.2 掉落示例

```
=== MISSION COMPLETE ===
Threat: Asteroid Storm (Difficulty: 12 = EXTREME)
Survivors: 2 crew members
Both gained experience: +2 bonus

Crystal calculation:
  - Base: 12 = EXTREME tier → 12 crystals
  - Survivor bonus: 2 × 1 = +2 crystals
  - Experience bonus: 2 × 1 = +2 crystals
  - Total reward: 12 + 2 + 2 = 16 skill crystals

Pilot(Nova) gained 2 experience points
Robot(Unit-7) gained 2 experience points
SKILL CRYSTALS OBTAINED: +16 🔷
```

### 2.3 技能水晶管理

```java
public class CrewMember {
    // ... 原有属性 ...
    
    private int skillCrystalsOwned = 0;  // 拥有的技能水晶数量
    
    /**
     * 添加技能水晶
     */
    public void addSkillCrystals(int amount) {
        this.skillCrystalsOwned += amount;
        logMessage("Obtained " + amount + " skill crystals!");
    }
    
    /**
     * 消耗技能水晶升级
     */
    public boolean upgradeAbility(int crystalCost) {
        if (this.skillCrystalsOwned >= crystalCost) {
            this.skillCrystalsOwned -= crystalCost;
            return true;
        }
        return false;
    }
    
    public int getSkillCrystalsOwned() {
        return this.skillCrystalsOwned;
    }
}
```

---

## 3. 技能升级系统

### 3.1 升级数据结构

```java
public class SkillUpgrade {
    private String abilityName;
    private int currentLevel = 1;      // 初始等级1
    private int maxLevel = 5;           // 最多升到5级
    private int totalCrystalsSpent = 0; // 总共消耗的晶
    
    /**
     * 计算升级到下一个等级所需的晶
     * 公式：cost = baseCount × currentLevel
     * 例：升到2级消耗5晶，升到3级消耗10晶，升到4级消耗15晶...
     */
    public int getCostForNextLevel() {
        if (currentLevel >= maxLevel) {
            return -1;  // 已满级
        }
        int baseCost = 5;
        return baseCost * currentLevel;
    }
    
    /**
     * 升级
     */
    public boolean upgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            totalCrystalsSpent += getCostForNextLevel();
            return true;
        }
        return false;
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }
}
```

### 3.2 升级消耗表

```
┌─────────────────────────────────────────┐
│  技能升级消耗表（技能水晶）             │
├──────┬────────────┬──────────┬──────────┤
│ 等级 │ 升级消耗   │ 总消耗   │  效果提升│
├──────┼────────────┼──────────┼──────────┤
│  1→2 │    5晶     │    5晶   │   +20%  │
│  2→3 │   10晶     │   15晶   │   +20%  │
│  3→4 │   15晶     │   30晶   │   +20%  │
│  4→5 │   20晶     │   50晶   │   +20%  │
└──────┴────────────┴──────────┴──────────┘

例：升级Soldier暴击技能
  等级1: 40%触发率  → 升级 (消耗5晶)
  等级2: 48%触发率  → 升级 (消耗10晶)
  等级3: 57.6%触发率→ 升级 (消耗15晶)
  等级4: 69.1%触发率→ 升级 (消耗20晶)
  等级5: 82.9%触发率 (满级)
```

---

## 4. 各技能升级效果设计

### 4.1 Pilot - 【闪避】升级表

```
技能水晶槽位：↓ 内容 ↓

等级  触发率    消耗    总消耗
─────────────────────────
 1    20%      -       -
 2    24%     +5晶      5晶
 3    28.8%   +10晶    15晶  
 4    34.6%   +15晶    30晶
 5    41.5%   +20晶    50晶

效果：每次升级触发率 ×1.2
```

**代码实现**：
```java
public class PilotEvasionUpgrade extends SkillUpgrade {
    private static final float BASE_RATE = 0.2f;
    private static final float UPGRADE_MULTIPLIER = 1.2f;
    
    @Override
    public float getEffectiveRate() {
        float rate = BASE_RATE;
        for (int i = 1; i < currentLevel; i++) {
            rate *= UPGRADE_MULTIPLIER;
        }
        return Math.min(rate, 0.99f);  // 最多99%
    }
}
```

### 4.2 Engineer - 【护盾】升级表

```
等级  护盾值   消耗    总消耗
─────────────────────────
 1    20点    -       -
 2    30点   +5晶      5晶
 3    40点   +10晶    15晶  
 4    50点   +15晶    30晶
 5    60点   +20晶    50晶

效果：每次升级护盾值 +10点
```

**代码实现**：
```java
public class EngineerShieldUpgrade extends SkillUpgrade {
    private static final int BASE_SHIELD = 20;
    private static final int SHIELD_INCREMENT = 10;
    
    @Override
    public int getEffectiveShieldValue() {
        return BASE_SHIELD + (SHIELD_INCREMENT * (currentLevel - 1));
    }
}
```

### 4.3 Medic - 【急救】升级表

```
等级  治疗量   消耗    总消耗
─────────────────────────
 1    12点    -       -
 2    14点   +5晶      5晶
 3    16点   +10晶    15晶  
 4    18点   +15晶    30晶
 5    20点   +20晶    50晶

效果：每次升级治疗量 +2点
同时：能量消耗 5 → 8保持不变（专业性提升）
```

**代码实现**：
```java
public class MedicHealingUpgrade extends SkillUpgrade {
    private static final int BASE_HEAL = 12;
    private static final int HEAL_INCREMENT = 2;
    
    @Override
    public int getEffectiveHealAmount() {
        return BASE_HEAL + (HEAL_INCREMENT * (currentLevel - 1));
    }
}
```

### 4.4 Scientist - 【智能分析】升级表

```
等级  伤害加成  触发率   消耗    总消耗
───────────────────────────────
 1   +30%     35%     -       -
 2   +36%     42%    +5晶      5晶
 3   +43%     50%    +10晶    15晶  
 4   +52%     60%    +15晶    30晶
 5   +62%     72%    +20晶    50晶

效果：每次升级伤害加成 ×1.2，触发率 ×1.2
```

**代码实现**：
```java
public class ScientistAnalysisUpgrade extends SkillUpgrade {
    private static final float BASE_MULTIPLIER = 1.3f;
    private static final float BASE_RATE = 0.35f;
    private static final float UPGRADE_MULTIPLIER = 1.2f;
    
    @Override
    public float getEffectiveDamageMultiplier() {
        float mult = BASE_MULTIPLIER;
        for (int i = 1; i < currentLevel; i++) {
            mult *= UPGRADE_MULTIPLIER;
        }
        return mult;
    }
    
    @Override
    public float getEffectiveRate() {
        float rate = BASE_RATE;
        for (int i = 1; i < currentLevel; i++) {
            rate *= UPGRADE_MULTIPLIER;
        }
        return Math.min(rate, 0.99f);
    }
}
```

### 4.5 Soldier - 【绝对打击】升级表

```
等级  暴击率    伤害倍数  消耗    总消耗
──────────────────────────────────
 1    40%      2.0x    -       -
 2    48%      2.2x   +5晶      5晶
 3    57.6%    2.4x   +10晶    15晶  
 4    69.1%    2.6x   +15晶    30晶
 5    82.9%    2.8x   +20晶    50晶

效果：每次升级暴击率 ×1.2，伤害倍数 +0.2x
```

**代码实现**：
```java
public class SoldierCriticalStrikeUpgrade extends SkillUpgrade {
    private static final float BASE_RATE = 0.4f;
    private static final float BASE_MULTIPLIER = 2.0f;
    private static final float UPGRADE_RATE_MULT = 1.2f;
    private static final float UPGRADE_DMG_INCREMENT = 0.2f;
    
    @Override
    public float getEffectiveRate() {
        float rate = BASE_RATE;
        for (int i = 1; i < currentLevel; i++) {
            rate *= UPGRADE_RATE_MULT;
        }
        return Math.min(rate, 0.99f);
    }
    
    @Override
    public float getEffectiveDamageMultiplier() {
        return BASE_MULTIPLIER + (UPGRADE_DMG_INCREMENT * (currentLevel - 1));
    }
}
```

### 4.6 Robot - 【自我修复】升级表

```
等级  修复量   修复周期  消耗    总消耗
──────────────────────────────────
 1    5点    每3回     -       -
 2    7点    每3回    +5晶      5晶
 3    10点   每3回   +10晶    15晶  
 4    13点   每3回   +15晶    30晶
 5    16点   每2回   +20晶    50晶

效果：修复量逐级递增，5级时修复周期从3回减为2回
```

**代码实现**：
```java
public class RobotRepairUpgrade extends SkillUpgrade {
    private static final int[] REPAIR_AMOUNTS = {5, 7, 10, 13, 16};
    private static final int[] REPAIR_INTERVALS = {3, 3, 3, 3, 2};
    
    @Override
    public int getEffectiveRepairAmount() {
        return REPAIR_AMOUNTS[currentLevel - 1];
    }
    
    @Override
    public int getEffectiveRepairInterval() {
        return REPAIR_INTERVALS[currentLevel - 1];
    }
}
```

---

## 5. 训练中心UI集成

### 5.1 训练中心界面布局

```
┌─────────────────────────────────────────┐
│         Space Colony - 训练中心         │
├─────────────────────────────────────────┤
│  技能水晶拥有:  [████████░░] 45/100晶   │
├─────────────────────────────────────────┤
│  可升级的技能:                          │
│                                         │
│  ┌─ Pilot (Nova)                      │
│  │ 【闪避】Lv.2 (48%)                  │
│  │ 升到Lv.3消耗: 10晶  [升级]            │
│  │ 进度: ████░░░░░░ (4/50晶)          │
│  └─────────────────────────────────────│
│                                         │
│  ┌─ Soldier (Rex)                     │
│  │ 【绝对打击】Lv.1 (40%)              │
│  │ 升到Lv.2消耗: 5晶   [升级]            │
│  │ 进度: ░░░░░░░░░░ (0/5晶)           │
│  └─────────────────────────────────────│
│                                         │
│  ┌─ Robot (Unit-7)                    │
│  │ 【自我修复】Lv.5 ⭐ (满级!)          │
│  │ 修复量: 16点/2回合                    │
│  │ [已满级] 无法升级                    │
│  └─────────────────────────────────────│
│                                         │
│                [返回] [存档]              │
└─────────────────────────────────────────┘
```

### 5.2 升级流程

```java
public class TrainingCenter extends AppCompatActivity {
    
    /**
     * 升级技能
     */
    public void upgradeAbility(CrewMember crew, SkillUpgrade upgrade) {
        int costNeeded = upgrade.getCostForNextLevel();
        
        if (costNeeded == -1) {
            showDialog("已满级", "该技能已升级到最高等级!");
            return;
        }
        
        if (crew.getSkillCrystalsOwned() < costNeeded) {
            showDialog("晶体不足", 
                String.format("需要%d晶,但只有%d晶", 
                    costNeeded, crew.getSkillCrystalsOwned()));
            return;
        }
        
        // 确认对话框
        new AlertDialog.Builder(this)
            .setTitle("确认升级?")
            .setMessage(String.format(
                "将 %s 从 Lv.%d 升到 Lv.%d?" +
                "n消耗: %d晶体", 
                upgrade.getAbilityName(),
                upgrade.getCurrentLevel(),
                upgrade.getCurrentLevel() + 1,
                costNeeded))
            .setPositiveButton("升级", (dialog, which) -> {
                // 执行升级
                crew.upgradeAbility(costNeeded);
                upgrade.upgrade();
                
                // 更新UI
                showToast("升级成功! " + upgrade.getAbilityName() + 
                         " 已升到Lv." + upgrade.getCurrentLevel());
                refreshUI();
            })
            .setNegativeButton("取消", null)
            .show();
    }
}
```

---

## 6. 完整战斗示例 - 升级技能对比

### 情景：同一队伍，技能未升级 vs 已升级

```
=== 测试1: Pilot 闪避技能 LV.1 ===
Threat: Asteroid Storm
Pilot(Nova) acts...
Asteroid Storm retaliates:
  Damage: 6 - 4 = 2
  [EVASION!] 20% chance triggers... ❌ MISS
  Evasion attempt: succeeded 1/10 times
  
=== 重新开始：升级后 ===
Pilot(Nova) 已升级闪避到 LV.2 (24% 触发率)

Asteroid Storm retaliates:
  Damage: 6 - 4 = 2
  [EVASION!] 24% chance triggers... ✅ SUCCESS
  Pilot(Nova) 成功躲避了1次额外的伤害!

累积统计：LV.1(10场) vs LV.2(10场)
  - LV.1: 2次成功闪避
  - LV.2: 2.4次成功闪避 (概率提升)
```

### 情景2：Robot修复技能升级

```
=== MISSION 1: Robot技能 LV.1 ===
Round 3: Robot修复 (5点能量)
  Robot(Unit-7) energy: 12/22 → 17/22
Round 6: Robot修复 (5点能量)  
  Robot(Unit-7) energy: 8/22 → 13/22
(每3回合修复5点)

=== MISSION 2: 升级后 Robot技能 LV.5 ===
Round 2: Robot修复 (16点能量) ⚡ 周期更短!
  Robot(Unit-7) energy: 6/22 → 22/22 (满!)
Round 4: Robot修复 (16点能量)
  Robot(Unit-7) energy: 16/22 → 22/22 (满!)
(每2回合修复16点 → 续航能力大幅提升!)
```

---

## 7. 存档系统集成

升级数据需要被保存，以便下次游戏加载时恢复。

```java
public class SaveData {
    public class CrewData {
        public String name;
        public String specialization;
        public int skill;
        public int experience;
        public int skillCrystalsOwned;
        
        // 技能升级数据
        public int pilotEvasionLevel = 1;
        public int engineerShieldLevel = 1;
        public int medicHealingLevel = 1;
        public int scientistAnalysisLevel = 1;
        public int soldierCriticalLevel = 1;
        public int robotRepairLevel = 1;
    }
}

// 保存时
public void saveMission(List<CrewMember> allCrew) {
    for (CrewMember crew : allCrew) {
        CrewData data = new CrewData();
        data.skillCrystalsOwned = crew.getSkillCrystalsOwned();
        data.pilotEvasionLevel = ((Pilot)crew).getEvasionLevel();
        // ... 其他技能等级 ...
    }
}

// 加载时
public void loadMission(List<CrewData> data) {
    for (CrewData crewData : data) {
        CrewMember crew = new Pilot(crewData.name);
        crew.setSkillCrystals(crewData.skillCrystalsOwned);
        ((Pilot)crew).setEvasionLevel(crewData.pilotEvasionLevel);
        // ... 其他技能等级 ...
    }
}
```

---

## 8. 游戏进度流程

```
开始游戏
  ↓
招募宇航员 (5个+1机器人共6个)
  ↓
第1次任务 → 击败威胁 → 获得 2-3晶
  ↓
积累晶体...
  ↓
第3-4次任务 → 积累足够晶体 (5+ 晶)
  ↓
进入训练中心 → 升级第一个技能 (消耗5晶)
  ↓
重新出击 → 感受升级后的威力!
  ↓
继续任务 → 难度加强 → 掉落更多晶体 (7-12晶)
  ↓
升级更多技能 → 整个队伍变强
  ↓
终局: 所有技能都升到5级 (累计消耗 300晶)
```

---

## 9. 平衡设计

### 9.1 成本设计原理

```
升级成本 = 5 × 当前等级

等级1→2: 5晶   (容易升级，快速成长)
等级2→3: 10晶  (需要积累)
等级3→4: 15晶  (中后期投资)
等级4→5: 20晶  (大量任务+高难任务)

总成本 = 5 + 10 + 15 + 20 = 50晶
(升到5级需要完成7-10场任务)
```

### 9.2 掉落设计原理

```
- EASY任务: 2晶    (新手练习)
- NORMAL任务: 4晶  (正常接近)
- HARD任务: 7晶    (挑战性)
- EXTREME任务: 12晶 (终极挑战)

+ 存活奖励: +1晶/人
+ 经验奖励: +1晶/人

策略：玩家需要在容易任务中积累，然后从难任务获得大量奖励
```

### 9.3 进度曲线

```
累积晶体数量:
│
│       ╱╱╱╱ EXTREME任务阶段
│     ╱╱  ╱
│   ╱  ╱ HARD任务阶段
│ ╱╱ NORMAL任务阶段
│╱ EASY任务阶段
└─────────────────→ 任务数
0   5  10  15  20

期望进度：
- 5场任务后: 10-15晶 (升1级)
- 10场任务后: 30-40晶 (升2-3级)  
- 15场任务后: 60-80晶 (升3-4级)
- 20场任务后: 100+晶 (升到满级)
```

---

## 10. 实现检查清单

### Phase 1: 基础框架
- [ ] SkillUpgrade 抽象类
- [ ] 6个技能升级类实现
- [ ] LootSystem 掉落系统
- [ ] CrewMember 添加晶体属性

### Phase 2: 训练中心UI
- [ ] TrainingCenter Activity 布局
- [ ] RecyclerView 展示所有技能
- [ ] 升级按钮逻辑
- [ ] 晶体不足提示

### Phase 3: 战斗集成
- [ ] 任务完成后调用 LootSystem
- [ ] 将晶体分配给存活队员
- [ ] UI 展示获得晶体数量

### Phase 4: 存档系统
- [ ] 序列化升级等级
- [ ] 读取升级等级
- [ ] 验证数据完整性

### Phase 5: 平衡测试
- [ ] 掉落概率测试
- [ ] 升级成本调整
- [ ] 游戏难度曲线

---

## 11. 创新价值

✨ **长期目标系统** - 玩家有明确的升级路线  
✨ **资源战略** - 需要选择优先升级哪个技能  
✨ **回合限制** - 线性成本防止快速升级（增加游戏时长）  
✨ **动态平衡** - 高难任务奖励多，激励玩家挑战  
✨ **个性化** - 不同队伍可有完全不同的升级方向

这个系统可以添加 **+2 ~ +3 分**的奖励分！
