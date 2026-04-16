package com.mars.colony.model;

/**
 * Engineer (工程师) - 技能6, 韧性3, 能量19
 */
public class Engineer extends CrewMember {
    private static final long serialVersionUID = 1L;
    private boolean shieldActive = false;
    private int shieldValue = 0;
    private int shieldDuration = 0;
    
    public Engineer(int id, String name, int imageResource) {
        super(id, name, "Engineer", 6, 3, 19, imageResource);
    }

    public boolean isShieldActive() { return shieldActive; }
    public void setShieldActive(boolean active) { shieldActive = active; }
    public int getShieldValue() { return shieldValue; }
    public void setShieldValue(int value) { shieldValue = value; }
    public int getShieldDuration() { return shieldDuration; }
    public void setShieldDuration(int duration) { shieldDuration = duration; }
}
