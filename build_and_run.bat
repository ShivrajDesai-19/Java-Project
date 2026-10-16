@echo off
title Smart Pet Care and Veterinary Management System
echo ==========================================================
echo    SMART PET CARE AND VETERINARY MANAGEMENT SYSTEM
echo       BCA Sem-3 Java Programming Major Project
echo ==========================================================
echo.
if not exist "bin\com\petcare\Main.class" (
    echo [1/2] Compiling source code...
    if not exist "bin" mkdir "bin"
    javac -cp "lib\mysql-connector-j.jar" -d bin src\com\petcare\*.java src\com\petcare\dao\*.java src\com\petcare\exception\*.java src\com\petcare\model\*.java src\com\petcare\service\*.java src\com\petcare\thread\*.java src\com\petcare\ui\*.java src\com\petcare\ui\dialogs\*.java src\com\petcare\ui\panels\*.java src\com\petcare\util\*.java
    if %ERRORLEVEL% neq 0 (
        echo [ERROR] Compilation failed.
        pause
        exit /b %ERRORLEVEL%
    )
)

echo [2/2] Launching Desktop Application...
echo Make sure MySQL is started in XAMPP Control Panel.
echo Default Database: smart_pet_care_db (auto-created on connect)
echo.
java -cp "bin;lib\mysql-connector-j.jar" com.petcare.Main --port 3306 --db smart_pet_care_db
if %ERRORLEVEL% neq 0 (
    echo Application exited with code %ERRORLEVEL%
    pause
)
