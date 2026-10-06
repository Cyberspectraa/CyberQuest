# CyberQuest

CyberQuest is the data-driven quest framework for the CyberSpectra Forge 1.20.1 ecosystem.

## Core design

- Quests are loaded from datapack JSON under `data/<namespace>/cyberquests/*.json`.
- Quest progress is persistent per player.
- Objective tracking is event-driven where practical, with a lightweight once-per-second refresh only for inventory and location objectives.
- CyberNpc integration is optional and one-way: CyberQuest can bind quests to CyberNpc entities without CyberNpc depending on CyberQuest.
- Cyber Level, race/evolution and class/advancement requirements are read through compatibility bridges rather than hard dependencies.
- Press **J** to open the quest journal.

## Objective types

- `kill`
- `collect`
- `talk`
- `visit`
- `advancement`

## Example quest

See `src/main/resources/data/cyberquest/cyberquests/first_hunt.json`.

## Quest NPC setup

A CyberNpc can be turned into a quest giver without editing its Java code.

```text
/cyberquest npc bind @e[type=cybernpc:cyber_npc,sort=nearest,limit=1] cyberquest:first_hunt
/cyberquest npc id @e[type=cybernpc:cyber_npc,sort=nearest,limit=1] hunter
/cyberquest npc inspect @e[type=cybernpc:cyber_npc,sort=nearest,limit=1]
```

Right-clicking the bound NPC accepts the first available quest, shows progress while it is active, and turns it in when all objectives are complete.

## Testing

```text
/cyberquest journal
/cyberquest status
/cyberquest start @s cyberquest:first_hunt
/cyberquest complete @s cyberquest:first_hunt
/cyberquest reset @s cyberquest:first_hunt
/cyberquest resetall @s
```


## CyberServer world-state gates

CyberQuest does not depend on CyberServer, but when CyberServer is installed a quest may optionally use server-owned progression gates:

```json
{
  "min_story_act": 2,
  "required_world_flags": ["magic_awakened"],
  "required_unlocked_regions": ["capital"]
}
```

CyberServer's quest reward multiplier scales Cyber XP and vanilla XP rewards. Item rewards are deliberately not multiplied so unique quest items are never duplicated.

A CyberNpc may also use only a stable `CyberQuestNpcId` while CyberServer owns the Season 2 quest bindings for that ID. Local per-entity CyberQuest bindings continue to work and are merged with server bindings.
