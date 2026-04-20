package com.mars.colony.model;

/**
 * Robot crew member. Highest max energy and a round counter for self-repair.
 */
public class Robot extends CrewMember {
    private static final long serialVersionUID = 1L;
    private int roundCount = 0;

    public Robot(int id, String name, int imageResource) {
        super(id, name, "Robot", 7, 2, 22, imageResource);
    }

    public int getRoundCount() { return roundCount; }
    public void incrementRoundCount() { roundCount++; }
    public void resetRoundCount() { roundCount = 0; }
}
