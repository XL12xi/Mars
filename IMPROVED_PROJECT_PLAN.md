# Space Colony - 改进的项目计划

## 1. 团队信息
- **团队成员**
  - Yuxiang Lu (学号: 001754290)
  - Zeyu Liu (学号: 001220669)
- **开发方法**: 配对编程（Pair Programming）
- **开发语言**: Java (Android Studio)
- **目标平台**: Android 8.0+

---

## 2. 项目概述

### 2.1 核心概念
本项目是一个太空殖民地管理游戏，玩家通过招募、训练和派遣宇航员完成任务。游戏机制受到Pokémon启发，但不包含交换功能。核心三大循环：
- **招募** - 创建不同专业的宇航员
- **训练** - 在模拟器中提升宇航员能力
- **战斗** - 两人小队合作对抗系统生成的威胁

### 2.2 游戏特色
- 6种独特的宇航员专业化，各具不同属性和能力
- **每个角色拥有专属特殊技能**，实现多态战斗系统
- 合作回合制战斗系统
- 经验和能量成长系统
- 视觉化的战斗日志和实时健康条更新
- 特殊事件：暴击系统、战斗随机性、技能触发

### 2.3 核心创新点
**特殊技能系统**（OOP多态的亮点实现）
- 应用OOP多态设计：每个角色定义不同的SpecialAbility子类
  - PilotEvasion - 20%概率躲避伤害
  - EngineerShield - 400消耗护盾（-20伤害）
  - MedicHealing - 消耗能量治疗队友
  - ScientistAnalysis - 智能加伤
  - SoldierStrike - 40%暴击
  - RobotRepair - 自动修复（每3回）
- 结果：每场战斗都充满策略变化，相同敌人遇上不同队伍配合会产生完全不同的战斗过程

**Robot角色的创意添加**
- 相比原项目5个回色：+1新角色
- 独特的自修复被动技能（每3回合恢复5能量）→ 长期作战型
- 打破"攻速"vs"韧性"的经典博弈 → 引入"续航"维度

---

## 3. 基础功能详细说明（13分）

### 3.1 宇航员管理系统

#### 3.1.1 宇航员初始属性表
| 专业 | 技能 | 韧性 | 最大能量 | 颜色 | 特殊技能 |
|------|------|------|---------|------|----------|
| Pilot (飞行员) | 5 | 4 | 20 | 蓝 | 【闪避】20%概率完全闪避伤害 |
| Engineer (工程师) | 6 | 3 | 19 | 黄 | 【护盾】消耗5能量抵挡20伤害 |
| Medic (医疗兵) | 7 | 2 | 18 | 绿 | 【急救】消耗8能量恢复队友12点能量 |
| Scientist (科学家) | 8 | 1 | 17 | 紫 | 【智能分析】伤害+30%针对特定威胁 |
| Soldier (士兵) | 9 | 0 | 16 | 红 | 【绝对打击】40%概率造成2倍伤害 |
| Robot (机器人) | 7 | 2 | 22 | 银 | 【自我修复】每3回合自动恢复5点能量 |

#### 3.1.2 宇航员数据结构
```
CrewMember (基类)
├─ name: String              // 姓名
├─ specialization: String    // 专业类型
├─ skill: int                // 基础技能值
├─ resilience: int           // 韧性值
├─ energy: int               // 当前能量
├─ maxEnergy: int            // 最大能量
├─ experience: int           // 经验值
├─ status: int               // 图片资源ID
├─ missionsCompleted: int    // 已完成任务数
├─ victoriesCount: int       // 胜利数
└─ trainingSessions: int     // 训练次数
```

#### 3.1.3 宇航员类层级
```
CrewMember (抽象基类)
├─ Pilot
├─ Engineer
├─ Medic
├─ Scientist
├─ Soldier
└─ Robot
```

### 3.2 位置系统

#### 3.2.1 Quarters（宿舍）
- **功能**: 宇航员恢复和管理
- **机制**: 
  - 宇航员返回时能量恢复至满
  - 经验值保留
  - 可以移动宇航员到其他地点
- **数据**: 存储休息中的宇航员列表

#### 3.2.2 Simulator（模拟器）
- **功能**: 训练宇航员获得经验
- **机制**:
  - 每次训练奖励1-3点经验
  - 训练消耗5-10点能量
  - 经验影响技能值（经验值 = 技能加成）
- **操作**: 选择宇航员、点击训练按钮

#### 3.2.3 MissionControl（任务控制）
- **功能**: 发起和管理任务
- **机制**: 选择2名宇航员、生成威胁、启动战斗

### 3.3 训练系统

#### 3.3.1 训练机制
```
trainSelectedCrew():
  - 选择要训练的宇航员
  - 每个被选中的宇航员：
    * 获得 1-3 点经验（可随机）
    * 消耗 5-10 点能量
    * 训练计数 +1
  - 经验直接转化为技能加成
```

#### 3.3.2 经验系统
- 每训练一次获得 1-3 点经验（随机）
- 经验不会清空，仅当宇航员被移除时消失
- 经验直接增加技能：`effectiveSkill = baseSkill + experience`

### 3.4 合作任务系统

#### 3.4.1 任务难度系统
```
threatSkill = 4 + (missionCount × 0.5)
threatResilience = 2 + (missionCount × 0.3)
threatMaxEnergy = 20 + (missionCount × 2)
```

#### 3.4.2 回合制战斗算法
```
while (threat.energy > 0 && (crewA.energy > 0 || crewB.energy > 0)):
    // 轮次开始
    Round X:
    
    // 宇航员A的回合
    if crewA.energy > 0:
        damage = crewA.act() - threat.defend()
        threat.energy -= max(1, damage)
        logMessage("$ {crewA.name} dealt {damage} damage")
        
        if threat.energy > 0:
            damage = threat.act() - crewA.defend()
            crewA.energy -= max(1, damage)
            logMessage("$ {threat.name} dealt {damage} damage")
            
            if crewA.energy <= 0:
                logMessage("$ {crewA.name} defeated!")
                crewA = null  // 宇航员被移除
    
    // 宇航员B的回合
    if crewB.energy > 0:
        damage = crewB.act() - threat.defend()
        threat.energy -= max(1, damage)
        logMessage("$ {crewB.name} dealt {damage} damage")
        
        if threat.energy > 0:
            damage = threat.act() - crewB.defend()
            crewB.energy -= max(1, damage)
            logMessage("$ {threat.name} dealt {damage} damage")
            
            if crewB.energy <= 0:
                logMessage("$ {crewB.name} defeated!")
                crewB = null  // 宇航员被移除

// 战斗结束检查
if threat.energy <= 0:
    result = "VICTORY"
    // 奖励经验
    if crewA alive: crewA.experience += 2
    if crewB alive: crewB.experience += 2
    return "The threat neutralized! Crew gains experience"
elif crewA == null && crewB == null:
    result = "DEFEAT"
    remove both crew members
    return "Mission failed. All crew lost."
```

#### 3.4.3 伤害计算
```
damage = attacker.skill - defender.resilience
finalDamage = max(1, damage)  // 最少1点伤害
```

### 3.5 **特殊技能系统（创新核心）**

每个角色都有独特的特殊技能，实现OOP多态设计。技能可在战斗中按需使用。

#### 3.5.1 Pilot - 【闪避】
```
触发概率: 20%（自动触发）
效果:
  - 完全闪避一次伤害
  - 减少受伤 = 0
  - 日志: "[EVASION!] Pilot dodges the attack!"
实现:
  if Math.random() < 0.2:
      return 0  // 伤害为0
```

#### 3.5.2 Engineer - 【护盾】
```
触发条件: 玩家主动使用（消耗5点能量）
效果:
  - 部署防御护盾
  - 该回合承受的伤害减少20
  - 护盾持续1回合
  - 日志: "[SHIELD!] Engineer deploys protection barrier!"
实现:
  if crew.energy >= 5 && crew.toggleShield():
      crew.energy -= 5
      crew.shieldActive = true
      crew.shieldDuration = 1
      incomingDamage = max(1, incomingDamage - 20)
```

#### 3.5.3 Medic - 【急救】
```
触发条件: 玩家主动使用（消耗8点能量）
效果:
  - 恢复队友12点能量（包括自己）
  - 仅在队友生存时有效
  - 日志: "[HEALING!] Medic restores 12 energy to teammate!"
实现:
  if crew.energy >= 8 && crew.toggleHealing(target):
      crew.energy -= 8
      target.energy = min(target.maxEnergy, target.energy + 12)
      if crew == target:
          logMessage("Medic heals themselves")
      else:
          logMessage("Medic heals " + target.name)
```

#### 3.5.4 Scientist - 【智能分析】
```
触发概率: 根据威胁类型35%触发
效果:
  - 分析敌人弱点
  - 伤害+30%
  - 可针对特定威胁类型（如 MeteorStorm、AlienAttack等）
  - 日志: "[ANALYSIS!] Scientist identified weakness!"
实现:
  if threat.type in scientistCounters && Math.random() < 0.35:
      damage = damage * 1.3
      logMessage("[ANALYSIS] Damage multiplier: 1.3x")
```

#### 3.5.5 Soldier - 【绝对打击】
```
触发概率: 40%
效果:
  - 强力一击
  - 造成2倍伤害
  - 日志: "[CRITICAL STRIKE!] Soldier lands a devastating blow!"
实现:
  if Math.random() < 0.4:
      damage = damage * 2.0
      logMessage("[CRITICAL STRIKE!] 2x Damage!")
```

#### 3.5.6 Robot - 【自我修复】
```
触发时机: 每3个回合自动触发
效果:
  - 自动恢复5点能量
  - 无需消耗其他资源
  - 日志: "[REPAIR CYCLE] Robot restores 5 energy!"
实现:
  if robot.roundCount % 3 == 0:
      robot.energy = min(robot.maxEnergy, robot.energy + 5)
      logMessage("[REPAIR CYCLE] Robot repairs itself!")
```

#### 3.5.7 技能系统类设计
```
interface SpecialAbility {
    boolean canUse(CrewMember crew);
    void executeAbility(CrewMember crew, Threat threat, CrewMember ally);
    String getAbilityName();
    String getAbilityDescription();
}

abstract class CrewMember {
    protected SpecialAbility specialAbility;
    
    public void useSpecialAbility(Threat threat, CrewMember ally) {
        if (specialAbility.canUse(this)) {
            specialAbility.executeAbility(this, threat, ally);
        }
    }
}

class PilotEvasion implements SpecialAbility {
    @Override
    public boolean canUse(CrewMember crew) {
        return Math.random() < 0.2;  // 20% 自动触发
    }
    
    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        // 设置标记，使得下一次伤害为0
        ((Pilot)crew).setEvading(true);
    }
}

// 类似地实现其他5个技能类
```

### 3.6 数据存储架构

#### 3.6.1 Storage类
```
Storage
├─ crewMap: HashMap<Integer, CrewMember>
│   ├─ key: 宇航员唯一ID
│   └─ value: CrewMember对象
└─ 方法:
   ├─ addCrew(id, crewMember)
   ├─ removeCrew(id)
   └─ getCrew(id)
```

#### 3.6.2 位置管理
```
Colony
├─ quarters: ArrayList<CrewMember>      // 宿舍中的宇航员
├─ simulator: ArrayList<CrewMember>     // 训练中的宇航员
├─ missionControl: ArrayList<CrewMember> // 待命的宇航员
├─ missionCount: static int             // 已完成任务数（用于难度计算）
└─ 方法:
   ├─ moveToQuarters(crewMember)
   ├─ moveToSimulator(crewMember)
   ├─ moveToMissionControl(crewMember)
   └─ getCrewByLocation(location)
```

#### 3.6.3 威胁系统
```
Threat
├─ name: String
├─ skill: int
├─ resilience: int
├─ energy: int
├─ maxEnergy: int
├─ 方法:
│  ├─ act(): int              // 返回攻击力
│  └─ defend(damage: int)    // 返回防御值
└─ 特点:
   └─ 不同威胁类型（陨石风暴、外星人、系统故障等）
```

---

## 4. 奖励功能说明（最多13分！新增技能升级系统）

### 4.1 宇航员图像 (+1分)
- **实现**: 为6种专业分別创建/使用图像资源
  - Pilot (蓝色）
  - Engineer (黄色）
  - Medic (绿色）
  - Scientist (紫色）
  - Soldier (红色）
  - Robot (银色）
- **技术**: 在RecyclerView中展示crew_image.png
- **技术栈**: Android drawable resources

### 4.2 不死机制 (+1分)
- **实现**: 宇航员能量降为0时不删除，而是送往医疗室
- **流程**:
  - 宇航员被送回Quarters
  - 能量恢复到满
  - 属性初始化但保留30%经验
  - 标记为"恢复中"状态
- **UI**: 展示恢复状态的视觉反馈

### 4.3 任务可视化 (+2分)
- **战斗日志**:
  - ScrollView显示实时战斗信息
  - 每个回合分行显示：
    ```
    --- Round X ---
    [CrewA] attacks [Threat]
      Damage: Y
      [Threat] Energy: Z/Max
    [Threat] retaliates against [CrewA]
      Damage: X
      [CrewA] Energy: W/Max
    ```
- **健康条**:
  - 使用ProgressBar实时更新
  - 宇航员血条（蓝色）、威胁血条（红色）
  - 动画过渡效果

### 4.4 战斗随机性 (+1分)
- **暴击系统**:
  ```
  isCritical = Math.random() < 0.2;  // 20%暴击率
  if (isCritical):
      damage = (skill - resilience) × 1.5
      logMessage("[CRITICAL HIT!] {damage} damage!")
  ```
- **伤害波动**: `damage = skill - resilience + (Math.random() * 2 - 1)`

### 4.5 胜率追踪 (+1分)
- **数据追踪**:
  - 每个宇航员记录: victoriesCount, missionsCompleted
  - 胜率 = victoriesCount / missionsCompleted
- **UI展示**:
  - 宇航员详情页显示统计信息
  - 列表中显示徽章（如"无敌"等）

### 4.6 **技能升级系统** (+2分) ✨ 新增创新功能
- **核心机制**:
  - 完成任务后掉落**技能水晶**（根据难度获得数量）
    - EASY: 2晶
    - NORMAL: 4晶
    - HARD: 7晶
    - EXTREME: 12晶
  - 额外奖励：存活队员 +1晶/人，获得经验 +1晶/人
  
- **升级系统**:
  - 每个角色的特殊技能可升级到5级
  - 升级消耗线性递增：升到Lv.2消耗5晶，Lv.3消耗10晶，Lv.4消耗15晶，Lv.5消耗20晶
  - 总消耗50晶达到满级
  
- **效果提升**（各技能不同）:
  - Pilot (闪避)：触发率 20% → 41.5%（每级×1.2）
  - Engineer (护盾)：护盾值 20 → 60点（每级+10）
  - Medic (治疗)：治疗量 12 → 20点（每级+2）
  - Scientist (分析)：伤害加成1.3x → 1.62x（每级×1.2）
  - Soldier (暴击)：暴击率40% → 82.9%（每级×1.2），伤害2.0x → 2.8x
  - Robot (修复)：修复量5 → 16点，周期3回 → 2回
  
- **UI实现**:
  - 新增"训练中心"Activity展示所有升级进度
  - RecyclerView列表显示每个技能的当前等级和升级成本
  - 升级按钮+确认对话框
  - 晶体不足提示
  
- **存档集成**:
  - 所有升级等级被保存到存档
  - 下次加载时恢复所有升级状态

---

## 5. UI 流程图

```
启动应用
  ↓
[主菜单/首页]
  ├→ [招募] → 输入名字、选择专业 → 确认 → 返回首页
  ├→ [宿舍] → 列表显示 → 选择/移动 → 返回首页
  ├→ [模拟器] → 列表显示 → 选择 → 训练 → 返回首页
  └→ [任务控制] → 选择2人 → 生成威胁 → 开始战斗
       ↓
     [战斗界面]
       ├→ 显示参战者和威胁信息
       ├→ 回合制战斗（自动/手动选择行动）
       ├→ 实时战斗日志和血条更新
       └→ 战斗结束 → 结果页面 → 返回任务控制/首页
```

---

## 6. 工作分工与时间规划

### 6.1 开发阶段

| 阶段 | 负责人 | 时间 | 任务 |
|------|--------|------|------|
| 基础架构 | 两人 | Week 1-2 | 创建所有基础类、数据结构、Storage类、Robot角色 |
| **特殊技能系统** | **Zeyu** | **Week 1.5-2.5** | **实现SpecialAbility接口及6个技能类(闪避、护盾、急救、智能分析、绝对打击、自修复)** |
| UI框架 | Yuxiang | Week 2-3 | 实现所有Activity布局和RecyclerView、Robot展示 |
| 游戏逻辑 | Zeyu | Week 2-3 | 实现训练、战斗算法、威胁生成 |
| 技能战斗集成 | 两人 | Week 3-3.5 | 技能与战斗系统一体化、日志显示 |
| 集成测试 | 两人 | Week 3.5-4 | 连接所有组件、测试流程 |
| 奖励功能 | 两人 | Week 4-5 | 实现所有奖励功能 |
| 调试优化 | 两人 | Week 5-6 | bug修复、性能优化、最终测试 |

### 6.2 代码分工（更新中）
- **Yuxiang Lu**:
  - UI/Activity层
  - RecyclerView适配器
  - 图像资源管理
  - Robot及其他角色的UI展示
  
- **Zeyu Liu**:
  - 核心游戏逻辑
  - 数据结构实现
  - 战斗算法
  - **特殊技能系统设计与实现** 🎯（核心创新）
  - 威胁生成
  - 所有6个角色的技能类实现

---

## 7. 技术栈

### 7.1 开发环境
- Android Studio 2024.1+
- Java 11+
- Target API: Android 31+

### 7.2 核心库
- AndroidX (AppCompat, RecyclerView, ConstraintLayout)
- Material Design Components

### 7.3 可能使用的库（可选）
- Gson - JSON序列化（数据存储）
- Room - 本地数据库（如实现存储/加载功能）

---

## 8. UML 类图详解

### 8.1 核心类关系

**继承层级**:
```
CrewMember (抽象基类)
   ├─ Pilot
   ├─ Engineer
   ├─ Medic
   ├─ Scientist
   ├─ Soldier
   └─ Robot
```

**接口层级**:
```
SpecialAbility (接口)
   ├─ PilotEvasion
   ├─ EngineerShield
   ├─ MedicHealing
   ├─ ScientistAnalysis
   ├─ SoldierStrike
   └─ RobotRepair
```

**聚合关系**:
- Colony **聚合** Simulator、MissionControl、Quarters
- Storage **包含** HashMap<Integer, CrewMember>
- MissionControl **生成** Threat
- CrewMember **组成** SpecialAbility

**依赖关系**:
- MissionControl 依赖 CrewMember
- MissionControl 依赖 Threat
- CrewMember 依赖 SpecialAbility

### 8.2 主要类职责

| 类名 | 职责 | 关键方法 |
|------|------|---------|
| CrewMember | 表示宇航员，包含专技能 | act(), defend(), train(), useSpecialAbility() |
| Pilot | 飞行员，具朠0%闪避力 | getEvaded() |
| Engineer | 工程师，可呴起护盾 | toggleShield() |
| Medic | 医疗兵，可以加慾队友 | toggleHealing(target) |
| Scientist | 科学家，有针对威胁加伤 | getCounterBonus(threat) |
| Soldier | 士兵，高把握打击 | getCriticalStrike() |
| Robot | 机器人，自动自我修复 | getRepairStatus(), repair() |
| SpecialAbility | 技能接口 | canUse(), executeAbility() |
| Storage | 数据持久化 | addCrew(), removeCrew(), getAllCrew() |
| Colony | 管理整个殖民地 | moveCrewTo(), getLocationList() |
| Simulator | 训练管理 | trainSelected() |
| MissionControl | 任务2管理 | startMission(), generateThreat() |
| Threat | 敵人 | act(), defend(), takeDamage() |

---

## 9. 实现细节补充

### 9.1 异常处理
- 选择任务时宇航员数量<2的检查
- 无效输入验证
- 能量不足处理

### 9.2 UI交互
- 长按列表查看详细信息
- 复选框多选机制
- Toast/Dialog提示用户操作结果

### 9.3 保存机制（如实现数据存储奖励功能）
```
保存位置: /sdcard/Android/data/com.mars.colony/files/
保存格式: JSON
保存内容: 所有CrewMember对象 + MissionCount + 位置信息
```

---

## 10. 测试计划

### 10.1 单元测试
- 伤害计算公式
- 经验增长
- 能量管理

### 10.2 集成测试
- 完整游戏流程（招募→训练→战斗）
- 位置移动逻辑
- 战斗结果正确性

### 10.3 用户体验测试
- UI响应速度
- 列表滚动流畅度
- 不同屏幕尺寸兼容性

---

## 11. 提交物清单

- ✅ GitHub公开仓库链接
- ✅ 项目文档（本文件 IMPROVED_PROJECT_PLAN.md）
- ✅ **特殊技能系统设计文档** (SPECIAL_ABILITIES_DESIGN.md)
- ✅ **技能升级系统设计文档** (SKILL_UPGRADE_SYSTEM.md)
- ✅ UML类图（包含SpecialAbility接口和Robot角色）
- ✅ 演示视频（5-10分钟）
- ✅ 源代码（Android项目）
- ✅ README.md（安装说明）
- ✅ 团队贡献说明
- ✅ AI使用声明

---

## 12. 修改日志

| 日期 | 版本 | 变更 | 说明 |
|------|------|------|------|
| 2026-04-08 | v1.0 | 初版 | 根据教师反馈补充详细内容 |
| 2026-04-08 | v1.1 | 新增 | 添加Robot角色和6个技能系统设计 |
| 2026-04-08 | v1.2 | 新增 | 添加技能升级系统（晶体掉落+升级机制） |
