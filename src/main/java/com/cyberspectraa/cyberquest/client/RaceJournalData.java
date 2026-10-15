package com.cyberspectraa.cyberquest.client;

import net.minecraftforge.fml.ModList;

import java.util.List;

/**
 * Lightweight written field guide for the Adventurer's Journal.
 * No compile-time link to CyberRaces: CyberQuest can still run alone.
 * These 15 starting races and the acquired Undead reflect the
 * CyberRaces 0.3.2-alpha.53 base-race beta.
 */
public final class RaceJournalData {
    private RaceJournalData() {}

    public record Entry(String name, String status, String identity,
                        String traits, String active, String caution) {}

    private static final List<Entry> ENTRIES = List.of(
        new Entry("Human", "Starting race",
            "Versatile settlers who can adapt to any style of play.",
            "Balanced size and attributes. Gain 5% extra experience.",
            "No active racial ability.",
            "No specialised racial advantage."),
        new Entry("Elf", "Starting race",
            "Swift, magically attuned archers of the forests.",
            "Draw bows about 15% faster; natural magical aptitude.",
            "No active racial ability.",
            "Lower maximum health than a Human."),
        new Entry("Dwarf", "Starting race",
            "Sturdy mountain miners who prize precious resources.",
            "Mine 15% faster underground. Innate Fortune II-style bonus when mining ores and other eligible resources.",
            "No V ability. Resource mining is their special talent.",
            "Slightly slower movement and lower magic potential."),
        new Entry("Orc", "Starting race",
            "Green-skinned warriors who thrive in brutal fights.",
            "Extra health; gain 20% attack damage below 35% health.",
            "V: War Cry.",
            "Reduced natural mana and magical regeneration."),
        new Entry("Goblin", "Starting race",
            "Small, fast scavengers and opportunists.",
            "Quick movement and a little bonus luck.",
            "V: Treasure Instinct: highlights nearby ore and stocked containers for 8 seconds.",
            "More fragile than Humans."),
        new Entry("Tiefling", "Starting race",
            "Horned, tailed people touched by infernal magic.",
            "Strong magic and increased resistance to fire.",
            "V: Infernal Surge: melee attacks strike harder and ignite foes for 8 seconds.",
            "Less suited to physical defence without Infernal Surge."),
        new Entry("Dragonborn", "Starting race",
            "Scaleblooded fighters with matching body, horns and tail.",
            "Strong armour, health and tough scales.",
            "V: Dragon Breath (Iron's Spells required).",
            "A little slower and especially vulnerable to enemy positioning."),
        new Entry("Fairy", "Starting race",
            "Very small, nimble creatures with natural wings.",
            "Excellent mobility, magical aptitude and small size.",
            "V: toggle Hover. Small sparkles appear while hovering.",
            "Extremely fragile."),
        new Entry("Nymph", "Starting race",
            "Green-toned nature spirits that flourish around water.",
            "Strong mana; slowly recover health while wet or in rain.",
            "V: Nature's Grace.",
            "Less resilient in direct combat."),
        new Entry("Catfolk", "Starting race",
            "Agile hunters with Figura-style ears and a four-segment cat tail.",
            "Jump higher and take much less fall damage.",
            "No active racial ability.",
            "Light build; less resilient than tough races."),
        new Entry("Dogfolk", "Starting race",
            "Sturdy canine adventurers, keen of nose.",
            "Less sprint exhaustion; a strong survivalist build.",
            "V: Hunter's Nose: briefly highlights nearby creatures through walls.",
            "Slightly lower natural mana."),
        new Entry("Foxfolk", "Starting race",
            "Quick, clever fox-eared adventurers.",
            "Better crouch mobility and fast movement.",
            "V: Quickstep dash.",
            "Lower health than average."),
        new Entry("Birdfolk", "Starting race",
            "Winged travellers built for aerial movement.",
            "Icarus wings when available; greatly reduced fall damage.",
            "V: Wing Burst.",
            "Lower health than average."),
        new Entry("Slime", "Starting race",
            "Gelatinous folk who bounce rather than break.",
            "No fall damage; springy jumps and landings; slime droplets instead of ordinary sprint dust.",
            "No active ability. Bouncing is natural.",
            "Slight vulnerability to fire."),
        new Entry("Arachnid", "Starting race",
            "Spider-blooded hunters who scale sheer surfaces.",
            "Climb walls automatically by touching them; sneak to stop rising.",
            "V: Web Shot fires a flying cobweb that slows a target.",
            "No automatic flight."),
        new Entry("Undead", "Acquired condition",
            "A curse gained through Zombification, not a selectable birth race.",
            "Poison/Hunger resistance and undead monsters ignore you.",
            "V: Grave Resilience. Cure with Weakness and a regular Golden Apple.",
            "Burns in daylight without protection; increased fire damage.")
    );

    public static List<Entry> entries() {
        return ModList.get().isLoaded("cyberraces") ? ENTRIES : List.of();
    }
}
