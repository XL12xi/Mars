package com.mars.colony.model;

/**
 * Pilot crew member. Balanced durability with a passive evasion flag.
 */
public class Pilot extends CrewMember {
    private static final long serialVersionUID = 1L;
    private boolean evaded = false;

    public Pilot(int id, String name, int imageResource) {
        super(id, name, "Pilot", 5, 4, 20, imageResource);
    }

    public boolean isEvaded() { return evaded; }
    public void setEvaded(boolean evaded) { this.evaded = evaded; }
    public void clearEvaded() { this.evaded = false; }

    @Override
    protected boolean shouldEvade() {
        return evaded;
    }
}
