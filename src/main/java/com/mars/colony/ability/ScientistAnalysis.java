package com.mars.colony.ability;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;
import com.mars.colony.model.Scientist;

/**
 * Scientist技能 - 【智能分析】
 * 自动触发,针对特定威胁类型造成30%更多伤害
 */
public class ScientistAnalysis implements SpecialAbility {
    private static final float BASE_MULTIPLIER = 1.3f;
    private static final float UPGRADE_MULTIPLIER = 1.2f;
    private static final float BASE_TRIGGER_RATE = 0.35f;
    
    // 科学家克制的威胁类型
    private static final String[] COUNTER_THREATS = {
        "ToxinLeak", "RadiationStorm", "ChemicalFire", 
        "BiologicalHazard", "LabAccident", "Radiation"
    };
    
    private int level = 1;
    private float currentMultiplier = BASE_MULTIPLIER;

    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        if (!(crew instanceof Scientist)) return false;
        if (threat == null) return false;
        
        return isThreatType(threat) && Math.random() < getEffectiveRate();
    }

    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        if (!(crew instanceof Scientist)) return;
        // 伤害加成在战斗逻辑中应用,这里只是标记
        ((Scientist)crew).setAnalysisActive(true);
    }

    @Override
    public String getAbilityName() {
        return "Analysis";
    }

    @Override
    public String getAbilityDescription() {
        return String.format("%.0f%% chance for %.1fx damage on specific threats", 
                getEffectiveRate() * 100, getEffectiveDamageMultiplier());
    }

    @Override
    public int getCurrentLevel() { return level; }

    @Override
    public void setLevel(int level) { this.level = Math.max(1, Math.min(level, 5)); }

    /**
     * 检查是否是克制的威胁类型
     */
    private boolean isThreatType(Threat threat) {
        String threatName = threat.getType();
        for (String counter : COUNTER_THREATS) {
            if (threatName.contains(counter)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得有效触发率(考虑升级)
     */
    public float getEffectiveRate() {
        float rate = BASE_TRIGGER_RATE;
        for (int i = 1; i < level; i++) {
            rate *= UPGRADE_MULTIPLIER;
        }
        return Math.min(rate, 0.99f);
    }

    /**
     * 获得有效伤害倍数(考虑升级)
     */
    public float getEffectiveDamageMultiplier() {
        float mult = BASE_MULTIPLIER;
        for (int i = 1; i < level; i++) {
            mult *= UPGRADE_MULTIPLIER;
        }
        return mult;
    }
}
