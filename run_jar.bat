@echo off
title Smart Pet Care System - JAR Launcher
echo ==========================================================
echo       Launching SmartPetCareSystem.jar (Desktop Executable)
echo ==========================================================
echo.
if not exist "SmartPetCareSystem.jar" (
    echo [ERROR] SmartPetCareSystem.jar not found!
    echo Please run build_jar.bat first to generate the JAR file.
    pause
    exit /b 1
)

echo Starting application...
java -jar SmartPetCareSystem.jar
if %ERRORLEVEL% neq 0 (
    echo Application exited with error code %ERRORLEVEL%
    pause
)
