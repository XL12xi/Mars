package com.mars.colony.ability;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;
import com.mars.colony.model.Medic;

/**
 * Medic技能 - 【急救】
 * 主动技能,消耗8能量恢复队友12能量
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
        
        // 优先治疗队友,如果队友能量满就治疗自己
        CrewMember target = (ally != null && ally.getEnergy() < ally.getMaxEnergy()) 
                            ? ally : crew;
        
        int healAmount = getEffectiveHealAmount();
        int newEnergy = Math.min(target.getMaxEnergy(), 
                                target.getEnergy() + healAmount);
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

    /**
     * 获得有效的治疗量(考虑升级)
     */
    public int getEffectiveHealAmount() {
        return BASE_HEAL + (HEAL_INCREMENT * (level - 1));
    }
}
