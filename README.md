# CyberQuest

**Current development version: 0.3.0-alpha.25** · Minecraft **1.20.1** · Forge **47.4.10** · Java **17**

CyberQuest is the data-driven quest and Adventurer's Guild framework for CyberSpectra Season 2. It owns **quest definitions, player progress, journal/tracking, NPC quest binding, generated guild contracts, guild reputation and ranks**. It does **not** own the world's story-act/region/boss state—that belongs to the optional [CyberServer](https://github.com/Cyberspectraa/CyberServer) mod.

## Python-free texture packaging (0.3.0-alpha.23)

CyberQuest's previous local build failed on Windows when the `py` launcher
was unavailable. The original 14 journal and guild-board textures can
now be packaged from a lossless prebuilt ZIP placed locally at
`tools/prebuilt-quest-textures.zip`, with no Python/Pillow installation.
The original Python generator remains available as an optional authoring
tool or fallback when the archive is missing.

See [the complete installation instructions](docs/BUILD_WITHOUT_PYTHON.md).
The prebuilt ZIP is supplied separately with the fix and must be copied
to the tools folder before building without Python.

## Current race field guide (0.3.0-alpha.22)

The Adventurer's Journal now follows CyberRaces' **actual** race
availability: **10 starting races** (Human, Elf, Orc, Tiefling, Nymph,
Catfolk, Dogfolk, Foxfolk, Slime and Arachnid); **Goblin and Dwarf**
are shown separately as **Wild NPC only**; and **Undead** is marked as
an acquired condition. Dragonborn, Fairy and Birdfolk are retired, while
Halfling and Aasimar aren't regular choices, so none of those are offered
as starting races by the guide.

Descriptions have been updated to avoid advertising disabled player
abilities. No changes were made to quests, player progress, journal
pagination or the race registry. See [the exact list and verification
notes](docs/RACE_JOURNAL_CURRENT.md).

The parchment journal (J by default) includes paginated race entries and wraps tracked quest objectives to fit the HUD. The field guide is optional when CyberRaces is absent.

## Main Seven personal quest finish (0.3.0-alpha.24)

Paired with CyberNpc **0.45.10**, all seven journal entries now match the revised personal dialogue. The existing objective IDs, counts, rewards and quest IDs remain unchanged. The new scoped item delivery API allows Kai's personal hand-in to show the plush without accidentally delivering it to the older Mason quest. The other five discussion quests retain order-independent crafting and dialogue progress; Katie's original 64-Yarn task is preserved. No NPC automatically departs. See [CyberNpc's final personal quest guide](https://github.com/Cyberspectraa/CyberNpc/blob/development-no-actions/docs/MAIN_SEVEN_PERSONAL_QUESTS.md).

## Journal simplification (0.3.0-alpha.25)

The journal now has three tabs: Journal, Chronicle and Races. Rumours is no longer a separate tab. Existing active rumour-section quests remain visible in Journal, and completed quests retain their Chronicle entries. No quest definitions or saved progress are deleted.

## Newcomer and personal quests

- **Your First Steps** guides new players from the Pope's introduction to guild class registration, completing automatically after class selection. Starter equipment is optional; no physical town-exit gate is imposed.
- Town and island introduction quests support order-independent dialogue objectives, so players need not repeat a conversation choice just because they selected it early.
- **Main Seven personal quests:** Kai, Katie, Elara, Rowan, Felix, Nadia and Orin offer quests through CyberNpc friendship dialogue at Trust 2 and Affection 1. Katie's original 64-Yarn quest is preserved. Kai's existing Choso Plush must be placed in the world by the series owner and is not consumed on hand-in.
- Crafting objectives track eligible crafts after quest acceptance; the journal and quest HUD reflect the actual progress.

See [guild registration](docs/GUILD_REGISTRATION_MAIN_QUEST.md) and [Main Seven quests](docs/MAIN_SEVEN_SECRET_QUESTS.md).

## Quest system

- Datapack-loaded quest JSON under `data/<namespace>/cyberquests/*.json`; example: `src/main/resources/data/cyberquest/cyberquests/first_hunt.json`.
- Persistent **per-player** quest state, with progression across defined stages.
- Objective types: `kill`, `collect`, `talk`, `deliver_item`, `visit`, `advancement`, `discover` and `craft`.
- Quest prerequisites, Cyber Level requirements, race/evolution/class/advancement requirements and, when CyberServer is installed, story-act/flag/region gates.
- Repeatable and non-repeatable quests, manual or automatic turn-in, optional time limits, completion text and failure penalties.
- Event-driven progress for relevant objectives, with a limited once-per-second refresh for objectives such as inventory counts and location checks.
- Rewards: **Cyber XP**, vanilla XP, items, guild reputation and symbolic class unlock IDs. CyberRaces alpha.76 gates Cyber XP until class registration when CyberClasses is installed.
- GUI quest journal (**J** by default), tracked quest selection, progress sync and a quest HUD overlay.

The journal is a player feature; quest creation and administrative state changes are controlled by server-side logic.

## NPC quest givers

With CyberNpc installed, a compatible NPC can be linked to one or more quests. Interactions support accepting available quests, seeing active progress and turning in completed work. Quest bindings can be stored on an NPC, assigned through a stable NPC identifier, or provided by CyberServer for world-specific story characters.

Current admin commands:

```mcfunction
/cyberquest npc id <entity> <id>
/cyberquest npc bind <entity> <quest>
/cyberquest npc unbind <entity> <quest>
/cyberquest npc inspect <entity>
```

For example, an NPC whose `CyberQuestNpcId` is `mason` can receive the world's Mason quest bindings through CyberServer without the quest engine containing Mason-specific Java code.

## Adventurer's Guild

The physical **Guild Board** displays **4–6 generated contract notices per in-game day**, based on the world, board position and day. Current generated requests include monster hunts (zombies, skeletons, spiders, creepers, drowned, pillagers and witches) and material collection (logs, coal, iron, leather, string).

Contract workflow:

1. Interact with a Guild Board and **take a physical notice**; the chosen paper is removed from that shared board until its daily refresh.
2. Take the notice to a **Guild Receptionist** (from CyberNpc) to **register** the contract.
3. Complete the objectives, then use the receptionist to **turn in and claim rewards**.
4. A same-day, unregistered notice can be put back on its original board after server-side verification.

The active/held limit is **two contracts per player in total**, including unregistered paper notices. Some notices are timed and may apply a guild reputation penalty on expiry. The guild maintains persistent contract-completion counts and reputation, plus a physical **Guild Card** with a dedicated viewer.

### Guild ranks

Ranks are assigned using **both** completed guild contracts and guild reputation:

| Rank | Minimum completed contracts | Minimum reputation |
| --- | ---: | ---: |
| F | Starting rank | Starting rank |
| E | 5 | 10 |
| D | 15 | 25 |
| C | 30 | 50 |
| B | 60 | 100 |
| A | 100 | 175 |
| S | 200 | 300 |

These are the thresholds implemented in `GuildRank`; higher ranks do not by themselves imply additional custom dungeons or guild missions.

## World-specific quest integration

**CyberServer** owns this particular world's state, can attach quests to stable NPC IDs and supplies the sample Mason chain:

- `cyberserver:mason_choso_plush`: deliver one **`cybernpc:choso_plush`** to NPC ID `mason`.
- `cyberserver:mason_secret`: a secret follow-up unlocked after completing the plush quest.

The plush belongs to **CyberNpc**, not the old CyberContent namespace. A `deliver_item` objective is resolved on interaction with the correct named quest NPC; Survival delivery consumes the item if specified while Creative preserves it.

When CyberServer is installed, quests may also include gates such as:

```json
{
  "min_story_act": 2,
  "required_world_flags": ["magic_awakened"],
  "required_unlocked_regions": ["capital"]
}
```

Those keys are **fields within a complete quest definition**, not a complete standalone quest JSON. CyberServer's quest reward multiplier affects Cyber XP and vanilla XP; item rewards remain unchanged.

## Player and admin commands

With CyberNpc 0.45.9+, use the shared `/cyber quest` prefix and `/cyber help quest`. The original `/cyberquest` commands below remain available and retain the same permissions.

```mcfunction
/cyberquest journal
/cyberquest status
```

Administrative commands (permission level 2):

```mcfunction
/cyberquest admin start <player> <quest>
/cyberquest admin complete <player> <quest>
/cyberquest admin reset <player> <quest>
/cyberquest admin resetall <player>
/cyberquest npc id <entity> <id>
/cyberquest npc bind <entity> <quest>
/cyberquest npc unbind <entity> <quest>
/cyberquest npc inspect <entity>
```

**Important:** the old top-level `/cyberquest start`, `complete`, `reset` and `resetall` commands moved under `/cyberquest admin` in the current version. NPC binding remains under `/cyberquest npc`.

## Architecture, compatibility and limits

- **CyberNpc:** optional quest-giver entities and guild receptionist hand-offs. CyberQuest does not require CyberNpc to load.
- **CyberRaces:** optional Cyber Level, race and evolution prerequisites/rewards.
- **CyberClasses:** optional class/advancement gating and class unlock flags.
- **CyberServer:** optional story state, experience multiplier and world-owned quest bindings.
- **Data content:** the engine and generated Guild contracts are implemented. A long, bespoke main campaign, rank-specific quest arcs and many class-ability rewards still require authored content and gameplay validation.

Keep world-specific story JSON and policy in CyberServer or datapacks instead of hardcoding them into the reusable CyberQuest engine.

## Local Windows build

Install matching CyberQuest JARs on client and server. From the CyberMods directory, clone the development branch if needed: `git clone --branch development-no-actions https://github.com/Cyberspectraa/CyberQuest.git`.

```powershell
cd "$HOME\CyberMods\CyberQuest"
git switch development-no-actions
git pull --ff-only
.\build-local.bat
```

For a Python-free build, the separately supplied prebuilt texture ZIP must already be saved as `tools/prebuilt-quest-textures.zip`; see [build without Python](docs/BUILD_WITHOUT_PYTHON.md). Without it, the original generator needs Python and Pillow. Do not put the archive into Minecraft's resourcepacks folder. No GitHub Actions or main-branch changes.

[Historical README](https://github.com/Cyberspectraa/CyberQuest/blob/e2660f5e2129cdf8abeea052f15e3048d6913dce/README.md). Current companion builds: CyberNpc 0.45.12, CyberRaces 0.3.2-alpha.76, CyberClasses 0.2.1-alpha.13 and CyberServer 0.2.7. Local build and gameplay verification remain necessary.
