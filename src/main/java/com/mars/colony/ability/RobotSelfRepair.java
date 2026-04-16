package com.mars.colony.ability;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;
import com.mars.colony.model.Robot;

/**
 * Robot技能 - 【自我修复】
 * 被动技能,每3回合自动恢复5能量(5级时变为2回合+16能量)
 */
public class RobotSelfRepair implements SpecialAbility {
    private static final int[] REPAIR_AMOUNTS = {5, 7, 10, 13, 16};
    private static final int[] REPAIR_INTERVALS = {3, 3, 3, 3, 2};
    private int level = 1;

    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        if (!(crew instanceof Robot)) return false;
        Robot robot = (Robot)crew;
        
        // 检查是否到了修复时间
        return robot.getRoundCount() % getEffectiveRepairInterval() == 0;
    }

    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        if (!(crew instanceof Robot)) return;
        
        int repairAmount = getEffectiveRepairAmount();
        int newEnergy = Math.min(crew.getMaxEnergy(), 
                                crew.getEnergy() + repairAmount);
        crew.setEnergy(newEnergy);
    }

    @Override
    public String getAbilityName() {
        return "Self Repair";
    }

    @Override
    public String getAbilityDescription() {
        return String.format("Auto-repair %d energy every %d turns", 
                getEffectiveRepairAmount(), getEffectiveRepairInterval());
    }

    @Override
    public int getCurrentLevel() { return level; }

    @Override
    public void setLevel(int level) { this.level = Math.max(1, Math.min(level, 5)); }

    /**
     * 获得有效修复量(考虑升级)
     */
    public int getEffectiveRepairAmount() {
        return REPAIR_AMOUNTS[level - 1];
    }

    /**
     * 获得有效修复间隔(考虑升级)
     */
    public int getEffectiveRepairInterval() {
        return REPAIR_INTERVALS[level - 1];
    }
}
