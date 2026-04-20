package com.mars.colony;

import com.mars.colony.ability.PilotEvasion;
import com.mars.colony.ability.SoldierCriticalStrike;
import com.mars.colony.game.Colony;
import com.mars.colony.game.CrystalAllocator;
import com.mars.colony.game.InteractiveBattle;
import com.mars.colony.game.MissionControl;
import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Pilot;
import com.mars.colony.model.Soldier;
import com.mars.colony.model.Threat;

/**
 * Console demo for colony-level crystal storage and crew skill upgrades.
 */
public class GlobalCrystalSystemDemo {

    public static void main(String[] args) {
        System.out.println("=== Global Crystal Storage Demo ===\n");

        Colony colony = new Colony();

        CrewMember pilot = new Pilot(colony.getStorage().getNextCrewId(), "Nova", 0);
        pilot.setSpecialAbility(new PilotEvasion());
        colony.addCrewMember(pilot);

        CrewMember soldier = new Soldier(colony.getStorage().getNextCrewId(), "Rex", 0);
        soldier.setSpecialAbility(new SoldierCriticalStrike());
        colony.addCrewMember(soldier);

        System.out.println("Recruited 2 crew members.");
        System.out.println(colony.getStatsString());
        System.out.println("Storage crystals: " + colony.getCrystalsInStorage() + "\n");

        MissionControl missionControl = new MissionControl(colony);
        CrystalAllocator allocator = new CrystalAllocator(colony);

        for (int i = 1; i <= 3; i++) {
            System.out.println("--- Mission #" + i + " ---");
            colony.moveCrewTo(pilot.getId(), "MISSION_CONTROL");
            colony.moveCrewTo(soldier.getId(), "MISSION_CONTROL");

            Threat threat = new Threat("Training Hazard", "Training", 1, 0, 6);
            InteractiveBattle battle = missionControl.startInteractiveMission(pilot, soldier, threat);
            if (battle != null) {
                runAutoBattle(battle);
                missionControl.finalizeInteractiveMission(battle);
            }

            System.out.println("Current storage crystals: " + colony.getCrystalsInStorage() + "\n");
        }

        System.out.println("=== Crystal Allocation Options ===");
        allocator.displayStorageStatus();

        System.out.println("=== Upgrade Attempts ===");
        allocator.upgradeCrewSkill(pilot.getId(), "Evasion");
        System.out.println("Storage crystals after Nova upgrade: " + allocator.getAvailableCrystals());

        allocator.upgradeCrewSkill(soldier.getId(), "CriticalStrike");
        System.out.println("Storage crystals after Rex upgrade: " + allocator.getAvailableCrystals());

        System.out.println("\n=== Final State ===");
        System.out.println("Nova Evasion: Lv." + pilot.getSpecialAbility().getCurrentLevel());
        System.out.println("Rex Critical Strike: Lv." + soldier.getSpecialAbility().getCurrentLevel());
        System.out.println("Remaining storage crystals: " + allocator.getAvailableCrystals());
    }

    private static void runAutoBattle(InteractiveBattle battle) {
        while (!battle.isBattleOver()) {
            switch (battle.getCurrentPhase()) {
                case PLAYER_CREW_SELECT:
                    battle.selectCrew(0);
                    break;
                case PLAYER_ACTION_SELECT:
                    battle.selectAction(InteractiveBattle.ActionType.NORMAL_ATTACK);
                    break;
                case PLAYER_TURN:
                    battle.executePlayerTurn();
                    break;
                case THREAT_TURN:
                    battle.executeThreatTurn();
                    break;
                case BATTLE_END:
                case INITIALIZATION:
                default:
                    return;
            }
        }
    }
}
