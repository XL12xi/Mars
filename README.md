# 火星殖民地生存游戏 (Space Colony Survival Game)

## 📋 项目概述

这是一个安卓回合制策略游戏，玩家控制一个两人宇航员团队在火星上生存和探险。核心特色包括：

- **6个角色职业** - 每个职业有独特属性和特殊能力
- **回合制战斗** - 2个宇航员 vs 1个威胁
- **技能水晶升级系统** - 通过完成任务获得水晶，升级特殊能力
- **动态难度缩放** - 难度随着完成的任务数增加

## 🎮 核心游戏系统

### 1. 角色职业系统 (Characters)

| 职业 | 技能 | 韧性 | 能量 | 特殊能力 |
|------|------|------|------|---------|
| 🛩️ 飞行员 (Pilot) | 5 | 4 | 20 | 闪避 (Evasion: 20%-41.5%) |
| 🔧 工程师 (Engineer) | 6 | 3 | 19 | 护盾 (Shield: 20-60伤害减免) |
| ⚕️ 医生 (Medic) | 7 | 2 | 18 | 治疗 (Healing: 12-20能量恢复) |
| 🔬 科学家 (Scientist) | 8 | 1 | 17 | 分析 (Analysis: 1.3x-1.62x伤害) |
| 💂 士兵 (Soldier) | 9 | 0 | 16 | 暴击 (Crit: 40%-82.9%×2.0-2.8x = 1.38x-6.4x伤害) |
| 🤖 机器人 (Robot) | 7 | 2 | 22 | 自修复 (Self-Repair: 5-16能量/3-2回合) |

### 2. 战斗系统

```
战斗流程：
1. 生成威胁 (Threat)
2. 回合循环：
   - 宇航员A攻击 (damage = skill + experience)
   - 如果威胁还活着，威胁反击
   - 宇航员B攻击
   - 如果威胁还活着，威胁再反击
   - 特殊能力触发检查
3. 战斗结束 → 分配水晶和经验
```

### 3. 技能水晶升级系统

每个角色的特殊能力可升级 **5 级**：

**升级成本** (线性递增):
- 升级到 Lv.2: 5晶
- 升级到 Lv.3: 10晶
- 升级到 Lv.4: 15晶
- 升级到 Lv.5: 20晶
- **总成本**: 50晶

**水晶掉落规则** (仅在任务完成时):

| 威胁难度 | 基础水晶 | 幸存者奖励 | 经验奖励 | 示例总计 |
|---------|---------|---------|---------|---------|
| EASY | 2 | +1×幸存者数 | +1×经验者数 | 2+2+2=6 |
| NORMAL | 4 | +1×幸存者数 | +1×经验者数 | 4+2+2=8 |
| HARD | 7 | +1×幸存者数 | +1×经验者数 | 7+2+2=11 |
| EXTREME | 12 | +1×幸存者数 | +1×经验者数 | 12+2+2=16 |

## 📁 项目文件结构

```
mars/
├── src/main/java/com/mars/colony/
│   ├── model/
│   │   ├── CrewMember.java         # 宇航员基类 (160行)
│   │   ├── Pilot.java              # 飞行员
│   │   ├── Engineer.java           # 工程师
│   │   ├── Medic.java              # 医生
│   │   ├── Scientist.java          # 科学家
│   │   ├── Soldier.java            # 士兵
│   │   ├── Robot.java              # 机器人 (NEW)
│   │   └── Threat.java             # 威胁/敌人
│   ├── ability/
│   │   ├── SpecialAbility.java     # 特殊能力接口
│   │   ├── PilotEvasion.java       # 飞行员闪避
│   │   ├── EngineerShield.java     # 工程师护盾
│   │   ├── MedicHealing.java       # 医生治疗
│   │   ├── ScientistAnalysis.java  # 科学家分析
│   │   ├── SoldierCriticalStrike.java # 士兵暴击
│   │   └── RobotSelfRepair.java    # 机器人自修复
│   ├── game/
│   │   ├── Colony.java             # 殖民地(游戏枢纽)
│   │   ├── MissionControl.java     # 任务控制(战斗引擎)
│   │   ├── Storage.java            # 宇航员存储系统
│   │   └── LootSystem.java         # 掉落物系统
│   ├── upgrade/
│   │   └── SkillUpgrade.java       # 技能升级基类
│   └── GameDemo.java               # 游戏演示/测试
└── README.md (本文件)
```

## 🔧 核心类详解

### CrewMember (模型基类, 160行)

**关键属性:**
```java
- id: 唯一ID
- name: 宇航员名字
- skill: 基础技能值
- resilience: 韧性(承伤倍数)
- energy: 当前能量(HP)
- maxEnergy: 最大能量
- experience: 经验值(影响伤害计算)
- skillCrystalsOwned: 拥有的技能水晶数
- specialAbility: 特殊能力(接口)
```

**关键方法:**
```java
act()                      # 行动(造成伤害 = skill + experience)
takeDamage(int)            # 受伤(检查闪避、护盾等)
train()                    # 训练(获得1-3经验,花费5-10能量)
recover()                  # 恢复(能量回满,在宿舍使用)
addSkillCrystals(int)      # 获得水晶
useSkillCrystals(int)      # 消耗水晶
getEffectiveSkill()        # 有效技能 = 基础 + 经验/10
```

### SpecialAbility (特殊能力接口)

```java
canUse(crew, threat, ally)       # 能否使用(检查条件)
executeAbility(crew, threat)     # 执行能力(应用效果)
getCurrentLevel()                # 获取当前升级等级
setLevel(int)                    # 设置升级等级
getAbilityName()                 # 能力名字
getAbilityDescription()          # 能力描述
```

### Colony (殖民地中枢, 180行)

**关键职能:**
- 管理宇航员位置 (宿舍/模拟器/任务控制)
- 提供位置-特定的行动 (在宿舍恢复、在模拟器训练)
- 追踪已完成任务数 (用于难度缩放)

**示例:**
```java
colony.addCrewMember(pilot);
colony.moveCrewTo(pilot.getId(), "MISSION_CONTROL");
colony.trainCrewInSimulator(List.of(engineer.getId()));
colony.completeMission();  // 任务后必须调用
```

### MissionControl (战斗引擎, 220行)

**核心方法:**
```java
startMission(crewA, crewB, threat)  # 启动战斗
- 循环直到威胁死亡或两个宇航员都死亡
- 每回合: crewA攻击 → threat反击 → crewB攻击 → threat再反击
- 调用 executeCrewAction() 处理伤害、特殊能力等
- 战斗结束后分配水晶和经验

generateThreat()                    # 生成随机威胁
- 威胁类型: RadiationStorm, ToxinLeak, IceAge, Meteor, Sandstorm
- 属性随 missionCount 缩放: stats × (1.0 + 0.1 * missionCount)
```

### LootSystem (掉落物系统, 100行)

```java
calculateCrystalReward(threat, crewA, crewB)
# 返回掉落的技能水晶数

计算公式:
- 基础掉落 = threatDifficulty / 5
- 幸存者奖励 = +1 晶/存活宇航员
- 经验奖励 = +1 晶/获得经验的宇航员
- 示例: 难度12 + 2幸存者 + 2经验者 = 16晶
```

## 🎯 游戏流程示例

```
1. 启动游戏
   ↓
2. 创建殖民地 + 招募宇航员
   ↓
3. 将2个宇航员移到任务控制
   ↓
4. 生成威胁(敌人)
   ↓
5. 启动战斗循环
   - 宇航员轮流攻击
   - 威胁反击
   - 特殊能力触发
   ↓
6. 战斗结束 → 计算水晶掉落
   ↓
7. 宇航员升级特殊能力(如果有足够水晶)
   ↓
8. 回到步骤3(或其他位置)
```

## 🚀 运行示例 (GameDemo)

```bash
# 编译
javac src/main/java/com/mars/colony/*.java \
      src/main/java/com/mars/colony/model/*.java \
      src/main/java/com/mars/colony/ability/*.java \
      src/main/java/com/mars/colony/game/*.java \
      src/main/java/com/mars/colony/upgrade/*.java

# 运行demo
java -cp src/main/java com.mars.colony.GameDemo
```

**输出示例:**
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
...

=== Mission Results ===
Success: YES
Nova: HP 14/20, XP 1, Crystals 6
Rex: HP 12/16, XP 1, Crystals 6

[5] Skill Upgrade Demo...
Before upgrade: Soldier crit rate = 40%
After upgrade to Lv.2: Soldier crit rate = 48%
Crystals remaining: 1
```

## 📊 能力升级对比

### 飞行员 (Pilot Evasion)
```
闪避率: 20% × 1.2^(level-1)
Lv.1: 20%   → 5次攻击中1次躲避
Lv.2: 24%   → 5次中1.2次躲避
Lv.3: 28.8% → 5次中1.44次躲避
Lv.4: 34.6% → 5次中1.73次躲避
Lv.5: 41.5% → 5次中2.08次躲避
```

### 士兵 (Soldier Critical Strike)
```
暴击伤害倍数: [crit_rate% × crit_mult]
Lv.1: 40% × 2.0x = 最大 +80% 伤害
Lv.2: 48% × 2.2x = 最大 +105% 伤害
Lv.3: 57.6% × 2.4x = 最大 +138% 伤害
Lv.4: 69% × 2.6x = 最大 +179% 伤害
Lv.5: 82.9% × 2.8x = 最大 +232% 伤害
```

### 机器人 (Robot Self-Repair)
```
修复量 & 间隔:
Lv.1: 5能量 每3回合
Lv.2: 7能量 每3回合
Lv.3: 10能量 每3回合
Lv.4: 13能量 每3回合
Lv.5: 16能量 每2回合 ← 更频繁!
```

## 🎓 教学设计亮点

1. **多态性设计** - SpecialAbility接口允许轻松扩展新能力
2. **数据驱动** - 每个能力的升级公式是独立的,易于调整
3. **平衡游戏性** - 水晶只在任务完成时掉落,防止刷币
4. **协作机制** - 2人团队共享战斗和奖励,需要战术配合
5. **难度曲线** - 随完成任务数自动缩放,保持挑战性

## 🔮 后续开发路线图

### UI层 (Yuxiang处理)
- [ ] HomeActivity - 主菜单
- [ ] QuartersActivity - 宿舍(恢复/管理)
- [ ] SimulatorActivity - 模拟器(训练)
- [ ] MissionControlActivity - 任务选择
- [ ] TrainingCenterActivity - 升级能力
- [ ] RecyclerView适配器

### 数据持久化 (Zeyu处理)
- [ ] Gson JSON序列化
- [ ] SharedPreferences存档
- [ ] 自动保存/读取机制

### 测试 & 优化
- [ ] 单元测试(JUnit)
- [ ] 战斗平衡测试
- [ ] 性能优化(RecyclerView缓存)

## 📝 文件大小统计

| 文件 | 行数 | 用途 |
|-----|------|------|
| CrewMember.java | 160 | 基础模型 |
| 6个角色类 | 20-30 | 角色专业化 |
| SpecialAbility.java | 30 | 接口定义 |
| 6个能力类 | 100-120 | 能力实现 |
| Threat.java | 110 | 敌人模型 |
| Storage.java | 80 | 数据存储 |
| LootSystem.java | 100 | 掉落计算 |
| SkillUpgrade.java | 60 | 升级基类 |
| Colony.java | 180 | 游戏中枢 |
| MissionControl.java | 220 | 战斗引擎 |
| GameDemo.java | 100 | 演示/测试 |
| **总计** | **1,370** | |

## ✅ 完成状态

- ✅ 模型层: 所有角色、能力、敌人、数据结构
- ✅ 游戏逻辑: 战斗、能力、升级、掉落系统
- ✅ 编译可用: 所有23+个Java文件无编译错误
- ⏳ UI层: 待实现(Activities + Adapters)
- ⏳ 存档系统: 待实现(Gson + SharedPreferences)
- ⏳ 测试: 基础演示完成,完整测试待做

---

**作者**: Yuxiang Lu (UI/Assets) & Zeyu Liu (Core Logic)  
**提交日期**: 2024年4月20日前  
**版本**: 1.0 Alpha  
