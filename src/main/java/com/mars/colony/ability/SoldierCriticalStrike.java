package com.mars.colony.ability;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;
import com.mars.colony.model.Soldier;

/**
 * Soldier技能 - 【绝对打击】
 * 自动触发,40%概率造成2倍伤害
 */
public class SoldierCriticalStrike implements SpecialAbility {
    private static final float BASE_CRIT_RATE = 0.4f;
    private static final float BASE_DAMAGE_MULT = 2.0f;
    private static final float UPGRADE_RATE_MULT = 1.2f;
    private static final float UPGRADE_DMG_INCREMENT = 0.2f;
    private int level = 1;

    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        if (!(crew instanceof Soldier)) return false;
        return Math.random() < getEffectiveRate();
    }

    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        if (!(crew instanceof Soldier)) return;
        ((Soldier)crew).setCriticalActive(true);
    }

    @Override
    public String getAbilityName() {
        return "Critical Strike";
    }

    @Override
    public String getAbilityDescription() {
        return String.format("%.0f%% chance for %.1fx damage", 
                getEffectiveRate() * 100, getEffectiveDamageMultiplier());
    }

    @Override
    public int getCurrentLevel() { return level; }

    @Override
    public void setLevel(int level) { this.level = Math.max(1, Math.min(level, 5)); }

    /**
     * 获得有效暴击率(考虑升级)
     */
    public float getEffectiveRate() {
        float rate = BASE_CRIT_RATE;
        for (int i = 1; i < level; i++) {
            rate *= UPGRADE_RATE_MULT;
        }
        return Math.min(rate, 0.99f);
    }

    /**
     * 获得有效伤害倍数(考虑升级)
     */
    public float getEffectiveDamageMultiplier() {
        return BASE_DAMAGE_MULT + (UPGRADE_DMG_INCREMENT * (level - 1));
    }
}
