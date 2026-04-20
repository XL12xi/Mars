# Crystal Reward Fix

Mission crystal rewards are stored at colony level.

Current reward behavior:

- Rewards are calculated by `LootSystem`.
- Successful missions add crystals to `Colony`.
- Failed missions in No Death mode grant no rewards.
- `CrystalAllocator` spends colony crystals when upgrading crew abilities.

This avoids giving crystals directly to individual crew members before the player chooses upgrades.

