# Upgrade System Completion

The skill upgrade system includes:

- A `SpecialAbility` interface.
- Role-specific ability classes.
- `SkillUpgradeManager` for level and cost tracking.
- Crystal storage in `Colony`.
- Crystal allocation through `CrystalAllocator`.
- Upgrade display support in the Training Center UI.

Upgrade cost model:

```text
Next level cost = 5 * currentLevel
Maximum level = 5
```

Current limitation:

- More automated tests would improve confidence in edge cases.

