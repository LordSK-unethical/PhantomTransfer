@echo off
title Transfer Project - Main Menu
color 0A
setlocal enabledelayedexpansion

:menu
cls
echo ========================================
echo     Transfer Project - Main Menu
echo ========================================
echo.
echo  1. Run Server (Local)
echo  2. Run Server (Docker)
echo  3. Stop Docker Server
echo  4. Run Client Launcher
echo  5. Compile All
echo  6. Clean Build
echo  0. Exit
echo.
echo ========================================
echo.

set /p choice="Enter your choice: "

if "%choice%"=="1" goto run_server
if "%choice%"=="2" goto run_docker
if "%choice%"=="3" goto stop_docker
if "%choice%"=="4" goto run_launcher
if "%choice%"=="5" goto compile_all
if "%choice%"=="6" goto clean
if "%choice%"=="0" goto cleanup_exit

echo Invalid choice. Press any key to continue...
pause >nul
goto menu

:run_server
echo Starting Server (Local)...
cd /d "%~dp0Server_Machine"
start "Server" cmd /k "call run_server.bat"
echo Server started in a new window.
timeout /t 2 >nul
goto menu

:run_docker
echo Starting Server (Docker)...
cd /d "%~dp0Server_Machine"
start "Docker Server" cmd /k "docker compose up -d --build && docker compose logs -f"
echo Docker server started.
timeout /t 2 >nul
goto menu

:stop_docker
echo Stopping Docker Server...
cd /d "%~dp0Server_Machine"
docker compose down
echo Docker server stopped.
pause
goto menu

:run_launcher
echo Starting Client Launcher...
cd /d "%~dp0Client_Machine"
start "Client Launcher" cmd /k "call run_launcher.bat"
echo Client Launcher started in a new window.
timeout /t 2 >nul
goto menu

:compile_all
echo Compiling Server...
cd /d "%~dp0Server_Machine"
javac server/*.java
if %errorlevel% neq 0 (
    echo Server compilation failed!
    pause
    goto menu
)
echo Server compiled successfully.
echo.
echo Compiling Client...
cd /d "%~dp0Client_Machine"
javac client/*.java
if %errorlevel% neq 0 (
    echo Client compilation failed!
    pause
    goto menu
)
echo Client compiled successfully.
echo.
echo All compilation completed.
pause
goto menu

:clean
echo Cleaning build files...
cd /d "%~dp0Server_Machine"
if exist *.class del /q *.class
if exist server\*.class del /q server\*.class
echo Server cleaned.

cd /d "%~dp0Client_Machine"
if exist *.class del /q *.class
if exist client\*.class del /q client\*.class
echo Client cleaned.
echo.
echo All cleaned.
pause
goto menu

:cleanup_exit
echo Closing all opened windows...
taskkill /FI "WINDOWTITLE eq Server*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Client*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Client Launcher*" /F >nul 2>&1
exit
