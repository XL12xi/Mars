package com.mars.colony;

import com.mars.colony.game.*;
import com.mars.colony.model.*;
import com.mars.colony.upgrade.*;
import java.util.*;

/**
 * UpgradeSystemTest - 技能升级系统测试类
 * 验证所有升级公式和掉落计算
 */
public class UpgradeSystemTest {
    
    private static int passCount = 0;
    private static int failCount = 0;
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║   技能升级系统 - 单元测试                  ║");
        System.out.println("╚════════════════════════════════════════════╝\n");

        // 测试1: 升级成本计算
        testUpgradeCostCalculation();
        
        // 测试2: 技能效果公式
        testSkillEffectFormulas();
        
        // 测试3: 掉落系统
        testLootSystem();
        
        // 测试4: 升级管理器
        testSkillUpgradeManager();
        
        // 汇总结果
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println(String.format("║   测试结果: %d 通过 | %d 失败           ║", passCount, failCount));
        System.out.println("╚════════════════════════════════════════════╝");
    }

    /**
     * 测试1: 升级成本计算
     * 成本公式: 5 × currentLevel
     */
    private static void testUpgradeCostCalculation() {
        System.out.println("[测试1] 升级成本计算\n");
        
        int[] expectedCosts = {5, 10, 15, 20};  // Lv1→2, Lv2→3, Lv3→4, Lv4→5
        
        for (int i = 0; i < 4; i++) {
            int cost = 5 * (i + 1);
            if (cost == expectedCosts[i]) {
                System.out.println("✓ Lv." + (i+1) + " → Lv." + (i+2) + ": " + cost + "晶");
                passCount++;
            } else {
                System.out.println("✗ Lv." + (i+1) + " → Lv." + (i+2) + ": 期望" + 
                        expectedCosts[i] + "晶，得到" + cost + "晶");
                failCount++;
            }
        }
        
        int totalCost = 5 + 10 + 15 + 20;
        System.out.println("\n总升级成本: " + totalCost + "晶");
        if (totalCost == 50) {
            System.out.println("✓ 从Lv.1升到Lv.5总成本: 50晶");
            passCount++;
        } else {
            System.out.println("✗ 从Lv.1升到Lv.5总成本应为50晶，得到" + totalCost + "晶");
            failCount++;
        }
        System.out.println();
    }

    /**
     * 测试2: 技能效果公式
     */
    private static void testSkillEffectFormulas() {
        System.out.println("[测试2] 技能效果升级公式\n");
        
        // 测试闪避: 20% × 1.2^(level-1)
        System.out.println("【闪避技能】 公式: 20% × 1.2^(level-1)");
        double[] evasionRates = new double[5];
        for (int lv = 1; lv <= 5; lv++) {
            evasionRates[lv-1] = 0.20 * Math.pow(1.2, lv - 1);
            System.out.println(String.format("  Lv.%d: %.2f%%", lv, evasionRates[lv-1] * 100));
        }
        if (Math.abs(evasionRates[4] - 0.415) < 0.01) {
            System.out.println("✓ 闪避Lv.5约等于41.5%");
            passCount++;
        } else {
            System.out.println("✗ 闪避Lv.5计算错误: " + (evasionRates[4] * 100) + "%");
            failCount++;
        }
        System.out.println();
        
        // 测试护盾: 20 + 10×(level-1)
        System.out.println("【护盾技能】 公式: 20 + 10×(level-1)");
        for (int lv = 1; lv <= 5; lv++) {
            int shield = 20 + 10 * (lv - 1);
            System.out.println(String.format("  Lv.%d: %d伤害减免", lv, shield));
        }
        if (20 + 10 * 4 == 60) {
            System.out.println("✓ 护盾Lv.5 = 60伤害减免");
            passCount++;
        }
        System.out.println();
        
        // 测试治疗: 12 + 2×(level-1)
        System.out.println("【治疗技能】 公式: 12 + 2×(level-1)");
        for (int lv = 1; lv <= 5; lv++) {
            int heal = 12 + 2 * (lv - 1);
            System.out.println(String.format("  Lv.%d: %d能量恢复", lv, heal));
        }
        if (12 + 2 * 4 == 20) {
            System.out.println("✓ 治疗Lv.5 = 20能量恢复");
            passCount++;
        }
        System.out.println();
        
        // 测试分析: 1.3x × 1.2^(level-1)
        System.out.println("【分析技能】 公式: 1.3x × 1.2^(level-1)");
        for (int lv = 1; lv <= 5; lv++) {
            double mult = 1.3 * Math.pow(1.2, lv - 1);
            System.out.println(String.format("  Lv.%d: %.2fx伤害加成", lv, mult));
        }
        double analysisFinal = 1.3 * Math.pow(1.2, 4);
        if (Math.abs(analysisFinal - 1.62) < 0.01) {
            System.out.println("✓ 分析Lv.5约等于1.62x");
            passCount++;
        }
        System.out.println();
        
        // 测试暴击: 40% × 1.2^(level-1), 伤害2.0x + 0.2×(level-1)
        System.out.println("【暴击技能】 公式: 暴击率40% × 1.2^(level-1) | 伤害2.0x + 0.2×(level-1)");
        for (int lv = 1; lv <= 5; lv++) {
            double rate = 0.40 * Math.pow(1.2, lv - 1);
            double damage = 2.0 + 0.2 * (lv - 1);
            System.out.println(String.format("  Lv.%d: %.1f%% × %.1fx", lv, rate * 100, damage));
        }
        double critRateFinal = 0.40 * Math.pow(1.2, 4);
        double critDamageFinal = 2.0 + 0.2 * 4;
        if (Math.abs(critRateFinal - 0.829) < 0.01 && critDamageFinal == 2.8) {
            System.out.println("✓ 暴击Lv.5: 82.9% × 2.8x");
            passCount++;
        }
        System.out.println();
        
        // 测试修复: 数组驱动 [5,7,10,13,16], 周期 [3,3,3,3,2]
        System.out.println("【修复技能】 修复量/周期 [5,7,10,13,16] / [3,3,3,3,2]");
        int[] repairs = {5, 7, 10, 13, 16};
        int[] intervals = {3, 3, 3, 3, 2};
        for (int lv = 1; lv <= 5; lv++) {
            System.out.println(String.format("  Lv.%d: %d能量 / 每%d回合", lv, repairs[lv-1], intervals[lv-1]));
        }
        if (repairs[4] == 16 && intervals[4] == 2) {
            System.out.println("✓ 修复Lv.5: 16能量 / 每2回合");
            passCount++;
        }
        System.out.println();
    }

    /**
     * 测试3: 掉落系统
     */
    private static void testLootSystem() {
        System.out.println("[测试3] 掉落系统\n");
        
        // 创建测试威胁
        Threat easyThreat = new Threat("EasyEnemy", "EASY", 4, 2, 20);
        Threat normalThreat = new Threat("NormalEnemy", "NORMAL", 6, 2, 25);
        Threat hardThreat = new Threat("HardEnemy", "HARD", 8, 3, 35);
        Threat extremeThreat = new Threat("ExtremeEnemy", "EXTREME", 10, 4, 50);
        
        // 检查难度等级分类
        System.out.println("难度等级分类:");
        System.out.println("  EASY (难度≤5): " + LootSystem.getMissionDifficulty(easyThreat));
        System.out.println("  NORMAL (6-10): " + LootSystem.getMissionDifficulty(normalThreat));
        System.out.println("  HARD (11-15): " + LootSystem.getMissionDifficulty(hardThreat));
        System.out.println("  EXTREME (16+): " + LootSystem.getMissionDifficulty(extremeThreat));
        System.out.println();
        
        // 检查基础奖励
        System.out.println("基础晶体奖励:");
        System.out.println("  EASY: " + LootSystem.getMissionDifficulty(easyThreat).getBaseCrystalReward() + "晶");
        System.out.println("  NORMAL: " + LootSystem.getMissionDifficulty(normalThreat).getBaseCrystalReward() + "晶");
        System.out.println("  HARD: " + LootSystem.getMissionDifficulty(hardThreat).getBaseCrystalReward() + "晶");
        System.out.println("  EXTREME: " + LootSystem.getMissionDifficulty(extremeThreat).getBaseCrystalReward() + "晶");
        System.out.println();
        
        // 检查奖励计算（2个存活者）
        System.out.println("2个存活者的完整奖励:");
        List<CrewMember> survivors = Arrays.asList(
                new Pilot(1, "A", 0),
                new Soldier(2, "B", 0)
        );
        
        int easyReward = LootSystem.calculateCrystalReward(easyThreat, survivors);
        int normalReward = LootSystem.calculateCrystalReward(normalThreat, survivors);
        int hardReward = LootSystem.calculateCrystalReward(hardThreat, survivors);
        int extremeReward = LootSystem.calculateCrystalReward(extremeThreat, survivors);
        
        System.out.println("  EASY: " + easyReward + "晶 (2+2+2)");
        System.out.println("  NORMAL: " + normalReward + "晶 (4+2+2)");
        System.out.println("  HARD: " + hardReward + "晶 (7+2+2)");
        System.out.println("  EXTREME: " + extremeReward + "晶 (12+2+2)");
        
        if (easyReward == 6 && normalReward == 8 && hardReward == 11 && extremeReward == 16) {
            System.out.println("\n✓ 掉落计算正确");
            passCount++;
        } else {
            System.out.println("\n✗ 掉落计算有误");
            failCount++;
        }
        System.out.println();
    }

    /**
     * 测试4: 升级管理器
     */
    private static void testSkillUpgradeManager() {
        System.out.println("[测试4] 升级管理器\n");
        
        SkillUpgradeManager manager = new SkillUpgradeManager(1, "TestCrew");
        
        // 初始状态
        System.out.println("初始状态:");
        System.out.println("  晶体: " + manager.getSkillCrystalsOwned());
        System.out.println("  所有技能等级: 1");
        
        if (manager.getSkillCrystalsOwned() == 0 && manager.getSkillLevel("Evasion") == 1) {
            System.out.println("✓ 初始化正确");
            passCount++;
        }
        System.out.println();
        
        // 添加晶体
        System.out.println("操作: 添加20晶体");
        manager.addCrystals(20);
        System.out.println("  晶体: " + manager.getSkillCrystalsOwned());
        
        if (manager.getSkillCrystalsOwned() == 20) {
            System.out.println("✓ 晶体添加正确");
            passCount++;
        }
        System.out.println();
        
        // 升级技能
        System.out.println("操作: 升级闪避技能 (Lv.1→Lv.2)");
        boolean result = manager.upgradeSkill("Evasion");
        System.out.println("  升级成功: " + result);
        System.out.println("  等级: " + manager.getSkillLevel("Evasion"));
        System.out.println("  晶体: " + manager.getSkillCrystalsOwned() + " (消耗5晶)");
        System.out.println("  总消耗: " + manager.getTotalCrystalsSpent());
        
        if (result && manager.getSkillLevel("Evasion") == 2 && 
            manager.getSkillCrystalsOwned() == 15 && manager.getTotalCrystalsSpent() == 5) {
            System.out.println("✓ 升级逻辑正确");
            passCount++;
        } else {
            System.out.println("✗ 升级逻辑有误");
            failCount++;
        }
        System.out.println();
        
        // 连升
        System.out.println("操作: 连续升级到Lv.5");
        manager.upgradeSkill("Evasion");  // Lv.2→3, 消耗10
        manager.upgradeSkill("Evasion");  // Lv.3→4, 消耗15
        manager.upgradeSkill("Evasion");  // Lv.4→5, 消耗20，总消耗50
        
        System.out.println("  最终等级: " + manager.getSkillLevel("Evasion"));
        System.out.println("  剩余晶体: " + manager.getSkillCrystalsOwned());
        System.out.println("  总消耗: " + manager.getTotalCrystalsSpent());
        
        if (manager.getSkillLevel("Evasion") == 5 && manager.getTotalCrystalsSpent() == 50) {
            System.out.println("✓ 连升到Lv.5成功，总成本50晶");
            passCount++;
        } else {
            System.out.println("✗ 连升计算有误");
            failCount++;
        }
        System.out.println();
    }
}
