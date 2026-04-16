# 特殊技能系统详细设计文档

## 概述
本文档详细说明了Space Colony游戏中6种角色的特殊技能系统实现方案。

这是一个**OOP多态设计的完美案例**，每个角色有不同的技能触发机制和效果。

---

## 1. SpecialAbility 接口

所有特殊技能都基于统一的接口：

```java
/**
 * 特殊技能的统一接口
 */
public interface SpecialAbility {
    /**
     * 检查是否可以使用该技能
     */
    boolean canUse(CrewMember crew, Threat threat, CrewMember ally);
    
    /**
     * 执行技能效果
     */
    void executeAbility(CrewMember crew, Threat threat, CrewMember ally);
    
    /**
     * 获取技能名称
     */
    String getAbilityName();
    
    /**
     * 获取技能描述
     */
    String getAbilityDescription();
}
```

---

## 2. 6种技能详细设计

### 2.1 Pilot（飞行员）- 【闪避】

**触发机制**：被动自动触发  
**触发概率**：20%  
**冷却时间**：无（每次伤害都可能触发）

**技能效果**：
- 当受到伤害时，有20%概率完全闪避该次伤害
- 伤害减少至0
- 战斗日志显示："[EVASION!] Pilot dodges the attack!"

**实现代码**：
```java
public class PilotEvasion implements SpecialAbility {
    private static final float EVASION_RATE = 0.2f;
    
    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        // 被动技能，等待被触发
        // 在 takeDamage() 中调用此方法
        return Math.random() < EVASION_RATE;
    }
    
    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        // 通过返回0伤害实现（需要在takeDamage中修改）
        Pilot pilot = (Pilot) crew;
        pilot.setLastDamageNegated(true);
    }
    
    @Override
    public String getAbilityName() {
        return "Evasion";
    }
    
    @Override
    public String getAbilityDescription() {
        return "20% chance to completely evade incoming attacks";
    }
}
```

**战斗场景示例**：
```
Round 1:
Asteroid Storm attacks Pilot(Nova)
  Damage: 6 - 4 = 2
  [EVASION!] Pilot dodges the attack!
  Pilot(Nova) energy: 20/20 (unchanged)
```

---

### 2.2 Engineer（工程师）- 【护盾】

**触发机制**：玩家主动使用 或 自动触发（可选）  
**能量消耗**：5  
**护盾效果**：减少20点伤害  
**持续时间**：1回合

**技能效果**：
- 消耗5点能量部署护盾
- 在护盾激活期间，所有接收的伤害减少20点
- 护盾持续1回合后失效
- 多次部署时护盾效果可叠加（高级设计）
- 战斗日志显示："[SHIELD!] Engineer deploys protection barrier!"

**实现代码**：
```java
public class EngineerShield implements SpecialAbility {
    private static final int SHIELD_COST = 5;
    private static final int SHIELD_VALUE = 20;  // 减少伤害
    private static final int SHIELD_DURATION = 1;
    
    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        Engineer engineer = (Engineer) crew;
        return engineer.getEnergy() >= SHIELD_COST && !engineer.isShieldActive();
    }
    
    @Override  
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        Engineer engineer = (Engineer) crew;
        engineer.setEnergy(engineer.getEnergy() - SHIELD_COST);
        engineer.setShieldActive(true);
        engineer.setShieldDuration(SHIELD_DURATION);
        engineer.setShieldValue(SHIELD_VALUE);
    }
    
    @Override
    public String getAbilityName() {
        return "Shield";
    }
    
    @Override
    public String getAbilityDescription() {
        return "Spend 5 energy to deploy a shield that reduces incoming damage by 20";
    }
}
```

**战斗场景示例**：
```
Round 2:
Engineer(Maya) casts Shield
  Energy cost: 5
  Engineer(Maya) energy: 14/19
  Shield deployed! (Duration: 1 turn)

Asteroid Storm attacks Engineer(Maya)
  Base damage: 6 - 3 = 3
  Shield effect: 3 - 20 = -17 (negated!)
  Engineer(Maya) takes 0 damage (shield absorbed it all)
  Engineer(Maya) energy: 14/19

After round 2: Shield expires
```

---

### 2.3 Medic（医疗兵）- 【急救】

**触发机制**：玩家主动使用（选择目标队友或自己）  
**能量消耗**：8  
**治疗效果**：恢复12点能量  
**冷却时间**：无

**技能效果**：
- 消耗8点能量
- 恢复选中目标12点能量（包括自己）
- 最多恢复至maxEnergy
- 战斗日志显示："[HEALING!] Medic restores 12 energy to [Target]!"

**实现代码**：
```java
public class MedicHealing implements SpecialAbility {
    private static final int HEAL_COST = 8;
    private static final int HEAL_AMOUNT = 12;
    
    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        Medic medic = (Medic) crew;
        // 检查是否有足够能量且有需要治疗的队友
        return medic.getEnergy() >= HEAL_COST && 
               (medic.getEnergy() < medic.getMaxEnergy() || 
                ally.getEnergy() < ally.getMaxEnergy());
    }
    
    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        Medic medic = (Medic) crew;
        medic.setEnergy(medic.getEnergy() - HEAL_COST);
        
        // 治疗队友或自己（通常优先队友）
        CrewMember target = (ally.getEnergy() < ally.getMaxEnergy()) ? ally : medic;
        int newEnergy = Math.min(target.getMaxEnergy(), 
                                target.getEnergy() + HEAL_AMOUNT);
        target.setEnergy(newEnergy);
    }
    
    @Override
    public String getAbilityName() {
        return "Healing";
    }
    
    @Override
    public String getAbilityDescription() {
        return "Spend 8 energy to heal an ally (or yourself) for 12 energy";
    }
}
```

**战斗场景示例**：
```
Round 3:
Pilot(Nova) energy: 8/20 (critical!)
Medic(Dr.Lee) casts Healing on Pilot(Nova)
  Energy cost: 8
  Medic(Dr.Lee) energy: 10/18
  Pilot(Nova) healed: 8 + 12 = 20/20 (full recovery!)
  [HEALING!] Medic restores 12 energy to Pilot!
```

---

### 2.4 Scientist（科学家）- 【智能分析】

**触发机制**：自动触发（基于威胁类型）  
**触发概率**：35%（当威胁是科学家克制类型时）  
**伤害加成**：+30%

**技能效果**：
- 自动识别某些威胁类型（如"ToxinLeak", "RadiationStorm"等）
- 对克制威胁造成30%更多伤害
- 其他威胁类型下技能不触发
- 战斗日志显示："[ANALYSIS!] Scientist identifies weakness! Damage: 1.3x"

**实现代码**：
```java
public class ScientistAnalysis implements SpecialAbility {
    private static final float DAMAGE_MULTIPLIER = 1.3f;
    private static final float TRIGGER_RATE = 0.35f;
    
    // 科学家克制的威胁类型
    private static final String[] COUNTER_THREATS = {
        "ToxinLeak", "RadiationStorm", "ChemicalFire", 
        "BiologicalHazard", "LabAccident"
    };
    
    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        Scientist scientist = (Scientist) crew;
        return isThreatType(threat) && Math.random() < TRIGGER_RATE;
    }
    
    private boolean isThreatType(Threat threat) {
        String threatName = threat.getName();
        for (String counter : COUNTER_THREATS) {
            if (threatName.contains(counter)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        // 伤害加成在战斗逻辑中应用
        Scientist scientist = (Scientist) crew;
        scientist.setDamageMultiplier(DAMAGE_MULTIPLIER);
    }
    
    @Override
    public String getAbilityName() {
        return "Analysis";
    }
    
    @Override
    public String getAbilityDescription() {
        return "35% chance to analyze weakness: +30% damage on specific threats";
    }
}
```

**战斗场景示例**：
```
Threat: Radiation Storm (克制type)

Round 1:
Scientist(Prof.Chen) acts against Radiation Storm
  Base damage: 8 - 2 = 6
  [ANALYSIS!] Scientist identifies weakness!
  Damage multiplier: 1.3x
  Final damage: 6 × 1.3 = 7.8 ≈ 8
  Radiation Storm energy: 17/25
```

---

### 2.5 Soldier（士兵）- 【绝对打击】

**触发机制**：自动触发（每次攻击）  
**触发概率**：40%  
**伤害加成**：2倍伤害

**技能效果**：
- 每次攻击有40%概率触发暴击
- 造成2倍伤害
- 战斗日志显示："[CRITICAL STRIKE!] Soldier lands a devastating blow!"

**实现代码**：
```java
public class SoldierCriticalStrike implements SpecialAbility {
    private static final float CRIT_RATE = 0.4f;
    private static final float CRIT_MULTIPLIER = 2.0f;
    
    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        // 主动型技能，每次攻击都检查
        return Math.random() < CRIT_RATE;
    }
    
    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        Soldier soldier = (Soldier) crew;
        soldier.setCriticalActive(true);
        soldier.setDamageMultiplier(CRIT_MULTIPLIER);
    }
    
    @Override
    public String getAbilityName() {
        return "Critical Strike";
    }
    
    @Override
    public String getAbilityDescription() {
        return "40% chance per attack to deal 2x damage";
    }
}
```

**战斗场景示例**：
```
Round 2:
Soldier(Rex) acts against Asteroid Storm
  Base damage: 9 - 2 = 7
  [CRITICAL STRIKE!] Soldier lands a devastating blow!
  Multiplier: 2x
  Final damage: 7 × 2 = 14
  Asteroid Storm energy: 11/25
```

---

### 2.6 Robot（机器人）- 【自我修复】✨ 新角色

**触发机制**：被动自动触发  
**触发时机**：每3回合一次  
**修复效果**：恢复5点能量  
**冷却时间**：每3回合重置

**技能效果**：
- 每经过3个回合，Robot自动修复5点能量
- 无需消耗资源
- 不受其他效果影响（如中毒、禁用等）
- 持久作战型角色
- 战斗日志显示："[REPAIR CYCLE] Robot restores 5 energy!"

**实现代码**：
```java
public class RobotSelfRepair implements SpecialAbility {
    private static final int REPAIR_INTERVAL = 3;  // 每3回合
    private static final int REPAIR_AMOUNT = 5;
    
    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        Robot robot = (Robot) crew;
        // 每3回合触发一次
        return robot.getRoundCount() % REPAIR_INTERVAL == 0;
    }
    
    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        Robot robot = (Robot) crew;
        int newEnergy = Math.min(robot.getMaxEnergy(), 
                                robot.getEnergy() + REPAIR_AMOUNT);
        robot.setEnergy(newEnergy);
    }
    
    @Override
    public String getAbilityName() {
        return "Self Repair";
    }
    
    @Override
    public String getAbilityDescription() {
        return "Automatically repairs 5 energy every 3 turns. Passive ability.";
    }
}
```

**战斗场景示例**：
```
Round 3:
Robot(Unit-7) repair cycle triggers (3 mod 3 == 0)
  [REPAIR CYCLE] Robot restores 5 energy!
  Robot(Unit-7) energy: 17/22 (+ 5 from repair)

Round 6:
Robot(Unit-7) repair cycle triggers again
  [REPAIR CYCLE] Robot restores 5 energy!
  Robot(Unit-7) energy: 22/22 (at max)
```

---

## 3. 战斗中的技能集成

### 3.1 战斗流程集成

```
while (threat.alive && (crewA.alive || crewB.alive)):
    Round X:
    
    // 检查并应用被动技能（Pilot闪避、Robot修复）
    applyPassiveAbilities(crewA, crewB, threat)
    
    // 宇航员A的回合
    if crewA.alive:
        // 检查主动技能（Engineer盾、Medic治疗）
        if playerCanUseAbility(crewA):
            executeAbility(crewA)
        else:
            // 执行普通攻击
            damage = crewA.act() - threat.defend()
            
            // 检查Soldier暴击、Scientist分析
            damage = applyActiveAbilities(crewA, damage, threat)
            
            threat.takeDamage(damage)
```

### 3.2 技能优先级

1. **被动防御技能** (最先检查)
   - Pilot闪避 (reducingDamage)
   - Engineer护盾 (reducingDamage)

2. **伤害加成技能** (最后应用)
   - Soldier暴击 (multiplying)
   - Scientist分析 (multiplying)

3. **被动生存技能** (回合结束后)
   - Robot修复 (regeneration)
   - Medic治疗 (主动使用)

---

## 4. OOP设计优势

### 4.1 多态性体现
```java
// 在战斗逻辑中统一处理所有技能
List<CrewMember> crew = new ArrayList<>();
crew.add(new Pilot(...));
crew.add(new Engineer(...));
crew.add(new Robot(...));

// 无论是什么角色，都用同一套接口处理
for (CrewMember member : crew) {
    SpecialAbility ability = member.getSpecialAbility();
    if (ability.canUse(member, threat, ally)) {
        ability.executeAbility(member, threat, ally);
    }
}
```

### 4.2 易于扩展
添加新角色只需：
1. 创建新的CrewMember子类
2. 实现对应的SpecialAbility子类
3. 将其注册到工厂类

无需修改战斗逻辑！

### 4.3 易于测试
每个技能独立实现，可单独测试：
```java
@Test
public void testPilotEvasion() {
    Pilot pilot = new Pilot("TestPilot");
    SpecialAbility ability = pilot.getSpecialAbility();
    
    for (int i = 0; i < 100; i++) {
        if (ability.canUse(pilot, threat, ally)) {
            evadedCount++;
        }
    }
    
    // 期望大约20次触发
    assertEquals(20, evadedCount, 5);  // ±5容差
}
```

---

## 5. 战斗示例对比

### 情景1：Pilot+Soldier vs 陨石风暴

```
=== MISSION: Asteroid Storm ===
Threat: Asteroid Storm (skill: 6, resilience: 2, energy: 25/25)
Crew Member A: Pilot(Nova) skill: 5; energy: 20/20
Crew Member B: Soldier(Rex) skill: 9; energy: 16/16

--- Round 1 ---
Pilot(Nova) acts
  Base damage: 5 - 2 = 3
  Asteroid Storm energy: 22/25
Asteroid Storm retaliates against Pilot(Nova)
  Incoming damage: 6 - 4 = 2
  [EVASION!] Pilot dodges! (20% triggered)
  Pilot(Nova) energy: 20/20 (unchanged)

Soldier(Rex) acts
  Base damage: 9 - 2 = 7
  [CRITICAL STRIKE!] Soldier lands a devastating blow! (40% triggered)
  Final damage: 7 × 2 = 14
  Asteroid Storm energy: 8/25
  
Asteroid Storm retaliates against Soldier(Rex)
  Damage: 6 - 0 = 6
  Soldier(Rex) energy: 10/16

--- Round 2 ---
Pilot(Nova) acts
  Base damage: 5 - 2 = 3
  Asteroid Storm energy: 5/25
  
Asteroid Storm retaliates - LOW ENERGY PANIC MODE
  Damage: 6 - 4 = 2
  Pilot(Nova) energy: 18/20
  
Soldier(Rex) acts FINAL BLOW
  Base damage: 9 - 2 = 7
  Asteroid Storm energy: 0/25 (defeated!)

=== MISSION COMPLETE ===
The Asteroid Storm has been neutralized!
Pilot(Nova) gains 2 experience points.
Soldier(Rex) gains 2 experience points.
Battles won this session: 1
```

### 情景2：Engineer+Medic vs 火灾

```
=== MISSION: Kitchen Fire ===
Threat: Kitchen Fire (skill: 4, resilience: 1, energy: 18/18)
Crew Member A: Engineer(Maya) skill: 6; energy: 19/19
Crew Member B: Medic(Dr.Lee) skill: 7; energy: 18/18

--- Round 1 ---
Engineer(Maya) casts Shield
  [SHIELD!] Engineer deploys protection!
  Engineer(Maya) energy: 14/19
  
Kitchen Fire attacks Engineer(Maya)
  Raw damage: 4 - 3 = 1
  Shield reduces by 20: 1 - 20 = -19
  Engineer(Maya) takes 0 damage! (Shield absorbed)
  Engineer(Maya) energy: 14/19

Medic(Dr.Lee) acts
  Base damage: 7 - 1 = 6
  Kitchen Fire energy: 12/18
  
Kitchen Fire retaliates
  Damage: 4 - 2 = 2
  Medic(Dr.Lee) energy: 16/18

--- Round 2 ---
Shield expires (duration: 1 turn)
Engineer(Maya) acts
  Base damage: 6 - 1 = 5
  Kitchen Fire energy: 7/18

Kitchen Fire attacks Engineer(Maya)
  Damage: 4 - 3 = 1
  Engineer(Maya) energy: 13/19

Medic(Dr.Lee) casts Healing on Engineer
  [HEALING!] Medic restores 12 energy to Engineer!
  Medic(Dr.Lee) energy: 8/18
  Engineer(Maya) energy: 19/19 (fully healed!)

--- Round 3 ---
Engineer(Maya) acts
  Base damage: 6 - 1 = 5
  Kitchen Fire energy: 2/18
  
Kitchen Fire retaliates (WEAKENED)
  Damage: 4 - 3 = 1
  Engineer(Maya) energy: 18/19
  
Medic(Dr.Lee) acts FINAL BLOW
  Base damage: 7 - 1 = 6
  Kitchen Fire energy: 0/18 (defeated!)

=== MISSION COMPLETE ===
The Kitchen Fire has been extinguished!
Engineer(Maya) gains 2 experience points.
Medic(Dr.Lee) gains 2 experience points.
```

### 情景3：Scientist+Robot vs 辐射风暴

```
=== MISSION: Radiation Storm ===
Threat: Radiation Storm (skill: 7, resilience: 3, energy: 28/28)
  [克制类型：Scientist bonus +30%伤害]
Crew Member A: Scientist(Prof.Chen) skill: 8; energy: 17/17
Crew Member B: Robot(Unit-7) skill: 7; energy: 22/22

--- Round 1 ---
Scientist(Prof.Chen) acts
  Base damage: 8 - 3 = 5
  [ANALYSIS!] Scientist identifies weakness! (35% triggered)
  Final damage: 5 × 1.3 = 6.5 ≈ 7
  Radiation Storm energy: 21/28
  
Radiation Storm retaliates
  Damage: 7 - 1 = 6
  Scientist(Prof.Chen) energy: 11/17

Robot(Unit-7) acts
  Base damage: 7 - 3 = 4
  Radiation Storm energy: 17/28
  
Radiation Storm retaliates
  Damage: 7 - 2 = 5
  Robot(Unit-7) energy: 17/22

--- Round 2 ---
(Similar to Round 1)

--- Round 3 ---
Scientist(Prof.Chen) acts
  Base damage: 8 - 3 = 5
  [ANALYSIS!] Scientist identifies weakness! (35% triggered)
  Final damage: 7
  Radiation Storm energy: 4/28

Robot(Unit-7) auto-repair triggers (round 3 % 3 == 0)
  [REPAIR CYCLE] Robot restores 5 energy!
  Robot(Unit-7) energy: 22/22 (at max)

Radiation Storm retaliates WEAKLY
  Damage: 7 - 1 = 6
  Scientist(Prof.Chen) energy: 5/17

Robot(Unit-7) acts FINAL BLOW
  Base damage: 7 - 3 = 4
  Radiation Storm energy: 0/28 (defeated!)

=== MISSION COMPLETE ===
The Radiation Storm has been neutralized!
Scientist(Prof.Chen) gains 2 experience points.
Robot(Unit-7) gains 2 experience points.
Long-term survival strategy SUCCESS!
```

---

## 6. 实现建议

### Phase 1: 核心框架
- [ ] 实现SpecialAbility接口
- [ ] 基础CrewMember抽象类
- [ ] 6个技能子类框架

### Phase 2: 单个技能
- [ ] PilotEvasion (简单)
- [ ] EngineerShield (中等)
- [ ] MedicHealing (中等)
- [ ] ScientistAnalysis (中等)
- [ ] SoldierCriticalStrike (中等)
- [ ] RobotSelfRepair (简单 - 时间计数)

### Phase 3: 战斗集成
- [ ] 被动技能检查
- [ ] 主动技能选择UI
- [ ] 伤害加成应用
- [ ] 战斗日志显示

### Phase 4: 测试和平衡
- [ ] 概率测试
- [ ] 伤害平衡
- [ ] 游戏体验调整

---

## 7. 创新价值总结

✨ **OOP多态设计**：6种技能，6个独立的类实现  
✨ **策略多样性**：相同敌人遇上不同队伍产生完全不同的战斗  
✨ **易于扩展**：添加新角色无需修改现有代码  
✨ **视觉反馈**：每个技能都有独特的战斗日志提示  
✨ **平衡设计**：没有绝对强的技能，各有优劣  
✨ **Robot创意**：第6个角色引入"续航"新维度

这个系统确实体现了一个**成熟的游戏设计**！
