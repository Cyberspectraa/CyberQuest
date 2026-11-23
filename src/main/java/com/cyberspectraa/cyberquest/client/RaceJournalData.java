package com.cyberspectraa.cyberquest.client;

import net.minecraftforge.fml.ModList;

import java.util.List;

/**
 * Adventurer's Journal guide aligned with CyberRaces' actual player-creation
 * choices (Race.selectableAtCreation / wildNpcChoices as of 0.3.2-alpha.75).
 *
 * Ten starting races, two additional Wild NPC races, and the acquired Undead
 * condition. Retired or unavailable races must not be advertised as choices.
 * There is deliberately no compile-time CyberRaces dependency: the journal
 * still works normally if that optional mod is missing.
 */
public final class RaceJournalData {
    private RaceJournalData() {}

    public record Entry(String name, String status, String identity,
                        String traits, String active, String caution) {}

    private static final List<Entry> ENTRIES = List.of(
        new Entry("Human",
            "Starting race",
            "Ordinary folk who get more out of every lesson.",
            "Standard health, size and speed. Earns 5% more XP.",
            "No V ability.",
            "No built-in combat advantage."),
        new Entry("Elf",
            "Starting race",
            "Agile archers with a natural talent for magic.",
            "18 health, quicker movement and increased mana.",
            "No V ability. Bows charge 15% faster.",
            "Less health than a Human."),
        new Entry("Orc",
            "Starting race",
            "Powerful fighters who become dangerous when injured.",
            "24 health. Deals 20% more damage at 35% health or less.",
            "V: War Cry grants Strength and knockback resistance for 8 seconds.",
            "Lower mana and slower mana recovery."),
        new Entry("Tiefling",
            "Starting race",
            "Horns, a tail and an affinity for infernal magic.",
            "Higher mana and takes half damage from fire.",
            "V: Infernal Surge improves melee attacks and burns targets for 8 seconds.",
            "No additional natural armour."),
        new Entry("Nymph",
            "Starting race",
            "Nature-bound people whose bodies recover in the rain.",
            "18 health, increased mana and slow healing while wet.",
            "V: Nature's Grace grants Regeneration and Speed for 8 seconds.",
            "Lower health than a Human."),
        new Entry("Catfolk",
            "Starting race",
            "Cat-eared travellers who land lightly on their feet.",
            "Quicker movement, 12% higher jumps and 60% less fall damage.",
            "No V ability.",
            "No extra health or natural armour."),
        new Entry("Dogfolk",
            "Starting race",
            "Sharp-nosed trackers who can keep running for longer.",
            "22 health. Sprinting uses 35% less exhaustion.",
            "V: Hunter's Nose outlines nearby creatures for 7 seconds.",
            "Slightly lower mana."),
        new Entry("Foxfolk",
            "Starting race",
            "Clever, quick-moving folk who favour escape over a fair fight.",
            "18 health and faster movement, including while sneaking.",
            "V: Quickstep dashes forward with a 6-second cooldown.",
            "Lower health than a Human."),
        new Entry("Slime",
            "Starting race",
            "Living gel with elastic limbs and a habit of bouncing.",
            "No fall damage, higher jumps and bouncy landings.",
            "No V ability. Sneak when landing to stop the bounce.",
            "Takes 30% more fire damage."),
        new Entry("Arachnid",
            "Starting race",
            "Wall-climbing people with a talent for weaving webbing.",
            "Climb walls. Hold Sneak against a wall to stay in place.",
            "V: Web Shot slows targets and leaves a temporary cobweb.",
            "No natural flight."),
        new Entry("Goblin",
            "Wild NPC only",
            "Small, nimble scavengers still found among the wild inhabitants.",
            "18 health and naturally quick movement.",
            "Not a player starting race. No player V ability.",
            "Retained for Wild NPCs; not available in character creation."),
        new Entry("Dwarf",
            "Wild NPC only",
            "Sturdy underground folk who still inhabit the wider world.",
            "22 health and a solid, slower-moving build.",
            "Not a player starting race. No player V ability.",
            "Retained for Wild NPCs; not available in character creation."),
        new Entry("Undead",
            "Acquired condition",
            "An infection can temporarily change an existing character.",
            "22 health; immune to Poison and Hunger. Undead mobs ignore you.",
            "V: Grave Resilience heals and grants resistance. Weakness and a Golden Apple can cure it.",
            "Burns in daylight without cover; takes 20% more fire damage.")
    );

    public static List<Entry> entries() {
        return ModList.get().isLoaded("cyberraces") ? ENTRIES : List.of();
    }
}
