package com.mars.colony;

import com.mars.colony.game.Colony;
import com.mars.colony.game.InteractiveBattle;
import com.mars.colony.game.MissionControl;
import com.mars.colony.model.Engineer;
import com.mars.colony.model.Pilot;
import com.mars.colony.model.Threat;

/**
 * Console demo for the interactive turn-based battle engine.
 */
public class InteractiveBattleDemo {

    public static void main(String[] args) {
        System.out.println("=== Interactive Battle Demo ===\n");

        Colony colony = new Colony();
        MissionControl missionControl = new MissionControl(colony);

        Pilot pilot = new Pilot(1, "Nova", 0);
        Engineer engineer = new Engineer(2, "Rex", 0);
        colony.addCrewMember(pilot);
        colony.addCrewMember(engineer);

        Threat threat = missionControl.generateThreat();
        System.out.println("Generated threat: " + threat);
        System.out.println();

        InteractiveBattle battle = missionControl.startInteractiveMission(pilot, engineer, threat);
        if (battle == null) {
            System.out.println("Failed to start mission.");
            return;
        }

        simulateInteractiveBattle(battle);
        missionControl.finalizeInteractiveMission(battle);

        System.out.println("\n" + battle.getBattleLog());
        System.out.println("\n=== Battle Summary ===");
        System.out.println("Winner: " + (battle.didCrewWin() ? "Crew" : "Threat"));
        System.out.println("Pilot HP: " + pilot.getEnergy() + "/" + pilot.getMaxEnergy());
        System.out.println("Engineer HP: " + engineer.getEnergy() + "/" + engineer.getMaxEnergy());
        System.out.println("Threat HP: " + threat.getEnergy() + "/" + threat.getMaxEnergy());
        System.out.println("Total rounds: " + battle.getCurrentRound());
    }

    private static void simulateInteractiveBattle(InteractiveBattle battle) {
        while (!battle.isBattleOver()) {
            InteractiveBattle.BattlePhase phase = battle.getCurrentPhase();
            System.out.println("Battle phase: " + phase + " (Round " + battle.getCurrentRound() + ")");

            switch (phase) {
                case PLAYER_CREW_SELECT:
                    int crewChoice = selectRandomAliveCrewIndex(battle);
                    System.out.println("Crew selected: " + crewChoice);
                    battle.selectCrew(crewChoice);
                    break;
                case PLAYER_ACTION_SELECT:
                    System.out.println("Action selected: NORMAL_ATTACK");
                    battle.selectAction(InteractiveBattle.ActionType.NORMAL_ATTACK);
                    break;
                case PLAYER_TURN:
                    battle.executePlayerTurn();
                    System.out.println("Crew turn executed");
                    break;
                case THREAT_TURN:
                    battle.executeThreatTurn();
                    System.out.println("Threat turn executed");
                    break;
                case BATTLE_END:
                    System.out.println("Battle ended.");
                    return;
                case INITIALIZATION:
                default:
                    System.out.println("Unknown phase: " + phase);
                    return;
            }

            System.out.println();
        }
    }

    private static int selectRandomAliveCrewIndex(InteractiveBattle battle) {
        InteractiveBattle.BattleStatus status = battle.getBattleStatus();

        if (status.crewAAlive && status.crewBAlive) {
            return (int) (Math.random() * 2);
        } else if (status.crewAAlive) {
            return 0;
        } else if (status.crewBAlive) {
            return 1;
        }
        return -1;
    }
}
