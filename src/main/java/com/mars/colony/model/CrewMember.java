package com.mars.colony.model;

import com.mars.colony.ability.SpecialAbility;
import com.mars.colony.upgrade.SkillUpgradeManager;
import java.io.Serializable;

public abstract class CrewMember implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int id;
    protected String name;
    protected String specialization;
    protected int skill;
    protected int resilience;
    protected int energy;
    protected int maxEnergy;
    protected int experience = 0;
    protected int imageResource;

    protected int missionsCompleted = 0;
    protected int victoriesCount = 0;
    protected int lossesCount = 0;
    protected int medbayVisits = 0;
    protected int trainingSessions = 0;

    protected int skillCrystalsOwned = 0;
    protected int totalCrystalsSpent = 0;

    protected SpecialAbility specialAbility;
    protected SkillUpgradeManager skillUpgradeManager;

    protected boolean alive = true;
    protected String location = "QUARTERS";

    public CrewMember(
            int id,
            String name,
            String specialization,
            int skill,
            int resilience,
            int maxEnergy,
            int imageResource
    ) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.skill = skill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.imageResource = imageResource;
    }

    public int act() {
        return this.skill + this.experience;
    }

    public int defend(int incomingDamage) {
        int defense = this.resilience;

        if (specialAbility != null && specialAbility.canUse(this, null, null)) {
            specialAbility.executeAbility(this, null, null);
        }

        if (this instanceof Engineer) {
            Engineer engineer = (Engineer) this;
            if (engineer.isShieldActive()) {
                defense = Math.max(defense, engineer.getShieldValue());
                engineer.setShieldDuration(engineer.getShieldDuration() - 1);
                if (engineer.getShieldDuration() <= 0) {
                    engineer.setShieldActive(false);
                    engineer.setShieldValue(0);
                }
            }
        }

        return Math.min(incomingDamage, defense);
    }

    public void takeDamage(int damage) {
        if (specialAbility != null && shouldEvade()) {
            if (this instanceof Pilot) {
                ((Pilot) this).clearEvaded();
            }
            return;
        }

        int actualDamage = Math.max(1, damage);
        this.energy -= actualDamage;

        if (this.energy <= 0) {
            this.energy = 0;
            this.alive = false;
        }
    }

    protected boolean shouldEvade() {
        return false;
    }

    public void train() {
        int experienceGain = 1 + (int) (Math.random() * 3);
        int energyLoss = 5 + (int) (Math.random() * 6);

        addExperience(experienceGain);
        trainingSessions++;
        setEnergy(this.energy - energyLoss);
    }

    public void recover() {
        this.energy = this.maxEnergy;
        this.alive = true;
    }

    public void addSkillCrystals(int amount) {
        if (amount <= 0) {
            return;
        }

        this.skillCrystalsOwned += amount;
        if (skillUpgradeManager != null) {
            skillUpgradeManager.addCrystals(amount);
        }
    }

    public boolean useSkillCrystals(int cost) {
        if (cost <= 0 || this.skillCrystalsOwned < cost) {
            return false;
        }

        this.skillCrystalsOwned -= cost;
        this.totalCrystalsSpent += cost;
        return true;
    }

    public void addExperience(int amount) {
        if (amount > 0) {
            this.experience += amount;
        }
    }

    public void addTrainingSession() {
        this.trainingSessions++;
    }

    public void addMissionCompleted() {
        this.missionsCompleted++;
    }

    public void addVictory() {
        this.victoriesCount++;
    }

    public void addLoss() {
        this.lossesCount++;
    }

    public void addMedbayVisit() {
        this.medbayVisits++;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public int getSkill() { return skill; }
    public int getResilience() { return resilience; }
    public int getEnergy() { return energy; }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(energy, maxEnergy));
        this.alive = this.energy > 0;
    }

    public int getMaxEnergy() { return maxEnergy; }
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = Math.max(0, experience); }
    public int getImageResource() { return imageResource; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getMissionsCompleted() { return missionsCompleted; }
    public void setMissionsCompleted(int count) { missionsCompleted = Math.max(0, count); }
    public int getVictoriesCount() { return victoriesCount; }
    public void setVictoriesCount(int count) { victoriesCount = Math.max(0, count); }
    public int getLossesCount() { return lossesCount; }
    public void setLossesCount(int count) { lossesCount = Math.max(0, count); }
    public int getMedbayVisits() { return medbayVisits; }
    public void setMedbayVisits(int count) { medbayVisits = Math.max(0, count); }
    public int getTrainingSessions() { return trainingSessions; }

    public int getSkillCrystalsOwned() { return skillCrystalsOwned; }
    public int getTotalCrystalsSpent() { return totalCrystalsSpent; }

    public SpecialAbility getSpecialAbility() { return specialAbility; }
    public void setSpecialAbility(SpecialAbility ability) { specialAbility = ability; }

    public SkillUpgradeManager getSkillUpgradeManager() { return skillUpgradeManager; }

    public void setSkillUpgradeManager(SkillUpgradeManager manager) {
        this.skillUpgradeManager = manager;
        if (manager == null) {
            return;
        }

        if (manager.getSkillCrystalsOwned() < skillCrystalsOwned) {
            manager.addCrystals(skillCrystalsOwned - manager.getSkillCrystalsOwned());
        } else if (manager.getSkillCrystalsOwned() > skillCrystalsOwned) {
            skillCrystalsOwned = manager.getSkillCrystalsOwned();
        }
    }

    public double getWinRate() {
        if (missionsCompleted == 0) {
            return 0.0;
        }
        return (double) victoriesCount / missionsCompleted * 100;
    }

    public int getEffectiveSkill() {
        return skill + experience;
    }

    @Override
    public String toString() {
        return String.format(
                "%s(%s) Skill:%d Res:%d Energy:%d/%d Exp:%d",
                name,
                specialization,
                skill,
                resilience,
                energy,
                maxEnergy,
                experience
        );
    }
}
