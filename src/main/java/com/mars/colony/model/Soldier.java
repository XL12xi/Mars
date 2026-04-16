package com.mars.colony.model;

/**
 * Soldier (士兵) - 技能9, 韧性0, 能量16
 */
public class Soldier extends CrewMember {
    private static final long serialVersionUID = 1L;
    private boolean criticalActive = false;
    
    public Soldier(int id, String name, int imageResource) {
        super(id, name, "Soldier", 9, 0, 16, imageResource);
    }

    public boolean isCriticalActive() { return criticalActive; }
    public void setCriticalActive(boolean active) { criticalActive = active; }
}
