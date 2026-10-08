# Changelog

## 0.3.0-alpha.7

### NPC dialogue bridge
- Guild Receptionist actions now run through explicit conversation choices in CyberNpc v0.44.19: register held contract, claim completed guild rewards, and view/issue Guild Card.
- Ordinary quest NPCs activate their quest interaction via the "Do you have any work for me?" dialogue response.
- Removed automatic quest acceptance/registration from raw NPC right-clicks, allowing the shared dialogue screen to open consistently.
- CyberQuest remains optional for CyberNpc; when both are installed their dialogue features integrate through a server-side compatibility bridge.
- Quest saves, inventory contract data, journal visuals and existing guild progression remain unchanged.

## 0.3.0-alpha.6

### Minecraft 1.20.1 vanilla-derived visual overhaul
- Reworked the journal's open-book paper, leather spine, wooden binding, tabs and parchment quest-entry strips into textured, Minecraft-style pixel art.
- Replaced the Guild Board's paper posters (plain, bounty and sealed) with hand-pixel-edited vanilla-paper/map-derived parchment, ink, pinned corners and wax seals.
- Retextured the board's backing and frame using edited Minecraft spruce and dark oak planks, preserving the 3x2 wall-mounted shape and quest interactions.
- Redrew the physical Guild Card, contract and Guild Board item icons by editing Minecraft 1.20.1 map, paper and wood sprite pixels.
- Replaced the Guild Card page with a textured membership sheet that still shows the player's live server-authoritative details.
- Reworked the guild contract preview parchment and quest-tracking HUD to share the parchment/wood visual language.
- Deleted two damaged journal-entry PNG assets and replaced them with verified PNG images generated via Pillow.
- Added a deterministic, build-time pixel texture script embedding exact 1.20.1 source sprite bytes from InventivetalentDev/minecraft-assets. It runs before resource packing, and adds no Python dependency to the game.
- All quest gameplay, registry IDs, networking and saved data remain unchanged.

## 0.3.0-alpha.5

### Repin contracts, two-contract limit, and Guild Card
- Right-click your original Guild Board while holding an unregistered contract to pin the paper back. The board restores the correct note visually for all players.
- Returned notices can be taken by another player; paper is only consumed when successfully pinned back, and the board verifies the original world, position, day and procedural offer.
- Notices can be returned only on the Minecraft day they were issued; older notices can still be registered with the receptionist.
- Limit two guild contracts per player, counting both carried unregistered notices and active registered contracts, enforced server-side; no taking more notices while at the limit.
- Added the reusable Guild Card item. Right-click it to read a server-authoritative member page showing player name, CyberRaces level, guild rank, total quests completed, guild contracts completed, reputation and active slots.
- Guild rank rises F/E/D/C/B/A/S based on cumulative guild completions and reputation. F begins at zero; higher ranks require more completed contracts and reputation.
- Guild Card uses only vanilla item/map and Minecraft book GUI textures, with vanilla fonts; no generated or custom textures.
- Guild Receptionists issue a Guild Card automatically upon first successful contract registration. Players who lost theirs can request a replacement with an empty hand. Cards are deliberately not craftable as membership credentials.
- Completed quest totals now persist across deaths/logins: both authored and guild completions increase total, guild completions separately count towards guild rank.
- Existing completed authored story/class quests are backfilled during save migration; older random guild contracts were not previously recorded permanently and cannot be reconstructed.
- Bumped the network protocol for Guild Card screen packets.

## 0.3.0-alpha.3

### Landscape Guild Board correction
- Rotated the physical Guild Board from 2 blocks wide by 3 blocks tall to the intended 3 blocks wide by 2 blocks tall landscape orientation.
- Kept the same six clickable contract positions and daily 4-6 notice generation, but redistributed the papers across the wider board.
- Rebuilt the outer frame models so the dark-oak border correctly surrounds the new 3x2 footprint.
- Kept the existing serialized board-part names so older worlds can still read the block states instead of failing on removed enum values.
- The board remains thin, wall-mounted, multiplayer-safe, individually clickable, and uses the same parchment/bounty/sealed notice styles.

## 0.3.0-alpha.2

### Physical 2x3 Guild Quest Board
- Rebuilt the Guild Board from a single cube into a true 2-block-wide by 3-block-tall wall-mounted multiblock.
- The board can only be placed flat against a supported vertical wall and checks all six spaces before placement.
- The six pieces form one continuous spruce notice board with a dark-oak outer frame and a thin wall-mounted collision shape.
- Added six physical notice positions spread across the board. Daily Guild contracts now generate four to six notices, so unused positions visibly remain bare.
- Added three parchment styles: handwritten notice, bounty poster and wax-sealed contract, with different sizes and slight rotations to make the board feel naturally pinned rather than like a grid of menu buttons.
- Available contracts are represented by actual paper models on the board. Right-clicking a paper opens that specific contract instead of opening a generic list.
- Empty board positions respond that no contract is pinned there.
- Clicking a contract now opens a compact parchment notice showing the task, Guild Reputation, reward, deadline, failure penalty and acceptance state.
- Accepted/taken notices remain physically posted for other multiplayer players because Guild contracts are personal while the daily board itself is shared.
- The board refreshes its visible papers as the Minecraft day changes and also refreshes immediately when interacted with.
- Breaking any one of the six board pieces removes the whole multiblock and drops one Guild Board item. Removing its supporting wall also safely removes the board.
- The multiblock cannot be pushed by pistons and no longer uses the obsolete single-block loot/model files.
- Added an original pixel-art Guild Board item icon and original parchment/notice textures inspired by the supplied medieval quest-board reference.
- Kept vanilla paper/book UI sounds when reading and accepting notices.

## 0.3.0-alpha.1

### Quest gameplay foundation, class quests and guild contracts
- Added distinct support for authored main/story quests, class-restricted quests and procedural guild contracts while keeping them in the same journal/progression engine.
- Added a DISCOVER objective type so future clues, locations, objects and scripted discoveries can advance quests without pretending everything is a kill/collect task.
- Added optional quest time limits measured in Minecraft days and automatic server-authoritative failure checks.
- Timed quest failures can apply a Guild Reputation penalty. Reputation is stored per player and ranges from -100 upward, giving timed contracts a consequence without deleting gear or griefing a multiplayer character.
- Added class quest rewards via the new class_unlock field. Unlocks are scoped to the player's required base class and are written into CyberClasses' quest-unlock store.
- Fixed CyberQuest's CyberClasses advancement compatibility key to read the actual Advancement field.
- Added vanilla book/page-turn sounds for journal updates and journal navigation, a completion sound, and a distinct failure sound.
- Quest/NPC interaction no longer cancels CyberNpc's own interaction, so quests can progress alongside normal NPC dialogue and services.
- Added procedural Guild Board contracts. Each board publishes four deterministic notices per Minecraft day so all players on a server see the same board for that day.
- Guild notices procedurally combine hunt/supply targets, counts, rewards and optional 1-2 day deadlines, allowing an effectively unbounded stream of repeatable daily work rather than a fixed list of hand-authored filler quests.
- Players may hold up to three active guild contracts at once.
- Daily guild offer history is only retained for the current day instead of permanently filling the completed-quest list, keeping long-running servers bounded.
- Returning to a Guild Board automatically resolves any ready guild contracts before showing the current notices.
- Added the Guild Board block, recipe, loot table, creative tab entry and client screen.
- The Guild Board shows Guild Reputation, task, silver/reputation reward, time limit, failure penalty and whether a notice is available, accepted or already taken.
- Procedural guild jobs stay out of Chronicle so Chronicle remains a readable record of authored adventures and class stories.
- Bumped the CyberQuest network protocol for Guild Board and timed-journal data.

### Data fields
- Quest definitions may now use time_limit_days and failure_guild_reputation.
- Rewards may now use guild_reputation and class_unlock.
- Main quests should use category "main".
- Class quests should use category "class", required_class, and optionally rewards.class_unlock.
- Guild contracts are generated at runtime and do not require individual JSON files.

## 0.2.0-alpha.3

### Minecraft-style open journal redesign
- Rebuilt the journal layout after reviewing Mojang's Bedrock book UI structure: separate book back, spine, page creases and page edges.
- Created original CyberQuest pixel-art book, tab and entry textures based on those layout ideas rather than redistributing Mojang's sample texture files.
- Removed the oversized full-width header and giant tab treatment; the journal is now a compact open book with small leather bookmarks.
- Fixed empty-state text so it is wrapped and centered inside a single page instead of crossing the book spine.
- Fixed quest title, description, notes and lead text widths so they stay inside the right-hand page.
- Removed the clipped bottom-right close hint; the configurable journal key still toggles the journal and Escape still closes it normally.
- Simplified the left page into a clean list of recorded threads and the right page into the selected thread's details.
- Updated the tracked-thread HUD to use the same parchment-entry texture language.
- Kept the journal intentionally medieval-fantasy and less menu-like for multiplayer immersion.

## 0.2.0-alpha.2

### Textured medieval journal pass
- Replaced the flat-color journal panels with real pixel-art wood, parchment, banner and button textures sourced from the MIT-licensed Adventure Production Kit GitHub project.
- Included the upstream MIT copyright/license notice inside the built JAR and documented exactly which GUI assets are reused.
- Reworked the journal into a textured wooden folio with separate parchment pages, a header banner, textured tabs, textured quest rows and a textured Track/Untrack control.
- Updated the tracked-thread HUD to use the same wood/banner texture language so it matches the journal instead of looking like a generic overlay.
- Kept the UI deliberately medieval-fantasy: wood, parchment, ink, muted wax-red accents and gold trim; no modern or sci-fi styling.
- The Adventurer's Journal is opened directly with the configurable CyberQuest keybind, default J; there is no physical quest-book item requirement.
- Pressing the configured journal key again while the journal is open closes it, and the on-screen close hint reflects remapped controls.

## 0.2.0-alpha.1

### Living Threads foundation
- Reworked CyberQuest around a simpler multiplayer-friendly Adventurer's Journal.
- Added three journal views: Journal, Rumours and Chronicle.
- Added a medieval parchment/leather journal UI with a compact two-page layout instead of the old generic objective panel.
- Added one personal tracked thread per player and a small unobtrusive HUD lead display.
- Quest progress remains server-authoritative and stored per player, keeping normal quest progress safe for multiplayer.
- Added stage-based quest definitions. A thread can now reveal one lead at a time instead of exposing every objective at once.
- Added stage titles, narrative notes and current leads for the immersive "What We Know" presentation.
- Existing flat quest JSON remains backwards compatible and is automatically treated as a single stage.
- Added optional journal_section, completion_text, stages, stage lead and stage notes data fields.
- Completed threads move into Chronicle and can show a written resolution.
- Rumour-category entries can live in the Rumours section without requiring a separate complicated quest subsystem.
- Removed the old bundled First Hunt demo quest so the framework is clean before new medieval-fantasy content is authored.
- Bumped the network protocol for the richer journal sync and tracking packet.
