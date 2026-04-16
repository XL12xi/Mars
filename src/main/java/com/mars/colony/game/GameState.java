package com.mars.colony.game;

import com.mars.colony.ability.EngineerShield;
import com.mars.colony.ability.MedicHealing;
import com.mars.colony.ability.PilotEvasion;
import com.mars.colony.ability.RobotSelfRepair;
import com.mars.colony.ability.ScientistAnalysis;
import com.mars.colony.ability.SoldierCriticalStrike;
import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Engineer;
import com.mars.colony.model.Medic;
import com.mars.colony.model.Pilot;
import com.mars.colony.model.Robot;
import com.mars.colony.model.Scientist;
import com.mars.colony.model.Soldier;
import com.mars.colony.upgrade.SkillUpgradeManager;

public final class GameState {
    private static Colony colony = createDefaultColony();

    private GameState() {
    }

    public static Colony getColony() {
        if (colony == null || colony.getAllCrew().isEmpty()) {
            colony = createDefaultColony();
        }
        return colony;
    }

    public static void reset() {
        colony = createDefaultColony();
    }

    private static Colony createDefaultColony() {
        Colony colony = new Colony();

        addCrew(colony, new Pilot(colony.getStorage().getNextCrewId(), "Nova", 0), new PilotEvasion());
        addCrew(colony, new Soldier(colony.getStorage().getNextCrewId(), "Rex", 0), new SoldierCriticalStrike());
        addCrew(colony, new Engineer(colony.getStorage().getNextCrewId(), "Maya", 0), new EngineerShield());
        addCrew(colony, new Medic(colony.getStorage().getNextCrewId(), "Iris", 0), new MedicHealing());
        addCrew(colony, new Scientist(colony.getStorage().getNextCrewId(), "Tesla", 0), new ScientistAnalysis());
        addCrew(colony, new Robot(colony.getStorage().getNextCrewId(), "Unit-7", 0), new RobotSelfRepair());

        return colony;
    }

    private static void addCrew(Colony colony, CrewMember crew, com.mars.colony.ability.SpecialAbility ability) {
        crew.setSpecialAbility(ability);

        SkillUpgradeManager manager = new SkillUpgradeManager(crew.getId(), crew.getName());
        crew.setSkillUpgradeManager(manager);
        crew.addSkillCrystals(12);

        colony.addCrewMember(crew);
    }
}
