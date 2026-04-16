package com.mars.colony.game;

import com.mars.colony.model.CrewMember;
import java.io.Serializable;

/**
 * CrystalAllocator - 水晶分配管理器
 * 玩家使用它从殖民地仓库中分配水晶给宇航员进行升级
 */
public class CrystalAllocator implements Serializable {
    private static final long serialVersionUID = 1L;

    private Colony colony;

    public CrystalAllocator(Colony colony) {
        this.colony = colony;
    }

    /**
     * 获取仓库当前的水晶数量
     */
    public int getAvailableCrystals() {
        return colony.getCrystalsInStorage();
    }

    /**
     * 为宇航员升级特定技能
     * 从仓库中消耗水晶，宇航员升级技能等级
     *
     * @param crewId 宇航员 ID
     * @param skillName 技能名称
     * @return 升级是否成功
     */
    public boolean upgradeCrewSkill(int crewId, String skillName) {
        CrewMember crew = colony.getStorage().getCrew(crewId);
        if (crew == null) {
            System.out.println("[CrystalAllocator] Crew not found: " + crewId);
            return false;
        }

        if (crew.getSpecialAbility() == null) {
            System.out.println("[CrystalAllocator] Crew has no special ability: " + crew.getName());
            return false;
        }

        // 获取当前等级和升级成本
        int currentLevel = crew.getSpecialAbility().getCurrentLevel();
        if (currentLevel >= 5) {
            System.out.println("[CrystalAllocator] Skill already at max level (5)");
            return false;
        }

        // 计算升级成本 (5 × currentLevel)
        int upgradeCost = 5 * (currentLevel + 1);
        int availableCrystals = colony.getCrystalsInStorage();

        if (availableCrystals < upgradeCost) {
            System.out.println(String.format("[CrystalAllocator] Insufficient crystals. Need: %d, Have: %d",
                    upgradeCost, availableCrystals));
            return false;
        }

        // 从仓库扣除水晶
        colony.addCrystalsToStorage(-upgradeCost);

        // 升级技能
        crew.getSpecialAbility().setLevel(currentLevel + 1);

        System.out.println(String.format("[CrystalAllocator] %s upgraded %s to Lv.%d (cost: %d crystals)",
                crew.getName(), skillName, currentLevel + 1, upgradeCost));

        return true;
    }

    /**
     * 直接分配水晶给宇航员（用于其他用途）
     */
    public boolean directAllocate(int crewId, int amount) {
        return colony.allocateCrystalsToCrewMember(crewId, amount);
    }

    /**
     * 获取宇航员和其技能信息（用于 UI 显示）
     */
    public String getAllCrewCrystalInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== 水晶仓库 ===\n"));
        sb.append(String.format("可用水晶: %d\n\n", getAvailableCrystals()));

        sb.append("=== 宇航员升级信息 ===\n");
        for (CrewMember crew : colony.getAllCrew()) {
            if (crew.getSpecialAbility() != null) {
                int currentLevel = crew.getSpecialAbility().getCurrentLevel();
                int nextCost = currentLevel < 5 ? (5 * (currentLevel + 1)) : -1;
                String costStr = nextCost > 0 ? nextCost + " 晶体" : "已满级";

                sb.append(String.format("%s (%s) - Lv.%d → 升级成本: %s\n",
                        crew.getName(), crew.getSpecialization(), currentLevel, costStr));
            }
        }

        return sb.toString();
    }

    /**
     * 显示仓库状态
     */
    public void displayStorageStatus() {
        System.out.println(getAllCrewCrystalInfo());
    }
}
