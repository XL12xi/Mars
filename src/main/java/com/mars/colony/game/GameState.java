package com.mars.colony.game;

import com.mars.colony.ability.EngineerShield;
import com.mars.colony.ability.MedicHealing;
import com.mars.colony.ability.PilotEvasion;
import com.mars.colony.ability.RobotSelfRepair;
import com.mars.colony.ability.ScientistAnalysis;
import com.mars.colony.ability.SoldierCriticalStrike;
import com.mars.colony.R;
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

    public static CrewMember recruitCrew(String name, String specialization) {
        Colony colony = getColony();
        int id = colony.getStorage().getNextCrewId();
        CrewMember crew;

        switch (specialization) {
            case "Pilot":
                crew = new Pilot(id, name, imageForSpecialization("Pilot"));
                addCrew(colony, crew, new PilotEvasion());
                break;
            case "Engineer":
                crew = new Engineer(id, name, imageForSpecialization("Engineer"));
                addCrew(colony, crew, new EngineerShield());
                break;
            case "Medic":
                crew = new Medic(id, name, imageForSpecialization("Medic"));
                addCrew(colony, crew, new MedicHealing());
                break;
            case "Scientist":
                crew = new Scientist(id, name, imageForSpecialization("Scientist"));
                addCrew(colony, crew, new ScientistAnalysis());
                break;
            case "Soldier":
                crew = new Soldier(id, name, imageForSpecialization("Soldier"));
                addCrew(colony, crew, new SoldierCriticalStrike());
                break;
            case "Robot":
                crew = new Robot(id, name, imageForSpecialization("Robot"));
                addCrew(colony, crew, new RobotSelfRepair());
                break;
            default:
                throw new IllegalArgumentException("Unknown specialization: " + specialization);
        }

        return crew;
    }

    private static Colony createDefaultColony() {
        Colony colony = new Colony();

        addCrew(colony, new Pilot(colony.getStorage().getNextCrewId(), "Nova", imageForSpecialization("Pilot")), new PilotEvasion());
        addCrew(colony, new Soldier(colony.getStorage().getNextCrewId(), "Rex", imageForSpecialization("Soldier")), new SoldierCriticalStrike());
        addCrew(colony, new Engineer(colony.getStorage().getNextCrewId(), "Maya", imageForSpecialization("Engineer")), new EngineerShield());
        addCrew(colony, new Medic(colony.getStorage().getNextCrewId(), "Iris", imageForSpecialization("Medic")), new MedicHealing());
        addCrew(colony, new Scientist(colony.getStorage().getNextCrewId(), "Tesla", imageForSpecialization("Scientist")), new ScientistAnalysis());
        addCrew(colony, new Robot(colony.getStorage().getNextCrewId(), "Unit-7", imageForSpecialization("Robot")), new RobotSelfRepair());

        return colony;
    }

    private static void addCrew(Colony colony, CrewMember crew, com.mars.colony.ability.SpecialAbility ability) {
        crew.setSpecialAbility(ability);

        SkillUpgradeManager manager = new SkillUpgradeManager(crew.getId(), crew.getName());
        crew.setSkillUpgradeManager(manager);
        crew.addSkillCrystals(12);

        colony.addCrewMember(crew);
    }

    private static int imageForSpecialization(String specialization) {
        switch (specialization) {
            case "Pilot":
                return R.drawable.crew_pilot;
            case "Engineer":
                return R.drawable.crew_engineer;
            case "Medic":
                return R.drawable.crew_medic;
            case "Scientist":
                return R.drawable.crew_scientist;
            case "Soldier":
                return R.drawable.crew_soldier;
            case "Robot":
                return R.drawable.crew_robot;
            default:
                return R.drawable.crew_pilot;
        }
    }
}
