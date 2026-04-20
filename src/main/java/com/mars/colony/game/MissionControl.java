package com.mars.colony.game;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;
import java.util.ArrayList;
import java.util.List;

public class MissionControl {
    private final Colony colony;
    private InteractiveBattle currentInteractiveBattle;

    public MissionControl(Colony colony) {
        this.colony = colony;
    }

    public Threat generateThreat() {
        int difficulty = (int) (colony.getMissionCount() / 2) + 1;
        float multiplier = colony.getDifficultyMultiplier();

        int threatSkill = (int) ((4 + difficulty * 0.5f) * multiplier);
        int threatResilience = (int) ((2 + difficulty * 0.3f) * multiplier);
        int threatMaxEnergy = (int) ((20 + difficulty * 2) * multiplier);

        String[] threatNames = {
                "Asteroid Storm", "Radiation Burst", "Solar Flare",
                "Alien Attack", "System Malfunction", "Fire Outbreak"
        };
        String[] threatTypes = {
                "MeteorStorm", "RadiationStorm", "SolarFlare",
                "AlienAttack", "SystemError", "Fire"
        };

        int index = (int) (Math.random() * threatNames.length);
        return new Threat(
                threatNames[index],
                threatTypes[index],
                threatSkill,
                threatResilience,
                threatMaxEnergy
        );
    }

    public InteractiveBattle startInteractiveMission(CrewMember crewA, CrewMember crewB, Threat threat) {
        if (crewA == null || crewB == null || threat == null) {
            return null;
        }

        if (!crewA.isAlive() || !crewB.isAlive()) {
            return null;
        }

        threat.setAlive(true);
        threat.setEnergy(threat.getMaxEnergy());

        this.currentInteractiveBattle = new InteractiveBattle(crewA, crewB, threat);
        return currentInteractiveBattle;
    }

    public InteractiveBattle getCurrentInteractiveBattle() {
        return currentInteractiveBattle;
    }

    public boolean finalizeInteractiveMission(InteractiveBattle battle) {
        if (battle == null || !battle.isBattleOver()) {
            return false;
        }

        CrewMember crewA = battle.getCrewA();
        CrewMember crewB = battle.getCrewB();
        Threat threat = battle.getThreat();

        List<CrewMember> survivors = new ArrayList<>();
        if (crewA.isAlive()) {
            survivors.add(crewA);
        }
        if (crewB.isAlive()) {
            survivors.add(crewB);
        }

        if (!battle.didCrewWin()) {
            recordNoDeathLoss(crewA);
            recordNoDeathLoss(crewB);
            System.out.println("[MissionControl] Battle lost. Crew sent to Medbay with no rewards.");
            return false;
        }

        int crystalReward = LootSystem.calculateCrystalReward(threat, survivors);
        int experienceReward = 2;

        System.out.println(String.format(
                "[MissionControl] Battle won! Survivors: %d, Crystals to storage: %d, Exp: %d",
                survivors.size(),
                crystalReward,
                experienceReward
        ));

        colony.addCrystalsToStorage(crystalReward);
        recordMissionAttempt(crewA);
        recordMissionAttempt(crewB);

        for (CrewMember crew : survivors) {
            crew.addExperience(experienceReward);
            crew.addVictory();
            System.out.println(String.format(
                    "[MissionControl] %s: +%d exp (total: %d)",
                    crew.getName(),
                    experienceReward,
                    crew.getExperience()
            ));
        }

        sendDefeatedCrewToMedbay(crewA);
        sendDefeatedCrewToMedbay(crewB);
        colony.completeMission();

        return true;
    }

    private void recordMissionAttempt(CrewMember crew) {
        if (crew != null) {
            crew.addMissionCompleted();
        }
    }

    private void recordNoDeathLoss(CrewMember crew) {
        if (crew == null) {
            return;
        }

        recordMissionAttempt(crew);
        crew.addLoss();
        crew.addMedbayVisit();
        colony.moveCrewTo(crew.getId(), "QUARTERS");
        crew.recover();
    }

    private void sendDefeatedCrewToMedbay(CrewMember crew) {
        if (crew != null && !crew.isAlive()) {
            crew.addLoss();
            crew.addMedbayVisit();
            colony.moveCrewTo(crew.getId(), "QUARTERS");
            crew.recover();
        }
    }
}
