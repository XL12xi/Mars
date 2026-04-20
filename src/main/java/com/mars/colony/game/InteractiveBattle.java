package com.mars.colony.game;

import com.mars.colony.ability.ScientistAnalysis;
import com.mars.colony.ability.SoldierCriticalStrike;
import com.mars.colony.ability.SpecialAbility;
import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Scientist;
import com.mars.colony.model.Soldier;
import com.mars.colony.model.Threat;
import java.util.ArrayList;
import java.util.List;

/**
 * Turn-based interactive battle engine used by the Android battle screen.
 */
public class InteractiveBattle {

    public enum BattlePhase {
        INITIALIZATION,
        PLAYER_CREW_SELECT,
        PLAYER_ACTION_SELECT,
        PLAYER_TURN,
        THREAT_TURN,
        BATTLE_END
    }

    public enum ActionType {
        NORMAL_ATTACK,
        SPECIAL_ABILITY
    }

    private CrewMember crewA;
    private CrewMember crewB;
    private Threat threat;

    private BattlePhase currentPhase;
    private int currentRound;
    private StringBuilder battleLog;

    private int selectedCrewIndex;
    private ActionType selectedAction;
    private CrewMember lastActingCrew;

    private boolean battleOver = false;
    private boolean threatDefeated = false;

    public InteractiveBattle(CrewMember crewA, CrewMember crewB, Threat threat) {
        this.crewA = crewA;
        this.crewB = crewB;
        this.threat = threat;

        this.currentRound = 0;
        this.battleLog = new StringBuilder();
        this.selectedCrewIndex = -1;
        this.currentPhase = BattlePhase.INITIALIZATION;

        initializeBattle();
    }

    private void initializeBattle() {
        addLog("=== BATTLE START ===");
        addLog(String.format(
                "Threat: %s (Skill: %d, Resilience: %d, Energy: %d)",
                threat.getName(),
                threat.getSkill(),
                threat.getResilience(),
                threat.getMaxEnergy()
        ));
        addLog(String.format(
                "Crew A: %s (Skill: %d, Resilience: %d, Energy: %d, Specialization: %s)",
                crewA.getName(),
                crewA.getSkill(),
                crewA.getResilience(),
                crewA.getMaxEnergy(),
                crewA.getSpecialization()
        ));
        addLog(String.format(
                "Crew B: %s (Skill: %d, Resilience: %d, Energy: %d, Specialization: %s)",
                crewB.getName(),
                crewB.getSkill(),
                crewB.getResilience(),
                crewB.getMaxEnergy(),
                crewB.getSpecialization()
        ));
        addLog("");

        currentRound = 1;
        currentPhase = BattlePhase.PLAYER_CREW_SELECT;
    }

    public BattlePhase getCurrentPhase() {
        return currentPhase;
    }

    public boolean selectCrew(int crewIndex) {
        if (currentPhase != BattlePhase.PLAYER_CREW_SELECT) {
            return false;
        }

        if (!crewA.isAlive() && !crewB.isAlive()) {
            currentPhase = BattlePhase.BATTLE_END;
            battleOver = true;
            threatDefeated = false;
            addLog("=== BATTLE END ===");
            addLog("All crew members defeated!");
            return false;
        }

        CrewMember selectedCrew = (crewIndex == 0) ? crewA : crewB;

        if (!selectedCrew.isAlive()) {
            addLog(String.format("[ERROR] %s has been defeated and cannot act!", selectedCrew.getName()));
            return false;
        }

        this.selectedCrewIndex = crewIndex;
        this.lastActingCrew = selectedCrew;

        addLog(String.format("Round %d: %s selected to act", currentRound, selectedCrew.getName()));

        currentPhase = BattlePhase.PLAYER_ACTION_SELECT;
        return true;
    }

    public boolean selectAction(ActionType actionType) {
        if (currentPhase != BattlePhase.PLAYER_ACTION_SELECT) {
            return false;
        }

        this.selectedAction = actionType;
        currentPhase = BattlePhase.PLAYER_TURN;
        return true;
    }

    public void executePlayerTurn() {
        if (currentPhase != BattlePhase.PLAYER_TURN) {
            return;
        }

        CrewMember actor = (selectedCrewIndex == 0) ? crewA : crewB;

        if (!actor.isAlive()) {
            addLog(String.format("[ERROR] %s cannot act - already defeated!", actor.getName()));
            currentPhase = BattlePhase.PLAYER_CREW_SELECT;
            return;
        }

        if (selectedAction == ActionType.NORMAL_ATTACK) {
            int damage = calculateAttackDamage(actor);
            threat.takeDamage(damage);

            addLog(String.format("  %s uses NORMAL ATTACK: %d damage", actor.getName(), damage));
            addLog(String.format("  %s HP: %d / %d", threat.getName(), threat.getEnergy(), threat.getMaxEnergy()));
        } else if (selectedAction == ActionType.SPECIAL_ABILITY) {
            SpecialAbility ability = actor.getSpecialAbility();
            if (ability != null) {
                CrewMember ally = getAlly(actor);
                String abilityName = ability.getAbilityName();
                addLog(String.format(
                        "  %s uses SPECIAL ABILITY: %s (Level %d)",
                        actor.getName(),
                        abilityName,
                        ability.getCurrentLevel()
                ));

                if (ability.canUse(actor, threat, ally)) {
                    ability.executeAbility(actor, threat, ally);
                } else {
                    addLog(String.format("  %s did not trigger this turn", abilityName));
                }

                int damage = calculateAttackDamage(actor);
                threat.takeDamage(damage);
                addLog(String.format("  Attack damage: %d", damage));
                addLog(String.format("  %s HP: %d / %d", threat.getName(), threat.getEnergy(), threat.getMaxEnergy()));
            }
        }

        if (!threat.isAlive()) {
            currentPhase = BattlePhase.BATTLE_END;
            battleOver = true;
            threatDefeated = true;
            addLog("=== BATTLE END ===");
            addLog(String.format("The %s has been defeated!", threat.getName()));
            return;
        }

        addLog("");
        currentPhase = BattlePhase.THREAT_TURN;
    }

    private CrewMember getAlly(CrewMember actor) {
        return actor == crewA ? crewB : crewA;
    }

    private int calculateAttackDamage(CrewMember actor) {
        int attackPower = applyDamageBonus(actor, actor.act());
        int damage = attackPower - threat.defend();
        return Math.max(1, damage);
    }

    private int applyDamageBonus(CrewMember actor, int baseAttackPower) {
        if (actor instanceof Soldier && ((Soldier) actor).isCriticalActive()) {
            float multiplier = getSoldierCriticalMultiplier(actor);
            int boostedAttackPower = Math.round(baseAttackPower * multiplier);
            ((Soldier) actor).setCriticalActive(false);
            addLog(String.format(
                    "  Critical Strike bonus: %.1fx attack (%d -> %d)",
                    multiplier,
                    baseAttackPower,
                    boostedAttackPower
            ));
            return boostedAttackPower;
        }

        if (actor instanceof Scientist && ((Scientist) actor).isAnalysisActive()) {
            float multiplier = getScientistAnalysisMultiplier(actor);
            int boostedAttackPower = Math.round(baseAttackPower * multiplier);
            ((Scientist) actor).setAnalysisActive(false);
            addLog(String.format(
                    "  Analysis bonus: %.1fx attack (%d -> %d)",
                    multiplier,
                    baseAttackPower,
                    boostedAttackPower
            ));
            return boostedAttackPower;
        }

        return baseAttackPower;
    }

    private float getSoldierCriticalMultiplier(CrewMember actor) {
        SpecialAbility ability = actor.getSpecialAbility();
        if (ability instanceof SoldierCriticalStrike) {
            return ((SoldierCriticalStrike) ability).getEffectiveDamageMultiplier();
        }
        return 2.0f;
    }

    private float getScientistAnalysisMultiplier(CrewMember actor) {
        SpecialAbility ability = actor.getSpecialAbility();
        if (ability instanceof ScientistAnalysis) {
            return ((ScientistAnalysis) ability).getEffectiveDamageMultiplier();
        }
        return 1.3f;
    }

    public void executeThreatTurn() {
        if (currentPhase != BattlePhase.THREAT_TURN) {
            return;
        }

        CrewMember defender = lastActingCrew;

        if (!defender.isAlive()) {
            addLog(String.format(
                    "%s is already defeated, threat targets the other crew member instead",
                    defender.getName()
            ));
            defender = (selectedCrewIndex == 0) ? crewB : crewA;
        }

        if (!defender.isAlive()) {
            currentPhase = BattlePhase.BATTLE_END;
            battleOver = true;
            threatDefeated = false;
            addLog("=== BATTLE END ===");
            addLog("All crew members defeated!");
            return;
        }

        int threatAttack = threat.act();
        int damage = threatAttack - defender.defend(threatAttack);
        damage = Math.max(1, damage);
        defender.takeDamage(damage);

        addLog(String.format("%s retaliates: %d damage to %s", threat.getName(), damage, defender.getName()));
        addLog(String.format("  %s HP: %d / %d", defender.getName(), defender.getEnergy(), defender.getMaxEnergy()));

        if (!defender.isAlive()) {
            addLog(String.format("  [DEFEATED] %s has been defeated!", defender.getName()));
        }

        addLog("");

        currentRound++;
        currentPhase = BattlePhase.PLAYER_CREW_SELECT;
    }

    public List<String> getAvailableActions() {
        List<String> actions = new ArrayList<>();
        actions.add("Normal Attack");

        CrewMember selectedCrew = (selectedCrewIndex == 0) ? crewA : crewB;
        if (selectedCrew.getSpecialAbility() != null) {
            String abilityName = selectedCrew.getSpecialAbility().getAbilityName();
            int level = selectedCrew.getSpecialAbility().getCurrentLevel();
            actions.add("Special Ability: " + abilityName + " (Lv." + level + ")");
        }

        return actions;
    }

    public List<String> getControllableCrew() {
        List<String> crew = new ArrayList<>();

        if (crewA.isAlive()) {
            crew.add(String.format(
                    "[CREW_A] %s (%s) - HP: %d/%d",
                    crewA.getName(),
                    crewA.getSpecialization(),
                    crewA.getEnergy(),
                    crewA.getMaxEnergy()
            ));
        }

        if (crewB.isAlive()) {
            crew.add(String.format(
                    "[CREW_B] %s (%s) - HP: %d/%d",
                    crewB.getName(),
                    crewB.getSpecialization(),
                    crewB.getEnergy(),
                    crewB.getMaxEnergy()
            ));
        }

        return crew;
    }

    public List<Integer> getControllableCrewIndices() {
        List<Integer> indices = new ArrayList<>();

        if (crewA.isAlive()) {
            indices.add(0);
        }

        if (crewB.isAlive()) {
            indices.add(1);
        }

        return indices;
    }

    public BattleStatus getBattleStatus() {
        return new BattleStatus(
                currentPhase,
                currentRound,
                crewA.getName(), crewA.getEnergy(), crewA.getMaxEnergy(), crewA.isAlive(), crewA.getSpecialization(),
                crewB.getName(), crewB.getEnergy(), crewB.getMaxEnergy(), crewB.isAlive(), crewB.getSpecialization(),
                threat.getName(), threat.getEnergy(), threat.getMaxEnergy(), threat.isAlive(),
                selectedCrewIndex,
                battleOver
        );
    }

    public boolean isBattleOver() {
        return battleOver;
    }

    public boolean didCrewWin() {
        return threatDefeated;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public String getBattleLog() {
        return battleLog.toString();
    }

    private void addLog(String message) {
        battleLog.append(message).append("\n");
    }

    public Threat getThreat() {
        return threat;
    }

    public CrewMember getCrewA() {
        return crewA;
    }

    public CrewMember getCrewB() {
        return crewB;
    }

    public static class BattleStatus {
        public BattlePhase phase;
        public int round;

        public String crewAName;
        public int crewAEnergy;
        public int crewAMaxEnergy;
        public boolean crewAAlive;
        public String crewASpec;

        public String crewBName;
        public int crewBEnergy;
        public int crewBMaxEnergy;
        public boolean crewBAlive;
        public String crewBSpec;

        public String threatName;
        public int threatEnergy;
        public int threatMaxEnergy;
        public boolean threatAlive;

        public int selectedCrewIndex;
        public boolean battleOver;

        public BattleStatus(
                BattlePhase phase,
                int round,
                String crewAName,
                int crewAEnergy,
                int crewAMaxEnergy,
                boolean crewAAlive,
                String crewASpec,
                String crewBName,
                int crewBEnergy,
                int crewBMaxEnergy,
                boolean crewBAlive,
                String crewBSpec,
                String threatName,
                int threatEnergy,
                int threatMaxEnergy,
                boolean threatAlive,
                int selectedCrewIndex,
                boolean battleOver
        ) {
            this.phase = phase;
            this.round = round;
            this.crewAName = crewAName;
            this.crewAEnergy = crewAEnergy;
            this.crewAMaxEnergy = crewAMaxEnergy;
            this.crewAAlive = crewAAlive;
            this.crewASpec = crewASpec;
            this.crewBName = crewBName;
            this.crewBEnergy = crewBEnergy;
            this.crewBMaxEnergy = crewBMaxEnergy;
            this.crewBAlive = crewBAlive;
            this.crewBSpec = crewBSpec;
            this.threatName = threatName;
            this.threatEnergy = threatEnergy;
            this.threatMaxEnergy = threatMaxEnergy;
            this.threatAlive = threatAlive;
            this.selectedCrewIndex = selectedCrewIndex;
            this.battleOver = battleOver;
        }
    }
}
