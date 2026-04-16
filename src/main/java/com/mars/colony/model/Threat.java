package com.mars.colony.model;

import java.io.Serializable;

/**
 * 威胁类 - 代表在任务中的敌人/威胁
 */
public class Threat implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String type;           // 威胁类型(如"MeteorStorm"等)
    private int skill;
    private int resilience;
    private int energy;
    private int maxEnergy;
    private boolean alive = true;

    /**
     * 构造函数
     */
    public Threat(String name, String type, int skill, int resilience, int maxEnergy) {
        this.name = name;
        this.type = type;
        this.skill = skill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
    }

    /**
     * 发动攻击
     */
    public int act() {
        return this.skill;
    }

    /**
     * 防御
     */
    public int defend() {
        return this.resilience;
    }

    /**
     * 受到伤害
     */
    public void takeDamage(int damage) {
        int actualDamage = Math.max(1, damage);
        this.energy -= actualDamage;
        if (this.energy <= 0) {
            this.energy = 0;
            this.alive = false;
        }
    }

    /**
     * 计算难度(用于掉落晶体)
     */
    public int calculateDifficulty() {
        return (this.skill + this.resilience + (this.maxEnergy / 10));
    }

    // ===== Get/Set =====
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
        return String.format("%s(Skill:%d Res:%d Energy:%d/%d)", 
                name, skill, resilience, energy, maxEnergy);
    }
}
