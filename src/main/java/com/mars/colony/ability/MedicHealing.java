package com.mars.colony.ability;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Medic;
import com.mars.colony.model.Threat;

/**
 * Medic ability: spends energy to heal an ally, or self-heals if the ally is full.
 */
public class MedicHealing implements SpecialAbility {
    private static final int HEAL_COST = 8;
    private static final int BASE_HEAL = 12;
    private static final int HEAL_INCREMENT = 2;
    private int level = 1;

    @Override
    public boolean canUse(CrewMember crew, Threat threat, CrewMember ally) {
        if (!(crew instanceof Medic)) return false;
        return crew.getEnergy() >= HEAL_COST &&
                (crew.getEnergy() < crew.getMaxEnergy() ||
                        (ally != null && ally.getEnergy() < ally.getMaxEnergy()));
    }

    @Override
    public void executeAbility(CrewMember crew, Threat threat, CrewMember ally) {
        if (!(crew instanceof Medic)) return;

        crew.setEnergy(crew.getEnergy() - HEAL_COST);

        CrewMember target = (ally != null && ally.getEnergy() < ally.getMaxEnergy())
                ? ally
                : crew;

        int healAmount = getEffectiveHealAmount();
        int newEnergy = Math.min(target.getMaxEnergy(), target.getEnergy() + healAmount);
        target.setEnergy(newEnergy);
    }

    @Override
    public String getAbilityName() {
        return "Healing";
    }

    @Override
    public String getAbilityDescription() {
        return String.format("Restore %d energy to an ally", getEffectiveHealAmount());
    }

    @Override
    public int getCurrentLevel() { return level; }

    @Override
    public void setLevel(int level) { this.level = Math.max(1, Math.min(level, 5)); }

    public int getEffectiveHealAmount() {
        return BASE_HEAL + (HEAL_INCREMENT * (level - 1));
    }
}
