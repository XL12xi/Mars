package com.mars.colony.model;

/**
 * Medic crew member. Strong utility role with low resilience.
 */
public class Medic extends CrewMember {
    private static final long serialVersionUID = 1L;

    public Medic(int id, String name, int imageResource) {
        super(id, name, "Medic", 7, 2, 18, imageResource);
    }
}
