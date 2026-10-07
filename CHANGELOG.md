# Changelog

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
