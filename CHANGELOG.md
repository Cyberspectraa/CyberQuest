# Changelog

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
