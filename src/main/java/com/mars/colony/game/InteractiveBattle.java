package com.mars.colony.game;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;
import java.util.ArrayList;
import java.util.List;

/**
 * InteractiveBattle - 交互式回合制战斗引擎
 * 类似宝可梦的战斗系统，允许玩家选择角色和技能
 * 
 * 战斗流程:
 * 1. 初始化战斗
 * 2. 玩家选择两个宇航员中的一个
 * 3. 玩家选择该宇航员的动作(普通攻击或特殊能力)
 * 4. 执行玩家动作
 * 5. 如果威胁还活着，威胁反击
 * 6. 回到步骤2，直到战斗结束
 */
public class InteractiveBattle {
    
    // 战斗阶段枚举
    public enum BattlePhase {
        INITIALIZATION,      // 战斗初始化
        PLAYER_CREW_SELECT,  // 玩家选择宇航员
        PLAYER_ACTION_SELECT,// 玩家选择动作
        PLAYER_TURN,         // 执行玩家动作
        THREAT_TURN,         // 威胁反击
        BATTLE_END          // 战斗结束
    }
    
    // 动作类型枚举
    public enum ActionType {
        NORMAL_ATTACK,  // 普通攻击
        SPECIAL_ABILITY // 特殊能力
    }
    
    private CrewMember crewA;
    private CrewMember crewB;
    private Threat threat;
    
    private BattlePhase currentPhase;
    private int currentRound;
    private StringBuilder battleLog;
    
    // 当前选择的信息
    private int selectedCrewIndex; // 0=crewA, 1=crewB, -1=未选择
    private ActionType selectedAction;
    private CrewMember lastActingCrew;
    
    // 战斗结果
    private boolean battleOver = false;
    private boolean threatDefeated = false;
    
    public InteractiveBattle(CrewMember crewA, CrewMember crewB, Threat threat) {
        this.crewA = crewA;
        this.crewB = crewB;
        this.threat = threat;
        
        this.currentRound = 0;
        this.battleLog = new StringBuilder();
        this.selectedCrewIndex = -1;
        this.currentPhase = BattlePhase.INITIALIZATION;
        
        initializeBattle();
    }
    
    /**
     * 初始化战斗
     */
    private void initializeBattle() {
        addLog("=== BATTLE START ===");
        addLog(String.format("Threat: %s (Skill: %d, Resilience: %d, Energy: %d)",
                threat.getName(), threat.getSkill(), threat.getResilience(), threat.getMaxEnergy()));
        addLog(String.format("Crew A: %s (Skill: %d, Resilience: %d, Energy: %d, Specialization: %s)",
                crewA.getName(), crewA.getSkill(), crewA.getResilience(), crewA.getMaxEnergy(), crewA.getSpecialization()));
        addLog(String.format("Crew B: %s (Skill: %d, Resilience: %d, Energy: %d, Specialization: %s)",
                crewB.getName(), crewB.getSkill(), crewB.getResilience(), crewB.getMaxEnergy(), crewB.getSpecialization()));
        addLog("");
        
        currentRound = 1;
        currentPhase = BattlePhase.PLAYER_CREW_SELECT;
    }
    
    /**
     * 获取当前战斗阶段
     */
    public BattlePhase getCurrentPhase() {
        return currentPhase;
    }
    
    /**
     * 玩家选择宇航员
     * @param crewIndex 0=crewA, 1=crewB
     * @return 是否选择成功
     */
    public boolean selectCrew(int crewIndex) {
        if (currentPhase != BattlePhase.PLAYER_CREW_SELECT) {
            return false;
        }
        
        // 先检查两个宇航员是否都已死亡
        if (!crewA.isAlive() && !crewB.isAlive()) {
            currentPhase = BattlePhase.BATTLE_END;
            battleOver = true;
            threatDefeated = false;
            addLog("=== BATTLE END ===");
            addLog("All crew members defeated!");
            return false;
        }
        
        CrewMember selectedCrew = (crewIndex == 0) ? crewA : crewB;
        
        // 检查选择的宇航员是否还活着
        if (!selectedCrew.isAlive()) {
            addLog(String.format("[ERROR] %s has been defeated and cannot act!", selectedCrew.getName()));
            return false;
        }
        
        this.selectedCrewIndex = crewIndex;
        this.lastActingCrew = selectedCrew;
        
        addLog(String.format("Round %d: %s selected to act", currentRound, selectedCrew.getName()));
        
        currentPhase = BattlePhase.PLAYER_ACTION_SELECT;
        return true;
    }
    
    /**
     * 玩家选择动作
     * @param actionType 动作类型(0=普通攻击, 1=特殊能力)
     * @return 是否选择成功
     */
    public boolean selectAction(ActionType actionType) {
        if (currentPhase != BattlePhase.PLAYER_ACTION_SELECT) {
            return false;
        }
        
        this.selectedAction = actionType;
        currentPhase = BattlePhase.PLAYER_TURN;
        return true;
    }
    
    /**
     * 执行玩家回合
     */
    public void executePlayerTurn() {
        if (currentPhase != BattlePhase.PLAYER_TURN) {
            return;
        }
        
        CrewMember actor = (selectedCrewIndex == 0) ? crewA : crewB;
        
        if (!actor.isAlive()) {
            addLog(String.format("[ERROR] %s cannot act - already defeated!", actor.getName()));
            currentPhase = BattlePhase.PLAYER_CREW_SELECT;
            return;
        }
        
        int damage = 0;
        
        if (selectedAction == ActionType.NORMAL_ATTACK) {
            // 普通攻击
            damage = actor.act() - threat.defend();
            damage = Math.max(1, damage);
            threat.takeDamage(damage);
            
            addLog(String.format("  %s uses NORMAL ATTACK: %d damage", actor.getName(), damage));
            addLog(String.format("  %s HP: %d / %d", threat.getName(), threat.getEnergy(), threat.getMaxEnergy()));
        } else if (selectedAction == ActionType.SPECIAL_ABILITY) {
            // 特殊能力
            if (actor.getSpecialAbility() != null) {
                String abilityName = actor.getSpecialAbility().getAbilityName();
                addLog(String.format("  %s uses SPECIAL ABILITY: %s (Level %d)",
                        actor.getName(), abilityName, actor.getSpecialAbility().getCurrentLevel()));
                
                // 执行特殊能力
                actor.getSpecialAbility().executeAbility(actor, threat, null);
                
                // 特殊能力可能会造成伤害、治疗、增加防御等
                // 这里简化处理：特殊能力执行后再普通攻击
                damage = actor.act() - threat.defend();
                damage = Math.max(1, damage);
                threat.takeDamage(damage);
                addLog(String.format("  Attack damage: %d", damage));
                addLog(String.format("  %s HP: %d / %d", threat.getName(), threat.getEnergy(), threat.getMaxEnergy()));
            }
        }
        
        // 检查威胁是否被打败
        if (!threat.isAlive()) {
            currentPhase = BattlePhase.BATTLE_END;
            battleOver = true;
            threatDefeated = true;
            addLog("=== BATTLE END ===");
            addLog(String.format("The %s has been defeated!", threat.getName()));
            return;
        }
        
        addLog("");
        
        // 威胁反击
        currentPhase = BattlePhase.THREAT_TURN;
    }
    
    /**
     * 执行威胁反击
     */
    public void executeThreatTurn() {
        if (currentPhase != BattlePhase.THREAT_TURN) {
            return;
        }
        
        CrewMember defender = lastActingCrew;
        
        if (!defender.isAlive()) {
            addLog(String.format("%s is already defeated, threat targets the other crew member instead", defender.getName()));
            defender = (selectedCrewIndex == 0) ? crewB : crewA;
        }
        
        if (!defender.isAlive()) {
            // 两个都死了
            currentPhase = BattlePhase.BATTLE_END;
            battleOver = true;
            threatDefeated = false;
            addLog("=== BATTLE END ===");
            addLog("All crew members defeated!");
            return;
        }
        
        int damage = threat.act() - defender.defend(threat.act());
        damage = Math.max(1, damage);
        defender.takeDamage(damage);
        
        addLog(String.format("%s retaliates: %d damage to %s", threat.getName(), damage, defender.getName()));
        addLog(String.format("  %s HP: %d / %d", defender.getName(), defender.getEnergy(), defender.getMaxEnergy()));
        
        if (!defender.isAlive()) {
            addLog(String.format("  [DEFEATED] %s has been defeated!", defender.getName()));
        }
        
        addLog("");
        
        // 回到下一个玩家回合
        currentRound++;
        currentPhase = BattlePhase.PLAYER_CREW_SELECT;
    }
    
    /**
     * 获取可用的动作(当前选中的宇航员可以执行的动作)
     */
    public List<String> getAvailableActions() {
        List<String> actions = new ArrayList<>();
        actions.add("Normal Attack");
        
        CrewMember selectedCrew = (selectedCrewIndex == 0) ? crewA : crewB;
        if (selectedCrew.getSpecialAbility() != null) {
            String abilityName = selectedCrew.getSpecialAbility().getAbilityName();
            int level = selectedCrew.getSpecialAbility().getCurrentLevel();
            actions.add("Special Ability: " + abilityName + " (Lv." + level + ")");
        }
        
        return actions;
    }
    
    /**
     * 获取可控制的宇航员列表
     */
    public List<String> getControllableCrew() {
        List<String> crew = new ArrayList<>();
        
        if (crewA.isAlive()) {
            crew.add(String.format("[CREW_A] %s (%s) - HP: %d/%d",
                    crewA.getName(), crewA.getSpecialization(),
                    crewA.getEnergy(), crewA.getMaxEnergy()));
        }
        
        if (crewB.isAlive()) {
            crew.add(String.format("[CREW_B] %s (%s) - HP: %d/%d",
                    crewB.getName(), crewB.getSpecialization(),
                    crewB.getEnergy(), crewB.getMaxEnergy()));
        }
        
        return crew;
    }
    
    /**
     * 获取可控制的宇航员对应的索引
     * @return 返回活着的宇航员索引列表 (0=crewA, 1=crewB)
     */
    public List<Integer> getControllableCrewIndices() {
        List<Integer> indices = new ArrayList<>();
        
        if (crewA.isAlive()) {
            indices.add(0);
        }
        
        if (crewB.isAlive()) {
            indices.add(1);
        }
        
        return indices;
    }
    
    /**
     * 获取战斗状态信息(给UI显示)
     */
    public BattleStatus getBattleStatus() {
        return new BattleStatus(
                currentPhase,
                currentRound,
                crewA.getName(), crewA.getEnergy(), crewA.getMaxEnergy(), crewA.isAlive(), crewA.getSpecialization(),
                crewB.getName(), crewB.getEnergy(), crewB.getMaxEnergy(), crewB.isAlive(), crewB.getSpecialization(),
                threat.getName(), threat.getEnergy(), threat.getMaxEnergy(), threat.isAlive(),
                selectedCrewIndex,
                battleOver
        );
    }
    
    /**
     * 检查战斗是否结束
     */
    public boolean isBattleOver() {
        return battleOver;
    }
    
    /**
     * 获取赢家
     * @return true=宇航员胜利, false=威胁胜利
     */
    public boolean didCrewWin() {
        return threatDefeated;
    }
    
    /**
     * 获取当前回合数
     */
    public int getCurrentRound() {
        return currentRound;
    }
    
    /**
     * 获取战斗日志
     */
    public String getBattleLog() {
        return battleLog.toString();
    }
    
    /**
     * 添加日志条目
     */
    private void addLog(String message) {
        battleLog.append(message).append("\n");
    }
    
    /**
     * 获取威胁信息
     */
    public Threat getThreat() {
        return threat;
    }
    
    /**
     * 获取宇航员信息
     */
    public CrewMember getCrewA() {
        return crewA;
    }
    
    public CrewMember getCrewB() {
        return crewB;
    }
    
    /**
     * 内部类：战斗状态信息
     */
    public static class BattleStatus {
        public BattlePhase phase;
        public int round;
        
        // 宇航员A
        public String crewAName;
        public int crewAEnergy, crewAMaxEnergy;
        public boolean crewAAlive;
        public String crewASpec;
        
        // 宇航员B
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
        
        public BattleStatus(
                BattlePhase phase, int round,
                String crewAName, int crewAEnergy, int crewAMaxEnergy, boolean crewAAlive, String crewASpec,
                String crewBName, int crewBEnergy, int crewBMaxEnergy, boolean crewBAlive, String crewBSpec,
                String threatName, int threatEnergy, int threatMaxEnergy, boolean threatAlive,
                int selectedCrewIndex,
                boolean battleOver
        ) {
            this.phase = phase;
            this.round = round;
            this.crewAName = crewAName;
            this.crewAEnergy = crewAEnergy;
            this.crewAMaxEnergy = crewAMaxEnergy;
            this.crewAAlive = crewAAlive;
            this.crewASpec = crewASpec;
            this.crewBName = crewBName;
            this.crewBEnergy = crewBEnergy;
            this.crewBMaxEnergy = crewBMaxEnergy;
            this.crewBAlive = crewBAlive;
            this.crewBSpec = crewBSpec;
            this.threatName = threatName;
            this.threatEnergy = threatEnergy;
            this.threatMaxEnergy = threatMaxEnergy;
            this.threatAlive = threatAlive;
            this.selectedCrewIndex = selectedCrewIndex;
            this.battleOver = battleOver;
        }
    }
}
