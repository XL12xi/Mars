# Skill Upgrade System

Each crew role has a special ability:

- Pilot: Evasion.
- Engineer: Shield.
- Medic: Healing.
- Scientist: Analysis.
- Soldier: Critical Strike.
- Robot: Self Repair.

Upgrade rules:

- Skills start at level 1.
- Maximum level is 5.
- Upgrade cost is `5 * currentLevel`.
- Crystals are earned from successful missions and stored by the colony.

Important classes:

- `SpecialAbility`
- `SkillUpgradeManager`
- `CrystalAllocator`
- Role-specific ability classes under `ability`
