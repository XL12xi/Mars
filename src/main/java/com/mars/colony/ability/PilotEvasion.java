package com.mars.colony.ability;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;
import com.mars.colony.model.Pilot;

/**
 * Pilot技能 - 【闪避】
 * 被动触发,20%基础概率完全闪避伤害
 */
public class PilotEvasion implements SpecialAbility {
    private static final float BASE_RATE = 0.2f;
    private static final float UPGRADE_MULTIPLIER = 1.2f;
    private int level = 1;

    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        float effectiveRate = getEffectiveRate();
        return Math.random() < effectiveRate;
    }

    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        // 闪避在takeDamage中处理
        if (crew instanceof Pilot) {
            ((Pilot)crew).setEvaded(true);
        }
    }

    @Override
    public String getAbilityName() {
        return "Evasion";
    }

    @Override
    public String getAbilityDescription() {
        return String.format("%.1f%% chance to completely evade incoming damage", 
                getEffectiveRate() * 100);
    }

    @Override
    public int getCurrentLevel() { return level; }

    @Override
    public void setLevel(int level) { this.level = Math.max(1, Math.min(level, 5)); }

    /**
     * 获得有效的闪避概率(考虑升级)
     */
    public float getEffectiveRate() {
        float rate = BASE_RATE;
        for (int i = 1; i < level; i++) {
            rate *= UPGRADE_MULTIPLIER;
        }
        return Math.min(rate, 0.99f);  // 最多99%
    }
}
