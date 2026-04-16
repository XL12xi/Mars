package com.mars.colony.model;

/**
 * Medic (医疗兵) - 技能7, 韧性2, 能量18
 */
public class Medic extends CrewMember {
    private static final long serialVersionUID = 1L;
    
    public Medic(int id, String name, int imageResource) {
        super(id, name, "Medic", 7, 2, 18, imageResource);
    }
}
