package com.mars.colony.ability;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Scientist;
import com.mars.colony.model.Threat;

/**
 * Scientist ability: has a chance to boost damage against selected threat types.
 */
public class ScientistAnalysis implements SpecialAbility {
    private static final float BASE_MULTIPLIER = 1.3f;
    private static final float UPGRADE_MULTIPLIER = 1.2f;
    private static final float BASE_TRIGGER_RATE = 0.35f;

    private static final String[] COUNTER_THREATS = {
            "ToxinLeak", "RadiationStorm", "ChemicalFire",
            "BiologicalHazard", "LabAccident", "Radiation"
    };

    private int level = 1;

    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        if (!(crew instanceof Scientist)) return false;
        if (threat == null) return false;
        return isThreatType(threat) && Math.random() < getEffectiveRate();
    }

    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        if (crew instanceof Scientist) {
            ((Scientist) crew).setAnalysisActive(true);
        }
    }

    @Override
    public String getAbilityName() {
        return "Analysis";
    }

    @Override
    public String getAbilityDescription() {
        return String.format(
                "%.0f%% chance for %.1fx damage on specific threats",
                getEffectiveRate() * 100,
                getEffectiveDamageMultiplier()
        );
    }

    @Override
    public int getCurrentLevel() { return level; }

    @Override
    public void setLevel(int level) { this.level = Math.max(1, Math.min(level, 5)); }

    private boolean isThreatType(Threat threat) {
        String threatName = threat.getType();
        for (String counter : COUNTER_THREATS) {
            if (threatName.contains(counter)) {
                return true;
            }
        }
        return false;
    }

    public float getEffectiveRate() {
        float rate = BASE_TRIGGER_RATE;
        for (int i = 1; i < level; i++) {
            rate *= UPGRADE_MULTIPLIER;
        }
        return Math.min(rate, 0.99f);
    }

    public float getEffectiveDamageMultiplier() {
        float multiplier = BASE_MULTIPLIER;
        for (int i = 1; i < level; i++) {
            multiplier *= UPGRADE_MULTIPLIER;
        }
        return multiplier;
    }
}
