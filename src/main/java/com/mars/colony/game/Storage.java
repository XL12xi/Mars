package com.mars.colony.game;

import com.mars.colony.model.CrewMember;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stores crew members by id and provides basic lookup helpers.
 */
public class Storage implements Serializable {
    private static final long serialVersionUID = 1L;

    private HashMap<Integer, CrewMember> crewMap = new HashMap<>();
    private int nextCrewId = 1;

    public void addCrew(CrewMember crew) {
        crewMap.put(crew.getId(), crew);
        if (crew.getId() >= nextCrewId) {
            nextCrewId = crew.getId() + 1;
        }
    }

    public void removeCrew(int id) {
        crewMap.remove(id);
    }

    public CrewMember getCrew(int id) {
        return crewMap.get(id);
    }

    public List<CrewMember> getAllCrew() {
        return new ArrayList<>(crewMap.values());
    }

    public List<CrewMember> getCrewByLocation(String location) {
        List<CrewMember> result = new ArrayList<>();
        for (CrewMember crew : crewMap.values()) {
            if (location.equals(crew.getLocation())) {
                result.add(crew);
            }
        }
        return result;
    }

    public int getNextCrewId() {
        return nextCrewId++;
    }

    public int getCrewCount() {
        return crewMap.size();
    }

    public void clear() {
        crewMap.clear();
        nextCrewId = 1;
    }
}
