# UI Integration Guide

The UI reads and mutates shared game state through `GameState.getColony()`.

Integration rules:

- Quarters should only show crew whose location is `QUARTERS`.
- Simulator should only train crew whose location is `SIMULATOR`.
- Mission Control should only launch missions with crew whose location is `MISSION_CONTROL`.
- Battle completion must call `MissionControl.finalizeInteractiveMission`.
- UI adapters should refresh after movement, training, or mission completion.

This keeps game rules in Java domain classes while activities focus on presentation and user actions.

