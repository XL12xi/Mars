package com.mars.colony.ability;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Robot;
import com.mars.colony.model.Threat;

/**
 * Robot ability: automatically restores energy on a level-based interval.
 */
public class RobotSelfRepair implements SpecialAbility {
    private static final int[] REPAIR_AMOUNTS = {5, 7, 10, 13, 16};
    private static final int[] REPAIR_INTERVALS = {3, 3, 3, 3, 2};
    private int level = 1;

    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        if (!(crew instanceof Robot)) return false;
        Robot robot = (Robot) crew;
        return robot.getRoundCount() % getEffectiveRepairInterval() == 0;
    }

    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        if (!(crew instanceof Robot)) return;

        int repairAmount = getEffectiveRepairAmount();
        int newEnergy = Math.min(crew.getMaxEnergy(), crew.getEnergy() + repairAmount);
        crew.setEnergy(newEnergy);
    }

    @Override
    public String getAbilityName() {
        return "Self Repair";
    }

    @Override
    public String getAbilityDescription() {
        return String.format(
                "Auto-repair %d energy every %d turns",
                getEffectiveRepairAmount(),
                getEffectiveRepairInterval()
        );
    }

    @Override
    public int getCurrentLevel() { return level; }

    @Override
    public void setLevel(int level) { this.level = Math.max(1, Math.min(level, 5)); }

    public int getEffectiveRepairAmount() {
        return REPAIR_AMOUNTS[level - 1];
    }

    public int getEffectiveRepairInterval() {
        return REPAIR_INTERVALS[level - 1];
    }
}
