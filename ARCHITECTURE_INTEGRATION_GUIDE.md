# Architecture Integration Guide

The Android UI uses the shared Java game model under `src/main/java`.

Key integration points:

- `GameState` provides the singleton colony used by activities.
- `Colony` owns crew storage, locations, mission count, and crystals.
- Activities filter crew by location before enabling actions.
- `MissionControlActivity` generates threats through `MissionControl`.
- `BattleActivity` drives `InteractiveBattle` and then calls `MissionControl.finalizeInteractiveMission`.

Keep UI code thin where possible. Game rules should stay in `game`, `model`, `ability`, and `upgrade` packages.
