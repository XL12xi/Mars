package com.mars.colony.ability;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Pilot;
import com.mars.colony.model.Threat;

/**
 * Pilot ability: passive chance to avoid incoming damage completely.
 */
public class PilotEvasion implements SpecialAbility {
    private static final float BASE_RATE = 0.2f;
    private static final float UPGRADE_MULTIPLIER = 1.2f;
    private int level = 1;

    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        return Math.random() < getEffectiveRate();
    }

    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        if (crew instanceof Pilot) {
            ((Pilot) crew).setEvaded(true);
        }
    }

    @Override
    public String getAbilityName() {
        return "Evasion";
    }

    @Override
    public String getAbilityDescription() {
        return String.format(
                "%.1f%% chance to completely evade incoming damage",
                getEffectiveRate() * 100
        );
    }

    @Override
    public int getCurrentLevel() { return level; }

    @Override
    public void setLevel(int level) { this.level = Math.max(1, Math.min(level, 5)); }

    public float getEffectiveRate() {
        float rate = BASE_RATE;
        for (int i = 1; i < level; i++) {
            rate *= UPGRADE_MULTIPLIER;
        }
        return Math.min(rate, 0.99f);
    }
}
