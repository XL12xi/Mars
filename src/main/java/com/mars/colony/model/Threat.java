package com.mars.colony.model;

import java.io.Serializable;

/**
 * Threat model used by mission battles.
 */
public class Threat implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String type;
    private int skill;
    private int resilience;
    private int energy;
    private int maxEnergy;
    private boolean alive = true;

    public Threat(String name, String type, int skill, int resilience, int maxEnergy) {
        this.name = name;
        this.type = type;
        this.skill = skill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
    }

    public int act() {
        return this.skill;
    }

    public int defend() {
        return this.resilience;
    }

    public void takeDamage(int damage) {
        int actualDamage = Math.max(1, damage);
        this.energy -= actualDamage;
        if (this.energy <= 0) {
            this.energy = 0;
            this.alive = false;
        }
    }

    public int calculateDifficulty() {
        return this.skill + this.resilience + (this.maxEnergy / 10);
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getSkill() { return skill; }
    public int getResilience() { return resilience; }
    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = Math.max(0, energy); }
    public int getMaxEnergy() { return maxEnergy; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }

    @Override
    public String toString() {
        return String.format(
                "%s(Skill:%d Res:%d Energy:%d/%d)",
                name,
                skill,
                resilience,
                energy,
                maxEnergy
        );
    }
}
