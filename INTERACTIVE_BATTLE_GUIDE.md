# Interactive Battle Guide

Battle flow:

1. Mission Control selects two alive crew members.
2. A generated threat is passed into `BattleActivity`.
3. `InteractiveBattle` starts in `PLAYER_CREW_SELECT`.
4. The player selects an alive crew member.
5. The player selects a normal attack or special ability.
6. The threat retaliates if still alive.
7. The loop repeats until the threat or both crew members are defeated.
8. `MissionControl.finalizeInteractiveMission` applies rewards or No Death failure handling.

Important methods:

- `selectCrew`
- `selectAction`
- `executePlayerTurn`
- `executeThreatTurn`
- `getBattleStatus`

