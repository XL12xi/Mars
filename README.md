# Mars Colony Survival

Mars Colony Survival is an Android Java coursework project for an object-oriented programming module. The player manages a Mars colony crew, moves crew members between colony locations, trains them, recruits new specialists, and sends two crew members into turn-based missions against generated threats.

## Current Feature Set

- Recruit crew members as Pilot, Engineer, Medic, Scientist, Soldier, or Robot.
- Move crew between Quarters, Simulator, and Mission Control.
- Recover crew in Quarters.
- Train selected crew in the Simulator.
- Generate mission threats through `MissionControl.generateThreat()`.
- Scale threat difficulty from completed mission count.
- Run interactive turn-based battles from Mission Control.
- Store mission crystal rewards at the colony level.
- Upgrade crew abilities with crystals.
- Track missions, wins, losses, win rate, training sessions, and medbay visits.
- Display original weapon-style images for each crew role.
- Use No Death handling: failed missions send crew back to Quarters/Medbay and record losses instead of deleting crew.

## Main Screens

- `MainActivity`: navigation hub.
- `RecruitActivity`: creates new crew members.
- `QuartersActivity`: shows crew in Quarters, recovery, and movement.
- `SimulatorActivity`: trains crew assigned to the Simulator.
- `MissionControlActivity`: selects two Mission Control crew members and starts a generated threat mission.
- `BattleActivity`: handles the interactive turn-based battle.
- `TrainingCenterActivity`: shows skill upgrade options.

## Core Classes

- `CrewMember`: abstract base class for all crew roles.
- `Pilot`, `Engineer`, `Medic`, `Scientist`, `Soldier`, `Robot`: role-specific crew types.
- `SpecialAbility`: shared interface for role abilities.
- `Colony`: owns storage, locations, mission count, and crystal storage.
- `MissionControl`: generates threats, starts battles, finalizes mission rewards or failures.
- `InteractiveBattle`: turn-based battle state machine.
- `LootSystem`: calculates crystal rewards from threat difficulty and survivors.
- `SkillUpgradeManager`: tracks per-crew skill upgrade levels and costs.

