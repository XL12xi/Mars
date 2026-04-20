package com.mars.colony.game;

import com.mars.colony.model.CrewMember;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Colony implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Storage storage;
    private final List<CrewMember> quarters = new ArrayList<>();
    private final List<CrewMember> simulator = new ArrayList<>();
    private final List<CrewMember> missionControl = new ArrayList<>();

    private int missionCount = 0;
    private static int missionsCompleted = 0;
    private int crystalStorage = 0;

    public Colony() {
        this.storage = new Storage();
    }

    public void addCrewMember(CrewMember crew) {
        storage.addCrew(crew);
        moveCrewTo(crew.getId(), "QUARTERS");
    }

    public void moveCrewTo(int crewId, String location) {
        CrewMember crew = storage.getCrew(crewId);
        if (crew == null) {
            return;
        }

        removeFromLocation(crew, crew.getLocation());
        crew.setLocation(location);

        switch (location.toUpperCase()) {
            case "QUARTERS":
                quarters.add(crew);
                break;
            case "SIMULATOR":
                simulator.add(crew);
                break;
            case "MISSION_CONTROL":
                missionControl.add(crew);
                break;
            default:
                quarters.add(crew);
                crew.setLocation("QUARTERS");
                break;
        }
    }

    private void removeFromLocation(CrewMember crew, String location) {
        switch (location.toUpperCase()) {
            case "QUARTERS":
                quarters.remove(crew);
                break;
            case "SIMULATOR":
                simulator.remove(crew);
                break;
            case "MISSION_CONTROL":
                missionControl.remove(crew);
                break;
            default:
                break;
        }
    }

    public void trainCrewInSimulator(List<Integer> crewIds) {
        for (Integer id : crewIds) {
            CrewMember crew = storage.getCrew(id);
            if (crew != null && simulator.contains(crew)) {
                crew.train();
            }
        }
    }

    public void completeMission() {
        missionCount++;
        missionsCompleted++;
    }

    public float getDifficultyMultiplier() {
        return 1.0f + (missionCount * 0.1f);
    }

    public Storage getStorage() { return storage; }
    public List<CrewMember> getQuarters() { return quarters; }
    public List<CrewMember> getSimulator() { return simulator; }
    public List<CrewMember> getMissionControl() { return missionControl; }
    public int getMissionCount() { return missionCount; }
    public static int getMissionsCompleted() { return missionsCompleted; }

    public List<CrewMember> getCrewByLocation(String location) {
        switch (location.toUpperCase()) {
            case "QUARTERS":
                return new ArrayList<>(quarters);
            case "SIMULATOR":
                return new ArrayList<>(simulator);
            case "MISSION_CONTROL":
                return new ArrayList<>(missionControl);
            default:
                return new ArrayList<>();
        }
    }

    public List<CrewMember> getAllCrew() {
        return storage.getAllCrew();
    }

    public int getCrewCount() {
        return storage.getCrewCount();
    }

    public int getVictoryCount() {
        int total = 0;
        for (CrewMember crew : storage.getAllCrew()) {
            total += crew.getVictoriesCount();
        }
        return total;
    }

    public CrewMember findCrewByName(String name) {
        for (CrewMember crew : storage.getAllCrew()) {
            if (crew.getName().equals(name)) {
                return crew;
            }
        }
        return null;
    }

    public String getStatsString() {
        return String.format(
                "Crew=%d Missions=%d Quarters=%d Simulator=%d MissionControl=%d Crystals=%d",
                storage.getCrewCount(),
                missionCount,
                quarters.size(),
                simulator.size(),
                missionControl.size(),
                crystalStorage
        );
    }

    public void addCrystalsToStorage(int amount) {
        if (amount > 0) {
            this.crystalStorage += amount;
            System.out.println(String.format(
                    "[Colony] +%d crystals added to storage (total: %d)",
                    amount,
                    this.crystalStorage
            ));
        }
    }

    public int getCrystalsInStorage() {
        return this.crystalStorage;
    }

    public boolean allocateCrystalsToCrewMember(int crewId, int amount) {
        if (amount <= 0 || this.crystalStorage < amount) {
            System.out.println(String.format(
                    "[Colony] Cannot allocate %d crystals (available: %d)",
                    amount,
                    this.crystalStorage
            ));
            return false;
        }

        CrewMember crew = storage.getCrew(crewId);
        if (crew == null) {
            System.out.println("[Colony] Crew not found: " + crewId);
            return false;
        }

        this.crystalStorage -= amount;
        crew.addSkillCrystals(amount);

        System.out.println(String.format(
                "[Colony] Allocated %d crystals to %s (storage now: %d)",
                amount,
                crew.getName(),
                this.crystalStorage
        ));
        return true;
    }

    public void setCrystalsInStorage(int amount) {
        this.crystalStorage = Math.max(0, amount);
    }
}
