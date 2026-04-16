package com.mars.colony.ability;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;
import com.mars.colony.model.Engineer;

/**
 * Engineer技能 - 【护盾】
 * 主动技能,消耗5能量部署护盾,减少20伤害,持续1回合
 */
public class EngineerShield implements SpecialAbility {
    private static final int SHIELD_COST = 5;
    private static final int BASE_SHIELD = 20;
    private static final int SHIELD_DURATION = 1;
    private static final int SHIELD_INCREMENT = 10;
    private int level = 1;

    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        if (!(crew instanceof Engineer)) return false;
        Engineer engineer = (Engineer)crew;
        return crew.getEnergy() >= SHIELD_COST && !engineer.isShieldActive();
    }

    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        if (!(crew instanceof Engineer)) return;
        Engineer engineer = (Engineer)crew;
        
        crew.setEnergy(crew.getEnergy() - SHIELD_COST);
        engineer.setShieldActive(true);
        engineer.setShieldDuration(SHIELD_DURATION);
        engineer.setShieldValue(getEffectiveShieldValue());
    }

    @Override
    public String getAbilityName() {
        return "Shield";
    }

    @Override
    public String getAbilityDescription() {
        return String.format("Deploy shield for 1 turn (+%d damage reduction)", 
                getEffectiveShieldValue());
    }

    @Override
    public int getCurrentLevel() { return level; }

    @Override
    public void setLevel(int level) { this.level = Math.max(1, Math.min(level, 5)); }

    /**
     * 获得有效的护盾值(考虑升级)
     */
    public int getEffectiveShieldValue() {
        return BASE_SHIELD + (SHIELD_INCREMENT * (level - 1));
    }
}
