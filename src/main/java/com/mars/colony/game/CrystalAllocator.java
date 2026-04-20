package com.mars.colony.game;

import com.mars.colony.model.CrewMember;
import java.io.Serializable;

/**
 * Allocates colony-level crystals to crew members and their special skills.
 */
public class CrystalAllocator implements Serializable {
    private static final long serialVersionUID = 1L;

    private Colony colony;

    public CrystalAllocator(Colony colony) {
        this.colony = colony;
    }

    public int getAvailableCrystals() {
        return colony.getCrystalsInStorage();
    }

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

        int currentLevel = crew.getSpecialAbility().getCurrentLevel();
        if (currentLevel >= 5) {
            System.out.println("[CrystalAllocator] Skill already at max level (5)");
            return false;
        }

        int upgradeCost = 5 * (currentLevel + 1);
        int availableCrystals = colony.getCrystalsInStorage();

        if (availableCrystals < upgradeCost) {
            System.out.println(String.format(
                    "[CrystalAllocator] Insufficient crystals. Need: %d, Have: %d",
                    upgradeCost,
                    availableCrystals
            ));
            return false;
        }

        colony.setCrystalsInStorage(availableCrystals - upgradeCost);
        crew.getSpecialAbility().setLevel(currentLevel + 1);

        System.out.println(String.format(
                "[CrystalAllocator] %s upgraded %s to Lv.%d (cost: %d crystals)",
                crew.getName(),
                skillName,
                currentLevel + 1,
                upgradeCost
        ));

        return true;
    }

    public boolean directAllocate(int crewId, int amount) {
        return colony.allocateCrystalsToCrewMember(crewId, amount);
    }

    public String getAllCrewCrystalInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== Crystal Storage ===%n"));
        sb.append(String.format("Available crystals: %d%n%n", getAvailableCrystals()));

        sb.append("=== Crew Upgrade Info ===\n");
        for (CrewMember crew : colony.getAllCrew()) {
            if (crew.getSpecialAbility() != null) {
                int currentLevel = crew.getSpecialAbility().getCurrentLevel();
                int nextCost = currentLevel < 5 ? (5 * (currentLevel + 1)) : -1;
                String costStr = nextCost > 0 ? nextCost + " crystals" : "MAX";

                sb.append(String.format(
                        "%s (%s) - Lv.%d -> Upgrade cost: %s%n",
                        crew.getName(),
                        crew.getSpecialization(),
                        currentLevel,
                        costStr
                ));
            }
        }

        return sb.toString();
    }

    public void displayStorageStatus() {
        System.out.println(getAllCrewCrystalInfo());
    }
}
