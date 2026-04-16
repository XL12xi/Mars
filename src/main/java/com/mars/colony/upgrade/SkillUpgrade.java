package com.mars.colony.upgrade;

import java.io.Serializable;

/**
 * SkillUpgrade - 技能升级基类
 * 所有技能升级都继承此类
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

    /**
     * 计算升级到下一级所需的晶体
     * 公式: baseCount × currentLevel
     */
    public int getCostForNextLevel() {
        if (currentLevel >= MAX_LEVEL) {
            return -1;  // 已满级
        }
        int baseCost = 5;
        return baseCost * currentLevel;
    }

    /**
     * 升级
     */
    public boolean upgrade() {
        if (currentLevel < MAX_LEVEL) {
            int cost = getCostForNextLevel();
            currentLevel++;
            totalCrystalsSpent += cost;
            return true;
        }
        return false;
    }

    /**
     * 获取升级信息
     */
    public String getUpgradeInfo() {
        if (currentLevel >= MAX_LEVEL) {
            return String.format("%s Lv.%d ⭐ (Max Level)", abilityName, currentLevel);
        }
        return String.format("%s Lv.%d → Cost: %d crystals", 
                abilityName, currentLevel, getCostForNextLevel());
    }

    // ===== Get/Set =====
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
