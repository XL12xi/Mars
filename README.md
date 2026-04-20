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

## Build And Run

Open this folder in Android Studio:

```text
D:\XueXi\JAVA\project\Mars_remote
```

The local SDK path is stored in `local.properties`.

To build from PowerShell on this machine:

```powershell
$env:JAVA_HOME='D:\Tool\Jetbrain\IntelliJ IDEA Community Edition 2025.2.1\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
$env:GRADLE_USER_HOME='C:\Users\haha\.gradle'
.\gradlew.bat assembleDebug
```

The debug APK is generated at:

```text
app\build\outputs\apk\debug\app-debug.apk
```

## Coursework Notes

The project now covers the major planned gameplay flows:

- Crew creation and management.
- Location-based actions.
- Training and recovery.
- System-generated scaled threats.
- Interactive missions.
- Mission rewards and failure handling.
- OOP role hierarchy and polymorphic abilities.

Known remaining polish items:

- Add persistent save/load if required by the final marking rubric.
- Add automated tests for battle and reward edge cases.
- Keep final report screenshots up to date with the latest UI.
