package com.mars.colony.upgrade;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SkillUpgradeManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String[] SKILL_NAMES = {
        "Evasion",
        "Shield",
        "Healing",
        "Analysis",
        "CriticalStrike",
        "SelfRepair"
    };

    private final int crewId;
    private final String crewName;
    private int skillCrystalsOwned = 0;
    private int totalCrystalsSpent = 0;
    private final Map<String, Integer> skillLevels = new HashMap<>();

    public SkillUpgradeManager(int crewId, String crewName) {
        this.crewId = crewId;
        this.crewName = crewName;

        for (String skillName : SKILL_NAMES) {
            skillLevels.put(skillName, 1);
        }
    }

    public void addCrystals(int amount) {
        if (amount > 0) {
            this.skillCrystalsOwned += amount;
        }
    }

    public boolean spendCrystals(int amount) {
        if (amount > 0 && this.skillCrystalsOwned >= amount) {
            this.skillCrystalsOwned -= amount;
            this.totalCrystalsSpent += amount;
            return true;
        }
        return false;
    }

    public boolean upgradeSkill(String skillName) {
        if (!skillLevels.containsKey(skillName)) {
            return false;
        }

        int currentLevel = skillLevels.get(skillName);
        if (currentLevel >= 5) {
            return false;
        }

        int cost = 5 * currentLevel;
        if (!spendCrystals(cost)) {
            return false;
        }

        skillLevels.put(skillName, currentLevel + 1);
        return true;
    }

    public int getSkillLevel(String skillName) {
        return skillLevels.getOrDefault(skillName, 1);
    }

    public int getUpgradeCost(String skillName) {
        int currentLevel = getSkillLevel(skillName);
        if (currentLevel >= 5) {
            return -1;
        }
        return 5 * currentLevel;
    }

    public String getUpgradeReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== %s skill upgrades ===%n", crewName));
        sb.append(String.format("Crystals: %d | Total spent: %d%n", skillCrystalsOwned, totalCrystalsSpent));

        for (String skillName : SKILL_NAMES) {
            int level = getSkillLevel(skillName);
            int cost = getUpgradeCost(skillName);
            sb.append(String.format(
                    "%s -> Lv.%d | Next cost: %s | Effect: %s%n",
                    skillName,
                    level,
                    cost == -1 ? "MAX" : String.valueOf(cost),
                    getSkillEffectDisplay(skillName)
            ));
        }

        return sb.toString();
    }

    public String getSkillEffectDisplay(String skillName) {
        int level = getSkillLevel(skillName);

        switch (skillName) {
            case "Evasion":
                return String.format("Evade chance %.1f%%", 0.20 * Math.pow(1.2, level - 1) * 100);
            case "Shield":
                return String.format("Shield value %d", 20 + 10 * (level - 1));
            case "Healing":
                return String.format("Heal %d energy", 12 + 2 * (level - 1));
            case "Analysis":
                return String.format("Damage bonus %.2fx", 1.3 * Math.pow(1.2, level - 1));
            case "CriticalStrike":
                return String.format(
                        "Crit %.1f%% at %.1fx",
                        0.40 * Math.pow(1.2, level - 1) * 100,
                        2.0 + 0.2 * (level - 1)
                );
            case "SelfRepair":
                int[] repairAmounts = {5, 7, 10, 13, 16};
                int[] repairIntervals = {3, 3, 3, 3, 2};
                return String.format(
                        "Repair %d energy every %d turns",
                        repairAmounts[level - 1],
                        repairIntervals[level - 1]
                );
            default:
                return "Unknown skill";
        }
    }

    public int getCrewId() { return crewId; }
    public String getCrewName() { return crewName; }
    public int getSkillCrystalsOwned() { return skillCrystalsOwned; }
    public int getTotalCrystalsSpent() { return totalCrystalsSpent; }

    public void setSkillLevel(String skillName, int level) {
        if (skillLevels.containsKey(skillName)) {
            skillLevels.put(skillName, Math.max(1, Math.min(level, 5)));
        }
    }

    @Override
    public String toString() {
        return String.format("%s [Crystals: %d, Spent: %d]", crewName, skillCrystalsOwned, totalCrystalsSpent);
    }
}
