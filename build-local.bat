@echo off
setlocal EnableExtensions
cd /d "%~dp0"
echo === CyberQuest local build (no GitHub Actions) ===
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0tools\build-local.ps1" -GradleVersion "8.8" -ModName "CyberQuest" -JarPattern "CyberQuest-*.jar"
set "RESULT=%ERRORLEVEL%"
if not "%RESULT%"=="0" (
    echo.
    echo Build failed. Please share build-local.log if you need help.
) else (
    echo.
    echo The file CyberQuest.jar is ready for testing.
)
pause
exit /b %RESULT%
