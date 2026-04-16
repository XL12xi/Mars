# 🎮 交互式回合制战斗系统 - 改造说明

## 📌 改动概览

火星殖民地游戏的战斗系统已升级为**交互式回合制战斗**，类似宝可梦的战斗方式。玩家现在可以在战斗中：

1. **选择宇航员** - 在两个宇航员之间选择要出战的
2. **选择动作** - 选择普通攻击或特殊能力
3. **观看战斗** - 实时观看战斗进程和伤害数据
4. **获得奖励** - 战斗胜利后获得经验和水晶

---

## 🏗️ 核心改动

### 1. **InteractiveBattle.java** (新建)
- **位置**: `src/main/java/com/mars/colony/game/InteractiveBattle.java`
- **功能**: 交互式战斗引擎
- **核心类**:
  ```java
  private enum BattlePhase {
      INITIALIZATION,      // 战斗初始化
      PLAYER_CREW_SELECT,  // 玩家选择宇航员
      PLAYER_ACTION_SELECT,// 玩家选择动作
      PLAYER_TURN,         // 执行玩家动作
      THREAT_TURN,         // 威胁反击
      BATTLE_END          // 战斗结束
  }
  ```

- **主要方法**:
  - `selectCrew(int crewIndex)` - 选择宇航员 (0=A, 1=B)
  - `selectAction(ActionType)` - 选择动作 (NORMAL_ATTACK 或 SPECIAL_ABILITY)
  - `executePlayerTurn()` - 执行玩家回合
  - `executeThreatTurn()` - 执行威胁反击
  - `getBattleStatus()` - 获取当前战斗状态用于 UI 显示

### 2. **MissionControl.java** (修改)
- **新增方法**:
  ```java
  // 启动交互式战斗
  InteractiveBattle startInteractiveMission(CrewMember crewA, CrewMember crewB, Threat threat);
  
  // 获取当前战斗实例
  InteractiveBattle getCurrentInteractiveBattle();
  
  // 完成战斗并分配奖励
  boolean finalizeInteractiveMission(InteractiveBattle battle);
  ```

### 3. **BattleActivity.java** (完全改造)
- **新增 UI 组件**:
  - 战斗阶段显示
  - 回合计数器
  - 两个宇航员的血量条和状态
  - 威胁的血量条和状态
  - 动作按钮容器 (动态创建)
  - 战斗日志

- **逻辑流程**:
  1. 初始化战斗
  2. 进入 PLAYER_CREW_SELECT 阶段，显示宇航员选择按钮
  3. 玩家选择后，进入 PLAYER_ACTION_SELECT 阶段，显示动作按钮
  4. 玩家选择动作后，自动执行 PLAYER_TURN
  5. 自动执行 THREAT_TURN (威胁反击)
  6. 返回步骤 2，直到战斗结束
  7. 显示战斗结果和完整日志

### 4. **activity_battle.xml** (完全改造)
- **新增布局结构**:
  ```
  标题
  ├─ 战斗阶段/回合信息
  ├─ 战斗结果提示
  ├─ 战斗状态区域
  │  ├─ 宇航员 A (血量条)
  │  ├─ 宇航员 B (血量条)
  │  └─ 威胁 (血量条)
  ├─ 动作按钮容器 (动态)
  ├─ 战斗日志 (滚动)
  └─ 返回菜单按钮
  ```

---

## 🎮 使用流程

### 在 Android 应用中

1. **启动战斗** (来自 MissionControlActivity):
   ```java
   Intent intent = new Intent(this, BattleActivity.class);
   intent.putExtra("crew_a_name", "Nova");
   intent.putExtra("crew_b_name", "Rex");
   intent.putExtra("threat_name", "Asteroid Storm");
   intent.putExtra("threat_type", "MeteorStorm");
   intent.putExtra("threat_skill", 7);
   intent.putExtra("threat_resilience", 3);
   intent.putExtra("threat_max_energy", 25);
   startActivity(intent);
   ```

2. **玩家操作**:
   - 等待"选择宇航员"提示
   - 点击要出战的宇航员按钮
   - 等待"选择动作"提示
   - 点击要使用的动作 (普通攻击或特殊能力)
   - 观看战斗日志中的伤害显示
   - 威胁自动反击
   - 重复，直到战斗结束

3. **战斗结束**:
   - 显示胜利或失败
   - 自动分配奖励
   - 点击"返回菜单"回到主界面

---

## 🔄 战斗阶段详解

### PLAYER_CREW_SELECT
```
UI: 显示两个还活着的宇航员的按钮
   ┌──────────────────────┐
   │ Nova (飞行员)        │
   │ HP: 20/20 ✓          │
   └──────────────────────┘
   ┌──────────────────────┐
   │ Rex (工程师)         │
   │ HP: 19/19 ✓          │
   └──────────────────────┘
```

### PLAYER_ACTION_SELECT
```
UI: 显示选中宇航员的可用动作
   ┌──────────────────────┐
   │ Normal Attack        │
   └──────────────────────┘
   ┌──────────────────────┐
   │ Special Ability:     │
   │ Evasion (Lv.2)       │
   └──────────────────────┘
```

### PLAYER_TURN / THREAT_TURN (自动执行)
```
日志输出:
> Round 1: Nova selected to act
    Nova uses NORMAL ATTACK: 7 damage
    Asteroid Storm HP: 18 / 25
  Asteroid Storm retaliates: 4 damage to Nova
    Nova HP: 16 / 20
```

---

## 📊 战斗状态信息 (BattleStatus)

```java
public static class BattleStatus {
    public BattlePhase phase;           // 当前阶段
    public int round;                  // 当前回合数
    
    // 宇航员 A
    public String crewAName;
    public int crewAEnergy, crewAMaxEnergy;
    public boolean crewAAlive;
    public String crewASpec;
    
    // 宇航员 B
    public String crewBName;
    public int crewBEnergy, crewBMaxEnergy;
    public boolean crewBAlive;
    public String crewBSpec;
    
    // 威胁
    public String threatName;
    public int threatEnergy, threatMaxEnergy;
    public boolean threatAlive;
    
    // 战斗状态
    public int selectedCrewIndex;
    public boolean battleOver;
}
```

---

## ✨ 新功能亮点

### 1. **选择宇航员**
- 每次攻击前，玩家可以在两个宇航员之间选择
- 只有还活着的宇航员可以选择
- 允许灵活的战术搭配

### 2. **选择动作**
- **普通攻击**: `伤害 = 技能 + 经验 - 敌人防御`
- **特殊能力**: 根据宇航员职业，执行特殊效果
  - 飞行员: 闪避 (避免部分伤害)
  - 工程师: 护盾 (减免伤害)
  - 医生: 治疗 (恢复能量)
  - 科学家: 分析 (增加伤害倍率)
  - 士兵: 暴击 (获得高倍伤害)
  - 机器人: 自修复 (恢复小部分能量)

### 3. **实时视觉反馈**
- 血量条实时更新
- 每个动作都在日志中记录
- 显示伤害数字和效果

### 4. **智能奖励系统**
- 战斗胜利后自动计算奖励
- 掉落水晶数量根据难度和幸存者数量
- 每个幸存者获得经验值

---

## 🛠️ 演示程序

### InteractiveBattleDemo.java
- **位置**: `src/main/java/com/mars/colony/InteractiveBattleDemo.java`
- **用途**: 命令行演示如何使用交互式战斗系统
- **运行**: `java -cp . com.mars.colony.InteractiveBattleDemo`

---

## 📋 文件清单

| 文件 | 状态 | 说明 |
|------|------|------|
| InteractiveBattle.java | 新建 | 交互式战斗引擎 |
| InteractiveBattleDemo.java | 新建 | 命令行演示程序 |
| MissionControl.java | 修改 | 添加交互式战斗支持 |
| BattleActivity.java | 改造 | 交互式战斗 UI |
| activity_battle.xml | 改造 | 新的战斗布局 |

---

## 🎯 下一步

1. ✅ 交互式战斗系统完成
2. ⏳ 其他 Activities 集成 (MissionControlActivity 需要调整启动战斗的方式)
3. ⏳ UI 效果优化 (添加动画、音效等)
4. ⏳ 更多战术选项 (组合技能、队友配合等)

---

## 💡 技术细节

### 状态机设计
```
INITIALIZATION
    ↓
PLAYER_CREW_SELECT (需要玩家输入)
    ↓
PLAYER_ACTION_SELECT (需要玩家输入)
    ↓
PLAYER_TURN (自动执行)
    ↓
(威胁是否还活着?)
不是 → BATTLE_END
是 ↓
THREAT_TURN (自动执行)
    ↓
(两个宇航员都死了?)
是 → BATTLE_END
不是 ↓
返回 PLAYER_CREW_SELECT
```

### 线程和 UI 更新
```java
// 使用 runOnUiThread 更新 UI
runOnUiThread(() -> {
    tvBattleLog.append(currentBattle.getBattleLog());
    svBattleLog.post(() -> svBattleLog.fullScroll(ScrollView.FOCUS_DOWN));
    // 延迟 1.5 秒后继续下一回合
    getWindow().getDecorView().postDelayed(this::updateBattleUI, 1500);
});
```

---

## 🐛 已知问题 & 待改进

1. **动画延迟**: 目前使用硬编码的 1.5 秒延迟，可以改为更灵活的动画系统
2. **并发处理**: 多次快速点击可能导致状态错乱，建议添加点击锁定
3. **AI 改进**: 威胁目前没有智能选择目标，可以增加目标选择逻辑
4. **视觉效果**: 可以添加伤害数字飘出、技能特效等

