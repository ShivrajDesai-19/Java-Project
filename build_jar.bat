@echo off
title Build Executable JAR - Smart Pet Care System
echo ==========================================================
echo    BUILDING STANDALONE EXECUTABLE JAR (DESKTOP PACKAGE)
echo ==========================================================
echo.

if not exist "bin\com\petcare\Main.class" (
    echo [1/3] Compiling source code first...
    call compile.bat
)

set "JAR_CMD=jar"
where jar >nul 2>&1
if %ERRORLEVEL% neq 0 (
    if defined JAVA_HOME (
        if exist "%JAVA_HOME%\bin\jar.exe" set "JAR_CMD=%JAVA_HOME%\bin\jar.exe"
    )
    for /d %%d in ("C:\Program Files\Java\jdk*") do (
        if exist "%%d\bin\jar.exe" set "JAR_CMD=%%d\bin\jar.exe"
    )
)

echo [2/3] Extracting JDBC driver and preparing classes...
if exist "build_jar_temp" rd /s /q "build_jar_temp"
mkdir "build_jar_temp\classes"
xcopy /s /e /q "bin\*" "build_jar_temp\classes\" > nul

cd "build_jar_temp\classes"
"%JAR_CMD%" -xf "..\..\lib\mysql-connector-j.jar"
if exist "META-INF\*.SF" del /q "META-INF\*.SF"
if exist "META-INF\*.DSA" del /q "META-INF\*.DSA"
if exist "META-INF\*.RSA" del /q "META-INF\*.RSA"

echo [3/3] Generating SmartPetCareSystem.jar...
"%JAR_CMD%" -cfe "..\..\SmartPetCareSystem.jar" com.petcare.Main *
cd ..\..
rd /s /q "build_jar_temp"

if exist "SmartPetCareSystem.jar" (
    echo.
    echo ==========================================================
    echo [SUCCESS] Standalone Executable JAR created successfully!
    echo File: SmartPetCareSystem.jar
    echo You can now double-click SmartPetCareSystem.jar to run!
    echo ==========================================================
) else (
    echo [ERROR] JAR creation failed. Make sure JDK is installed and in PATH.
)
pause
