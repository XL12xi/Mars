package com.mars.colony.game;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;
import java.util.ArrayList;
import java.util.List;

/**
 * MissionControl - 任务控制类
 * 负责生成威胁、管理交互式战斗、分配奖励
 * 
 * 支持交互式回合制战斗 - 玩家选择宇航员和技能，类似宝可梦战斗
 */
public class MissionControl {
    private Colony colony;
    private int threatIdCounter = 1;
    private InteractiveBattle currentInteractiveBattle;

    public MissionControl(Colony colony) {
        this.colony = colony;
    }

    /**
     * 生成威胁
     */
    public Threat generateThreat() {
        int difficulty = (int)(colony.getMissionCount() / 2) + 1;
        float multiplier = colony.getDifficultyMultiplier();

        int threatSkill = (int)((4 + difficulty * 0.5f) * multiplier);
        int threatResilience = (int)((2 + difficulty * 0.3f) * multiplier);
        int threatMaxEnergy = (int)((20 + difficulty * 2) * multiplier);

        String[] threatNames = {
            "Asteroid Storm", "Radiation Burst", "Solar Flare",
            "Alien Attack", "System Malfunction", "Fire Outbreak"
        };
        String[] threatTypes = {
            "MeteorStorm", "RadiationStorm", "SolarFlare",
            "AlienAttack", "SystemError", "Fire"
        };

        int index = (int)(Math.random() * threatNames.length);
        return new Threat(threatNames[index], threatTypes[index],
                threatSkill, threatResilience, threatMaxEnergy);
    }

    // ============== 交互式战斗方法 ==============

    /**
     * 开始交互式战斗 - 用于UI游戏
     * 返回一个交互式战斗实例，UI可以通过它来管理战斗流程
     */
    public InteractiveBattle startInteractiveMission(CrewMember crewA, CrewMember crewB, Threat threat) {
        if (crewA == null || crewB == null || threat == null) {
            return null;
        }

        // 重置宇航员状态(用于新战斗)
        crewA.setAlive(true);
        crewA.setEnergy(crewA.getMaxEnergy());  // 恢复血量
        crewB.setAlive(true);
        crewB.setEnergy(crewB.getMaxEnergy());  // 恢复血量
        
        threat.setAlive(true);
        threat.setEnergy(threat.getMaxEnergy());  // 恢复威胁的能量

        // 创建交互式战斗实例
        this.currentInteractiveBattle = new InteractiveBattle(crewA, crewB, threat);
        return currentInteractiveBattle;
    }

    /**
     * 获取当前的交互式战斗实例
     */
    public InteractiveBattle getCurrentInteractiveBattle() {
        return currentInteractiveBattle;
    }

    /**
     * 完成交互式战斗 - 分配奖励、更新统计信息
     * 必须在战斗结束后调用
     */
    public boolean finalizeInteractiveMission(InteractiveBattle battle) {
        if (!battle.isBattleOver()) {
            return false;
        }

        CrewMember crewA = battle.getCrewA();
        CrewMember crewB = battle.getCrewB();
        Threat threat = battle.getThreat();

        List<CrewMember> survivors = new ArrayList<>();
        if (crewA.isAlive()) survivors.add(crewA);
        if (crewB.isAlive()) survivors.add(crewB);

        if (!battle.didCrewWin()) {
            // 任务失败
            System.out.println("[MissionControl] Battle lost, no rewards");
            return false;
        }

        // 任务成功 - 分配奖励
        int crystalReward = LootSystem.calculateCrystalReward(threat, survivors);
        int experienceReward = 2;  // 每个存活者获得2经验

        System.out.println(String.format("[MissionControl] Battle won! Survivors: %d, Crystals to storage: %d, Exp: %d",
                survivors.size(), crystalReward, experienceReward));

        // ✨ 水晶进入全球仓库，而不是直接给宇航员
        colony.addCrystalsToStorage(crystalReward);

        // 给存活者分配经验（但不分配水晶）
        for (CrewMember crew : survivors) {
            crew.setExperience(crew.getExperience() + experienceReward);
            crew.setVictoriesCount(crew.getVictoriesCount() + 1);
            crew.setMissionsCompleted(crew.getMissionsCompleted() + 1);
            
            System.out.println(String.format("[MissionControl] %s: +%d exp (total: %d)",
                    crew.getName(), experienceReward, crew.getExperience()));
        }

        // 更新任务计数
        colony.completeMission();

        return true;
    }
}

