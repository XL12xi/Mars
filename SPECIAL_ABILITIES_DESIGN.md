# Special Abilities Design

The project uses polymorphism through the `SpecialAbility` interface.

Implemented abilities:

- `PilotEvasion`: chance to avoid damage.
- `EngineerShield`: temporary defensive shield.
- `MedicHealing`: restores crew energy.
- `ScientistAnalysis`: marks analysis bonus against selected threat types.
- `SoldierCriticalStrike`: chance to mark a critical hit.
- `RobotSelfRepair`: restores energy on a turn interval.

This design lets each role keep unique behavior while the battle engine calls the same interface methods.
