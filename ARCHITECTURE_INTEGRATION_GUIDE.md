# Space Colony - 游戏系统架构整合指南

## 📋 三大核心系统文档

你的项目现在包含**3个详细的成体系设计文档**：

1. **IMPROVED_PROJECT_PLAN.md** - 总体项目计划 ⭐ 提交给教师
2. **SPECIAL_ABILITIES_DESIGN.md** - 特殊技能系统（6种角色×6种技能）
3. **SKILL_UPGRADE_SYSTEM.md** - 技能升级系统（晶体+升级机制）

---

## 🎮 游戏流程架构

```
┌─────────────────────────────────────────────────────┐
│  GAME START                                         │
│  ↓                                                  │
│  主菜单 → 招募宇航员 (6种角色选择)                │
│           └─ Pilot, Engineer, Medic, Scientist,    │
│              Soldier, Robot                        │
└─────────────────────────────────────────────────────┘
                      ↓
     ┌────────────────┴────────────────┐
     ↓                                 ↓
  宿舍(Quarters)              模拟器(Simulator)
  - 存储宇航员                - 仅训练经验
  - 恢复能量/保留经验         - 消耗能量
  - 查看统计                  - [不涉及技能]
     ↓                                 ↓
     └────────────────┬────────────────┘
                      ↓
         ┌─────────────────────────┐
         │ 任务控制(MissionControl) │
         │ - 选择2人队伍            │
         │ - 发起任务              │
         │ - 生成威胁              │
         │ - 执行战斗              │
         └─────────────────────────┘
                      ↓
         ┌──────────────────────────────┐
         │   ⚙ 战斗系统(Battle Engine)  │ ← SPECIAL_ABILITIES_DESIGN.md
         │   ├─ 基本伤害计算           │
         │   ├─ 特殊技能(6种)          │
         │   │  ├─ Pilot闪避           │
         │   │  ├─ Engineer护盾        │
         │   │  ├─ Medic治疗           │
         │   │  ├─ Scientist分析       │
         │   │  ├─ Soldier暴击         │
         │   │  └─ Robot修复           │
         │   ├─ 回合制逻辑             │
         │   ├─ 威胁难度缩放           │
         │   └─ 战斗日志打印           │
         └──────────────────────────────┘
                      ↓
         ┌──────────────────────────┐
         │   战斗结果                │
         │   ├─ 团队胜利             │
         │   │  ├─ 经验奖励           │
         │   │  └─ 技能水晶掉落 ✨     │
         │   │     (SKILL_UPGRADE_SYSTEM.md)
         │   │                      │
         │   └─ 团队失败             │
         │      ├─ 战败死亡           │
         │      └─ 无晶体掉落         │
         └──────────────────────────┘
                      ↓
         ┌──────────────────────────┐
         │   新增! 训练中心          │ ← SKILL_UPGRADE_SYSTEM.md
         │   (Training Center)      │
         │   ├─ 查看拥有的晶体       │
         │   ├─ 显示所有技能等级     │
         │   ├─ 升级技能(消耗晶)     │
         │   │  └─ 1→2: 5晶          │
         │   │  └─ 2→3: 10晶         │
         │   │  └─ 3→4: 15晶         │
         │   │  └─ 4→5: 20晶         │
         │   └─ 存档升级数据         │
         └──────────────────────────┘
                      ↓
         ┌──────────────────────────┐
         │   游戏进度循环             │
         │   ├─ 回到宿舍             │
         │   ├─ 训练或发起新任务     │
         │   └─ 不断升级技能         │
         └──────────────────────────┘
                      ↓
              { 游戏继续... }
```

---

## 🔗 系统间的依赖关系

### 1️⃣ 特殊技能系统 → 战斗系统的集成

```
战斗流程:

Round X Start:
  ├─ 检查被动技能
  │  ├─ Pilot闪避 (20%-41.5%, 根据等级) ← 升级后增强
  │  ├─ Engineer护盾 (20-60伤害吸收)     ← 升级后增强
  │  └─ Robot修复 (5-16能量, 每2-3回)   ← 升级后增强
  │
  ├─ Crew A 攻击
  │  ├─ Scientist分析 (1.3x-1.62x伤害)  ← 升级后更强
  │  └─ Soldier暴击 (40%-82.9% × 2.0-2.8x) ← 升级后更强
  │
  ├─ Threat 反击
  │  ├─ Medic治疗 (12-20能量恢复)         ← 升级后更强
  │  └─ (Engineer护盾继续吸收伤害)
  │
  └─ Crew B 同样过程
```

### 2️⃣ 战斗系统 → 升级系统的流程

```
任务完成 → 计算奖励 → 分配到队员

// 在 MissionControl.finishMission()

int difficulty = calculateDifficulty(threat, survivors);
int crystals = LootSystem.calculateCrystalReward(
    threat, 
    missionCount, 
    survivors
);

for (CrewMember crew : survivors) {
    crew.addSkillCrystals(crystals);  // ← 关键!
    updateUI_ShowAward(crew, crystals);
}

// 然后玩家进入训练中心升级
```

### 3️⃣ 升级系统 → 特殊技能系统的反馈

```
玩家升级技能 → 重新进行任务 → 感受到升级效果

例：升级 Soldier 暴击从 Lv.1 → Lv.2

之前 (Lv.1): 40% 触发 2.0x伤害 = 平均 +0.8倍伤害
升级后 (Lv.2): 48% 触发 2.2x伤害 = 平均 +1.06倍伤害

效果提升: 16% 更高的伤害输出
玩家感受: "咦？我的暴击好像更强了！"
```

---

## 💾 数据流和存档

### 数据模型层级

```
Colony (全局)
├─ Storage (所有宇航员数据)
│  └─ HashMap<CrewId, CrewMember>
│     └─ CrewMember
│        ├─ BaseAttributes
│        │  ├─ name, specialization
│        │  ├─ skill, resilience
│        │  ├─ energy, maxEnergy
│        │  └─ experience
│        │
│        ├─ SpecialAbility (多态!)
│        │  ├─ level (1-5)
│        │  ├─ totalCrystalsSpent
│        │  └─ canUse(), executeAbility()
│        │
│        ├─ Statistics
│        │  ├─ missionsCompleted
│        │  ├─ victoriesCount
│        │  └─ trainingSessions
│        │
│        └─ Resources
│           └─ skillCrystalsOwned ← 新增!
│
├─ Quarters
├─ Simulator  
└─ MissionControl
   └─ missionCount (用于难度计算)
```

### 存档序列化

```json
{
  "gameData": {
    "missionCount": 15,
    "globalCrystals": 45,
    "crew": [
      {
        "id": 1,
        "name": "Nova",
        "specialization": "Pilot",
        "skill": 5,
        "experience": 8,
        "skillCrystalsOwned": 12,
        "abilities": {
          "evasion": {
            "level": 2,
            "totalCrystalsSpent": 5
          }
        },
        "location": "MISSION_CONTROL",
        "energy": 18,
        "maxEnergy": 20,
        "stats": {
          "missionsCompleted": 5,
          "victoriesCount": 4,
          "trainingSessions": 3
        }
      },
      // ... 其他宇航员 ...
    ]
  }
}
```

---

## 🎯 游戏进度示例

### Day 1: 初体验

```
📍 Step 1: 招募队伍
  - 招募: Pilot(Nova), Soldier(Rex), Engineer(Maya)
  - 初始技能等级: 全部 Lv.1
  
📍 Step 2: 第一场任务
  - 对手: Kitchen Fire (简单)
  - 结果: 胜利! Nova升级Lv.2, Rex升级Lv.1
  - 奖励: 2 + 1(Nova) + 1(Rex) = 4晶
  
📍 Step 3: 继续2场简单任务
  - 每场获得 2 + 2(存活奖励) = 4晶
  - 总晶数: 4 + 4 + 4 = 12晶
  
📍 Step 4: 进入训练中心
  - 拥有晶数: 12
  - 升级 Soldier 暴击: Lv.1→Lv.2 (消耗5晶)
  - 升级 Pilot 闪避: Lv.1→Lv.2 (消耗5晶)
  - 剩余晶数: 12 - 5 - 5 = 2晶
```

### Day 2-3: 能力增强

```
📍 新一轮任务 (技能已升级)
  - Soldier 暴击: 40% → 48% (感受到提升!)
  - Pilot 闪避: 20% → 24% (更难被打中)
  
📍 3场NORMAL难度任务
  - 每场获得 4 + 3(存活奖励) = 7晶
  - 总晶数: 2 + 7 + 7 + 7 = 23晶
  
📍 再次升级
  - Engineer 护盾: Lv.1→Lv.2 (消耗5晶)
  - Scientist 分析: Lv.1→Lv.2 (消耗5晶)
  - 剩余晶数: 13晶
```

### 终局: 完全升级

```
📍 继续挑战难度更高的任务 (HARD/EXTREME)
  - 获得更多晶体奖励 (7-12晶/次)
  - 升级更多技能
  
📍 升级完成后的状态
  Pilot 闪避 (Lv.5): 41.5%触发率
  Engineer 护盾 (Lv.5): 60点吸收
  Medic 治疗 (Lv.5): 20点恢复
  Scientist 分析 (Lv.5): 1.62x伤害加成
  Soldier 暴击 (Lv.5): 82.9% × 2.8x伤害
  Robot 修复 (Lv.5): 16点能量, 每2回合
  
  总耗晶: 50 × 6 = 300晶
  需要任务数: ~40次NORMAL或15次EXTREME
```

---

## 🏗️ 实现建议(分阶段)

### **Phase 1: 基础(Week 1-2)**
✅ CrewMember 抽象类设计  
✅ 6种角色的子类实现  
✅ Storage 数据结构  
✅ 位置系统 (Quarters, Simulator, MissionControl)

### **Phase 2: 战斗(Week 2-3)**
✅ SpecialAbility 接口设计  
✅ 6个技能类实现  
✅ 基础伤害计算  
✅ 回合制战斗循环  
✅ 战斗日志输出

### **Phase 3: 升级系统(Week 3-4)** ← 这是新的
✅ SkillUpgrade 抽象类  
✅ 6个升级类实现  
✅ LootSystem 掉落计算  
✅ 训练中心UI

### **Phase 4: UI & 集成(Week 4-5)**
✅ 所有Activity 布局  
✅ RecyclerView 适配器  
✅ 数据绑定逻辑  
✅ 技能升级UI集成

### **Phase 5: 测试 & 优化(Week 5-6)**
✅ 单元测试  
✅ 难度平衡  
✅ 性能优化  
✅ 最终测试

---

## 🎓 OOP设计要点总结

### 1. 多态性 (Polymorphism)

```java
// 所有技能遵循同一接口
for (CrewMember crew : allCrew) {
    SpecialAbility ability = crew.getSpecialAbility();
    if (ability.canUse(crew, threat, ally)) {
        ability.executeAbility(crew, threat, ally);
    }
}

// 无论是哪种角色、哪个等级，都用同样的代码处理
```

### 2. 继承性 (Inheritance)

```
CrewMember (抽象基类)
  ├─ Pilot extends CrewMember
  ├─ Soldier extends CrewMember
  └─ Robot extends CrewMember

SkillUpgrade (抽象基类)
  ├─ PilotEvasionUpgrade extends SkillUpgrade
  ├─ SoldierCriticalUpgrade extends SkillUpgrade
  └─ RobotRepairUpgrade extends SkillUpgrade
```

### 3. 封装性 (Encapsulation)

```java
public class SkillUpgrade {
    private int currentLevel = 1;
    private int totalCrystalsSpent = 0;
    
    // 通过公有方法控制升级逻辑
    public boolean upgrade() { ... }
    public int getCostForNextLevel() { ... }
    
    // 不允许直接修改level
}
```

### 4. 抽象性 (Abstraction)

```java
public interface SpecialAbility {
    boolean canUse(CrewMember crew, Threat threat, CrewMember ally);
    void executeAbility(CrewMember crew, Threat threat, CrewMember ally);
    String getAbilityName();
    String getAbilityDescription();
}

// 实现者只需关注具体逻辑，不需要理解整个战斗系统
```

---

## 📊 项目总体成绩预估

### 基础分: 13分 ✅
- OOP设计
- 6种宇航员 ✅
- 回合制战斗 ✅
- 训练系统 ✅
- 恢复机制 ✅
- Android兼容 ✅
- 文档完整 ✅

### 奖励分: 最多13分

| 功能 | 分数 | 难度 | 是否实现 |
|------|------|------|---------|
| RecyclerView | +1 | ⭐ | 待 |
| 宇航员图像 | +1 | ⭐ | 待 |
| 任务可视化 | +2 | ⭐⭐ | 待 |
| 战斗随机性 | +1 | ⭐ | 待 |
| 胜率追踪 | +1 | ⭐ | 待 |
| **技能升级系统** | **+2** | **⭐⭐⭐** | 待 |
| **其他创新** | **+5** | 可选 | 待 |
| **总计** | **+13** | | |

### 总分预估: **26 / 13 = 2.0x** 🎉

---

## 📚 文档包含的内容

### IMPROVED_PROJECT_PLAN.md
- ✅ 项目概述 (包含Robot和6大技能系统)
- ✅ 功能详细说明
- ✅ UML类图 (包含SpecialAbility和升级系统)
- ✅ 工作分工
- ✅ 实现细节
- ✅ 所有奖励功能清单

### SPECIAL_ABILITIES_DESIGN.md
- ✅ SpecialAbility 接口完整代码
- ✅ 6个技能的详细设计 + Java代码
- ✅ 战斗中的集成逻辑
- ✅ 3个真实战斗场景示例
- ✅ OOP优势详解

### SKILL_UPGRADE_SYSTEM.md
- ✅ 掉落系统完整设计
- ✅ 每个技能的升级表
- ✅ Java代码框架
- ✅ 训练中心UI设计
- ✅ 存档系统集成
- ✅ 平衡设计解释

---

## 🚀 最终建议

1. **立即提交改进计划** 到老师（包含Robot + 6技能系统 + 升级系统）
2. **Zeyu开始编码** - 从Phase 1 和 Phase 2 开始，技能系统是核心
3. **Yuxiang准备UI** - 为training center设计界面，为技能升级做准备
4. **定期测试** - 每个Phase完成后都要测试集成

## ✨ 你们的项目真的很棒！

- 6个角色 × 6个不同技能 = 策略多样性 🎮
- 系统化的升级机制 = 长期目标激励 🎯
- 详细的文档 = 专业的态度 📋

**预计得分: 24-26分** (其中13分基础 + 11-13分奖励)

加油！只需要把设计转化为代码，你们就完成了一个真正的、成体系的游戏！ 🎉
