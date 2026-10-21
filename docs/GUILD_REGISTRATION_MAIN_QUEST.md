# Main Quest — Your First Steps (0.3.0-alpha.16)

Once a new player has **completed the Pope's introduction** and has
not yet selected a CyberClasses class, a Main Quest appears in the
existing Adventurer's Journal and tracked quest HUD:

**Your First Steps**
> Speak with the Adventurers' Guild Receptionist and choose a class.
> Accepting the one-time starter equipment is optional.

The quest automatically marks itself complete on successful class
registration and moves into the Chronicle. It grants no duplicate gear
and does not alter class saves or other quests.

- Existing already-classed players are not retroactively given a task.
- The quest tracks itself by default when no other quest is tracked;
  players can track/untrack it in the Journal.
- This is an integrated story entry derived from the actual completion
  state, not a physical restriction or an external quest reward.
- Compatible with previously persisted CyberQuest journal and guild data.
- This requires CyberNpc and CyberClasses for the full intro/registration
  path; no compile-time dependencies were introduced.
