# Changelog

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
