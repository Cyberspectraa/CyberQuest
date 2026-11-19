# The Fractured Island — Main Seven personal quests (CyberQuest 0.3.0-alpha.21)

Six new side quests are supplied in datapacks. **Katie's existing `cyberquest:katie_yarn` quest is preserved unchanged**. No quest starts from ordinary right-click; CyberNpc's authored friendship dialogue (requires matching CyberNpc update) grants these six quests at relationship Trust >=2 and Affection >=1.

| NPC | ID | Quest | Objectives |
| --- | --- | --- | --- |
| Kai | `kai_choso_plush` | A Piece of Home | **Show** existing `cybernpc:choso_plush` to Kai, do not consume it |
| Katie | `katie_yarn` (existing) | A Little Yarn | Craft 64 `cybernpc:yarn` |
| Elara | `elara_lost_route` | The Lost Route | Craft map; compare notes about dangerous paths |
| Rowan | `rowan_welcome_meal` | A Table for Strangers | Craft 8 bread; discuss a welcome meal |
| Felix | `felix_fair_deck` | A Fair Hand | Craft 12 paper; discuss the old cards |
| Nadia | `nadia_failsafe` | The Last Failsafe | Craft 2 repeaters; plan a failsafe test |
| Orin | `orin_star_notes` | A Sky Worth Remembering | Craft spyglass; discuss the sky |

All six new quests use existing `CRAFT`, `DISCOVER` and `DELIVER_ITEM` objective types. No new quest data format, worldgen or non-vanilla dependencies. All are manually turned in through their NPC's secret dialogue and give small vanilla XP; CyberNpc grants a one-time Trust improvement on completion. They are not guild contracts.

**Kai's plush already exists but has no natural acquisition route**: the series owner should place it in a hidden chest or otherwise make it discoverable manually. We intentionally do not automatically spawn the special story item. For Katie, existing progress and completion data remain valid.

## Testing
- Install both CyberNpc and CyberQuest builds from their respective development branches.
- Raise Trust/Affection through existing non-repeatable daily conversations, then verify the secret choice appears.
- Speak to each NPC; do not see unrelated characters' secret quests.
- Progress two objectives in either order for the other five; crafted items must be crafted *after* accepting.
- Kai: while holding the pre-existing plush, choose the return option; player should keep it.
- Katie: existing active/completed yarn quest should remain accessible after upgrade.
- Finish each quest once, collect rewards once and confirm the completed follow-up line.
- Multiplayer: per-player quest and friendship state must not leak.
- No automatic quest unlocking from plain interaction; no engine/packet changes.
