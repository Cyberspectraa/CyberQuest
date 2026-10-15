package com.cyberspectraa.cyberquest.client;

import net.minecraftforge.fml.ModList;

import java.util.List;

/**
 * Lightweight written field guide for the Adventurer's Journal.
 * No compile-time link to CyberRaces: CyberQuest can still run alone.
 * The current fifteen starting races and the acquired Undead condition.
 * Checked against CyberRaces abilities and attributes.
 */
public final class RaceJournalData {
    private RaceJournalData() {}

    public record Entry(String name, String status, String identity,
                        String traits, String active, String caution) {}

    private static final List<Entry> ENTRIES = List.of(
        new Entry("Human",
            "Starting race",
            "No special gifts; hard work pays off.",
            "Normal health, size and speed. Earns 5% more XP.",
            "No V ability.",
            "No racial combat bonus."),
        new Entry("Elf",
            "Starting race",
            "Light-footed archers with a knack for magic.",
            "18 health, faster movement and higher mana.",
            "No V ability. Bows charge 15% faster.",
            "Less health than a Human."),
        new Entry("Dwarf",
            "Starting race",
            "Hardy miners, more at home below ground.",
            "22 health and faster mining underground. Eligible ores and resources can drop extra items.",
            "No V ability. Mining is the main perk.",
            "Slower on foot and lower mana."),
        new Entry("Orc",
            "Starting race",
            "Built to take hits and keep swinging.",
            "24 health. Deals 20% more damage at 35% health or less.",
            "V: War Cry grants Strength and knockback resistance for 8 seconds.",
            "Lower mana and mana recovery."),
        new Entry("Goblin",
            "Starting race",
            "Small, quick and always looking for valuables.",
            "18 health, bonus movement speed and +0.5 Luck.",
            "V: Treasure Instinct marks nearby ores and filled containers for 8 seconds.",
            "Less health than a Human."),
        new Entry("Tiefling",
            "Starting race",
            "Horns and a tail show their infernal blood.",
            "Higher mana and half damage from fire.",
            "V: Infernal Surge boosts melee damage by 25% and sets targets alight for 8 seconds.",
            "Normal health and armour; no natural physical protection."),
        new Entry("Dragonborn",
            "Starting race",
            "Scales, horns and a tail come with a sturdy build.",
            "22 health, natural armour and 15% less fire damage.",
            "V: Fire Breath from Iron's Spells, with no mana cost. Requires that mod.",
            "Slightly slower than a Human."),
        new Entry("Fairy",
            "Starting race",
            "Tiny winged folk, quick but easy to hurt.",
            "14 health, higher speed and strong mana. Natural wings use Icarus when installed.",
            "V: Toggle Hover, around 3-4 blocks above the ground.",
            "Very low health."),
        new Entry("Nymph",
            "Starting race",
            "Nature-bound folk who recover in the rain.",
            "18 health, higher mana and slow healing while wet.",
            "V: Nature's Grace grants Regeneration and Speed for 8 seconds.",
            "Lower health than a Human."),
        new Entry("Catfolk",
            "Starting race",
            "Cat ears, a tail and a talent for landing on their feet.",
            "Move faster, jump 12% higher and take 60% less fall damage.",
            "No V ability.",
            "No extra health or natural armour."),
        new Entry("Dogfolk",
            "Starting race",
            "Wolf-eared trackers with a keen nose.",
            "22 health. Sprinting uses 35% less exhaustion.",
            "V: Hunter's Nose outlines up to 16 nearby creatures for 7 seconds.",
            "Slightly lower mana."),
        new Entry("Foxfolk",
            "Starting race",
            "Fox-eared folk who favour quick escapes.",
            "18 health. Moves faster, including while sneaking.",
            "V: Quickstep, a forward dash with a 6-second cooldown.",
            "Lower health than a Human."),
        new Entry("Birdfolk",
            "Starting race",
            "Feathered wings make travel a little easier.",
            "18 health. Takes 65% less fall damage; wings use Icarus when installed.",
            "V: Wing Burst pushes forward and upward; 7-second cooldown.",
            "Lower health than a Human."),
        new Entry("Slime",
            "Starting race",
            "A springy body of living gel.",
            "No fall damage, higher jumps and bouncy landings. Slime droplets follow movement.",
            "No V ability. Sneak while landing to stop a bounce.",
            "Takes 30% more fire damage."),
        new Entry("Arachnid",
            "Starting race",
            "Spider-like climbers with a useful supply of web.",
            "Climb walls. Hold Sneak against a wall to stay put.",
            "V: Web Shot slows targets and leaves a cobweb for 8 seconds.",
            "Cannot fly; otherwise takes normal damage."),
        new Entry("Undead",
            "Acquired condition",
            "Zombification changes your race until you are cured.",
            "22 health, immune to Poison and Hunger effects. Undead mobs leave you alone.",
            "V: Grave Resilience grants resistance and restores health. Cure with Weakness and a Golden Apple.",
            "Burns in daylight without a helmet or cover; takes 20% more fire damage.")
    );

    public static List<Entry> entries() {
        return ModList.get().isLoaded("cyberraces") ? ENTRIES : List.of();
    }
}
