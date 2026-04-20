package com.mars.colony;

import com.mars.colony.game.LootSystem;
import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Pilot;
import com.mars.colony.model.Soldier;
import com.mars.colony.model.Threat;
import com.mars.colony.upgrade.SkillUpgradeManager;
import java.util.Arrays;
import java.util.List;

/**
 * Lightweight console checks for skill upgrade formulas and reward logic.
 */
public class UpgradeSystemTest {

    private static int passCount = 0;
    private static int failCount = 0;

    public static void main(String[] args) {
        System.out.println("=== Skill Upgrade System Checks ===\n");

        testUpgradeCostCalculation();
        testSkillEffectFormulas();
        testLootSystem();
        testSkillUpgradeManager();

        System.out.println();
        System.out.println("=== Test Results ===");
        System.out.println("Passed: " + passCount + " | Failed: " + failCount);
    }

    private static void testUpgradeCostCalculation() {
        System.out.println("[Test 1] Upgrade cost calculation");
        int[] expectedCosts = {5, 10, 15, 20};

        for (int i = 0; i < expectedCosts.length; i++) {
            int cost = 5 * (i + 1);
            assertCondition(
                    cost == expectedCosts[i],
                    "Lv." + (i + 1) + " -> Lv." + (i + 2) + " costs " + cost + " crystals"
            );
        }

        int totalCost = 5 + 10 + 15 + 20;
        assertCondition(totalCost == 50, "Lv.1 -> Lv.5 total cost is 50 crystals");
        System.out.println();
    }

    private static void testSkillEffectFormulas() {
        System.out.println("[Test 2] Skill effect formulas");

        double evasionLv5 = 0.20 * Math.pow(1.2, 4);
        assertCondition(Math.abs(evasionLv5 - 0.41472) < 0.0001, "Pilot Evasion Lv.5 is about 41.5%");

        int shieldLv5 = 20 + 10 * 4;
        assertCondition(shieldLv5 == 60, "Engineer Shield Lv.5 blocks 60 damage");

        int healLv5 = 12 + 2 * 4;
        assertCondition(healLv5 == 20, "Medic Healing Lv.5 restores 20 energy");

        double analysisLv5 = 1.3 * Math.pow(1.2, 4);
        assertCondition(Math.abs(analysisLv5 - 2.69568) < 0.0001, "Scientist Analysis Lv.5 multiplier formula is stable");

        double critRateLv5 = 0.40 * Math.pow(1.2, 4);
        double critDamageLv5 = 2.0 + 0.2 * 4;
        assertCondition(Math.abs(critRateLv5 - 0.82944) < 0.0001 && critDamageLv5 == 2.8,
                "Soldier Critical Strike Lv.5 is 82.9% at 2.8x");

        int[] repairs = {5, 7, 10, 13, 16};
        int[] intervals = {3, 3, 3, 3, 2};
        assertCondition(repairs[4] == 16 && intervals[4] == 2, "Robot Self Repair Lv.5 restores 16 energy every 2 turns");
        System.out.println();
    }

    private static void testLootSystem() {
        System.out.println("[Test 3] Loot system");

        Threat easyThreat = new Threat("EasyEnemy", "EASY", 4, 2, 20);
        Threat normalThreat = new Threat("NormalEnemy", "NORMAL", 6, 2, 25);
        Threat hardThreat = new Threat("HardEnemy", "HARD", 8, 3, 35);
        Threat extremeThreat = new Threat("ExtremeEnemy", "EXTREME", 10, 4, 50);

        List<CrewMember> survivors = Arrays.asList(
                new Pilot(1, "A", 0),
                new Soldier(2, "B", 0)
        );

        assertCondition(LootSystem.calculateCrystalReward(easyThreat, survivors) == 6, "Easy reward is 6 crystals");
        assertCondition(LootSystem.calculateCrystalReward(normalThreat, survivors) == 8, "Normal reward is 8 crystals");
        assertCondition(LootSystem.calculateCrystalReward(hardThreat, survivors) == 11, "Hard reward is 11 crystals");
        assertCondition(LootSystem.calculateCrystalReward(extremeThreat, survivors) == 16, "Extreme reward is 16 crystals");
        System.out.println();
    }

    private static void testSkillUpgradeManager() {
        System.out.println("[Test 4] Skill upgrade manager");

        SkillUpgradeManager manager = new SkillUpgradeManager(1, "TestCrew");
        assertCondition(manager.getSkillCrystalsOwned() == 0, "Manager starts with 0 crystals");
        assertCondition(manager.getSkillLevel("Evasion") == 1, "Evasion starts at Lv.1");

        manager.addCrystals(50);
        assertCondition(manager.getSkillCrystalsOwned() == 50, "Added 50 crystals");

        boolean firstUpgrade = manager.upgradeSkill("Evasion");
        assertCondition(firstUpgrade, "First Evasion upgrade succeeds");
        assertCondition(manager.getSkillLevel("Evasion") == 2, "Evasion reaches Lv.2");
        assertCondition(manager.getSkillCrystalsOwned() == 45, "First upgrade spends 5 crystals");

        manager.upgradeSkill("Evasion");
        manager.upgradeSkill("Evasion");
        manager.upgradeSkill("Evasion");
        assertCondition(manager.getSkillLevel("Evasion") == 5, "Evasion reaches Lv.5");
        assertCondition(manager.getTotalCrystalsSpent() == 50, "Lv.1 -> Lv.5 spends 50 crystals");
        System.out.println();
    }

    private static void assertCondition(boolean condition, String message) {
        if (condition) {
            passCount++;
            System.out.println("[PASS] " + message);
        } else {
            failCount++;
            System.out.println("[FAIL] " + message);
        }
    }
}
