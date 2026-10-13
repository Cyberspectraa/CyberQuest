# CyberQuest

**Current version: 0.3.0-alpha.11** · Minecraft **1.20.1** · Forge **47.4.10** · Java **17**

CyberQuest is the data-driven quest and Adventurer's Guild framework for CyberSpectra Season 2. It owns **quest definitions, player progress, journal/tracking, NPC quest binding, generated guild contracts, guild reputation and ranks**. It does **not** own the world's story-act/region/boss state—that belongs to the optional [CyberServer](https://github.com/Cyberspectraa/CyberServer) mod.

## Races reference tab (0.3.0-alpha.11)

The parchment Adventurer's Journal includes a fourth **Races** tab.
It shows the 15 CyberRaces starting races plus **Undead**, which is now
an acquired Zombification condition. Each entry explains the race's
identity, advantages, active ability and trade-offs. Five entries appear
on each journal page; Prev/Next paginate the left leaf, while selecting
a race displays its details on the right leaf. The layout uses existing
CyberQuest journal textures and makes no new network or save data.

The tab reads a lightweight local field guide and appears when
CyberRaces is installed. CyberQuest still works without CyberRaces:
the tab then displays an installation hint rather than failing to
load. Race guide copy is aligned to CyberRaces **0.3.2-alpha.52**;
existing users should install that CyberRaces version before relying
on the guide's Slime, Arachnid and crafting descriptions.

Only replace CyberQuest.jar on client/server for this journal UI update.
Its quests, contracts, quest progress and tracking controls are intact.

## Quest system

- Datapack-loaded quest JSON under `data/<namespace>/cyberquests/*.json`; example: `src/main/resources/data/cyberquest/cyberquests/first_hunt.json`.
- Persistent **per-player** quest state, with progression across defined stages.
- Objective types: `kill`, `collect`, `talk`, `deliver_item`, `visit`, `advancement` and `discover`.
- Quest prerequisites, Cyber Level requirements, race/evolution/class/advancement requirements and, when CyberServer is installed, story-act/flag/region gates.
- Repeatable and non-repeatable quests, manual or automatic turn-in, optional time limits, completion text and failure penalties.
- Event-driven progress for relevant objectives, with a limited once-per-second refresh for objectives such as inventory counts and location checks.
- Rewards: **Cyber XP**, vanilla XP, items, guild reputation and symbolic class unlock IDs.
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
