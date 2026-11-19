# Build CyberQuest locally (no GitHub Actions)

Use branch `development-no-actions`, **Java JDK 17**, and **Python 3 with Pillow**
(the CyberQuest Gradle build already uses Python to generate its existing
quest item textures). Gradle 8.8 is downloaded by the included Windows
PowerShell script from the official distribution with SHA-256 verification.

```powershell
cd "$HOME\CyberMods\CyberQuest"
git pull --ff-only
.\build-local.bat
```

On success, a `CyberQuest.jar` is produced in the repository folder;
a versioned JAR remains in `build/libs`. If the build fails,
check `build-local.log`. The original stable `main` branch and
GitHub Actions are not touched.

CyberQuest 0.3.0-alpha.21 adds Main Seven secret quest definitions
corresponding to CyberNpc 0.44.91. Both updated JARs are required to
test the linked dialogue and quest progression.
