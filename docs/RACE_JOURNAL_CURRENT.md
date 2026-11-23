# Adventurer's Journal — current races (CyberQuest 0.3.0-alpha.22)

This update corrects the Races tab to match the actual CyberRaces
`development-no-actions` source (its `Race.selectableAtCreation()` and
`Race.allowedForWildNpc()` logic, plus `docs/RACE_RETIREMENT.md`).

## Starting races (10)

Human, Elf, Orc, Tiefling, Nymph, Catfolk, Dogfolk, Foxfolk, Slime, Arachnid.

These are selectable by players during normal character creation.

## World inhabitants not selectable by players (2)

Goblin and Dwarf continue to exist as Wild NPC races, but are retired
from player character creation. The journal identifies these entries
as **Wild NPC only**, not starting races. Their descriptions are written
as field-guide information rather than promising currently disabled
player V-key abilities.

## Acquired condition (1)

Undead is an infection/condition acquired after creation; it is not a
starting player ancestry. The journal labels it **Acquired condition**.

## Entries deliberately not advertised

- Dragonborn, Fairy and Birdfolk are retired for both player creation and
  new natural Wild NPC race selection.
- Halfling and Aasimar remain enum IDs for old save compatibility but are
  not normal player starting choices or Wild NPC choices.
- Retired race IDs and unrelated CyberRaces data are **not deleted**.
  Nothing in the mod's save format, quest references, Journal tab handling,
  race attributes or keybind implementation is changed.

CyberQuest intentionally keeps no hard compile-time dependency on the
optional CyberRaces mod. If CyberRaces is absent, the Races tab continues
to show its existing empty/disabled state instead of crashing.

## Verification for local testing

1. Install CyberQuest 0.3.0-alpha.22 and the matching current CyberRaces
   development version in the Forge 1.20.1 client/server.
2. Open the journal with **J**, choose Races and page through the 13 entries.
3. Confirm exactly 10 items have the status `Starting race`, followed
   by Goblin and Dwarf marked `Wild NPC only` and Undead marked
   `Acquired condition`.
4. Confirm retired/unselectable Dragonborn, Fairy, Birdfolk, Halfling and
   Aasimar are not presented as starting choices.
5. At GUI scale 2 and 4, verify pagination, text wrapping and selection.
6. Open the actual CyberRaces creator and compare its options directly.
   Check unrelated quests, tracking and journal progress are unchanged.

No GitHub Actions should be triggered. This is a source change on
`development-no-actions`; it has not yet been compiled or game-tested.
