@echo off
echo ==========================================================
echo Compiling Smart Pet Care and Veterinary Management System...
echo ==========================================================
if not exist "bin" mkdir "bin"
javac -cp "lib\mysql-connector-j.jar" -d bin src\com\petcare\*.java src\com\petcare\dao\*.java src\com\petcare\exception\*.java src\com\petcare\model\*.java src\com\petcare\service\*.java src\com\petcare\thread\*.java src\com\petcare\ui\*.java src\com\petcare\ui\dialogs\*.java src\com\petcare\ui\panels\*.java src\com\petcare\util\*.java
if %ERRORLEVEL% equ 0 (
    echo.
    echo [SUCCESS] Compilation Successful! Classes generated in bin/
) else (
    echo.
    echo [ERROR] Compilation failed with error code %ERRORLEVEL%
)
pause
