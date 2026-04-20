package com.mars.colony.upgrade;

import java.io.Serializable;

/**
 * Base class for skill upgrades that share a level and crystal cost model.
 */
public abstract class SkillUpgrade implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String abilityName;
    protected int currentLevel = 1;
    protected static final int MAX_LEVEL = 5;
    protected int totalCrystalsSpent = 0;

    public SkillUpgrade(String abilityName) {
        this.abilityName = abilityName;
    }

    public int getCostForNextLevel() {
        if (currentLevel >= MAX_LEVEL) {
            return -1;
        }
        int baseCost = 5;
        return baseCost * currentLevel;
    }

    public boolean upgrade() {
        if (currentLevel < MAX_LEVEL) {
            int cost = getCostForNextLevel();
            currentLevel++;
            totalCrystalsSpent += cost;
            return true;
        }
        return false;
    }

    public String getUpgradeInfo() {
        if (currentLevel >= MAX_LEVEL) {
            return String.format("%s Lv.%d (Max Level)", abilityName, currentLevel);
        }
        return String.format(
                "%s Lv.%d -> Cost: %d crystals",
                abilityName,
                currentLevel,
                getCostForNextLevel()
        );
    }

    public String getAbilityName() { return abilityName; }
    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int level) {
        currentLevel = Math.max(1, Math.min(level, MAX_LEVEL));
    }
    public int getTotalCrystalsSpent() { return totalCrystalsSpent; }
    public void setTotalCrystalsSpent(int spent) { totalCrystalsSpent = spent; }

    @Override
    public String toString() {
        return getUpgradeInfo();
    }
}
