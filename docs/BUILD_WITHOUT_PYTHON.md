# CyberQuest 0.3.0-alpha.23 — Python-free local build

## What failed

The old Gradle build unconditionally tried the Windows Python launcher
`py` when packaging generated quest textures. The user's build log
confirms Java 17.0.21 is installed, but the `py` launcher was missing.
No Java compilation error was demonstrated by that log.

Those textures are 14 approved vanilla-derived pixel-art PNGs, created
by `tools/generate_vanilla_textures.py`. Python and Pillow were only needed
to author that artwork, not to run Minecraft. Normal builds should not
need to regenerate unchanged art.

## Option A: original texture archive, no Python installation

CyberQuest's updated Gradle build accepts the optional local file:

    tools/prebuilt-quest-textures.zip

The ZIP starts at `assets/cyberquest/textures/` and includes all 14
source-equivalent texture files. Its SHA-256 is:

    79b1477618c8ff28dcccd4da2182d90971a141f4a05327d133cd33fc617f375d

Every pixel of the indexed PNG versions was compared against the original
generator's RGBA output and matched exactly. The source artwork, textures,
colours and layout are preserved; the ZIP is smaller only because its
PNG files have a more efficient lossless representation.

Download the provided `cyberquest_prebuilt_textures.zip` file from the
conversation and save it into your Windows Downloads folder, then run:

    cd "$HOME\CyberMods\CyberQuest"
    git switch development-no-actions
    git pull --ff-only
    Copy-Item "$HOME\Downloads\cyberquest_prebuilt_textures.zip" ".\tools\prebuilt-quest-textures.zip"
    .\build-local.bat

The ZIP is **only a build input**. Do not put it in the Minecraft mods
or resourcepacks folder. The build includes its files directly inside
the resulting CyberQuest.jar. It remains in the tools folder for later
local builds. This attached ZIP was **not committed to GitHub**; it needs
to be copied locally to make the Python-free path available.

## Option B: continue authoring with Python/Pillow

Without the ZIP, Gradle retains the original texture generator. The
updated task tries PYTHON (environment override), Windows py -3,
Windows python, or python3/python on other systems. It checks that Pillow
imports successfully before running the generator. If no working Python
and Pillow installation is found, it now explains how to install Python
and Pillow or use the ZIP instead of failing with a missing executable.

The standalone Gradle task `generateVanillaQuestTextures` remains
available for people intentionally regenerating the texture artwork.

## Safety and verification

This changes the build process, not the mod's IDs, journal content,
quest logic, network packets or save data. All normal assets should
appear in the compiled JAR under `assets/cyberquest/textures/`. Test
the journal, tabs, quest HUD and guild board after a successful local
build. A failed build does not produce a newly tested JAR.

Only the `development-no-actions` branch is changed. Do not run GitHub
Actions and do not merge into main as part of this fix.
