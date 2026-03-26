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
echo  1. Run Server (must run first)
echo  2. Run Client Launcher
echo  3. Run Client (connect mode)
echo  4. Run All (Server + Client)
echo  5. Compile All
echo  6. Clean Build
echo  0. Exit
echo.
echo ========================================
echo.

set /p choice="Enter your choice: "

if "%choice%"=="1" goto run_server
if "%choice%"=="2" goto run_launcher
if "%choice%"=="3" goto run_client
if "%choice%"=="4" goto run_all
if "%choice%"=="5" goto compile_all
if "%choice%"=="6" goto clean
if "%choice%"=="0" exit

echo Invalid choice. Press any key to continue...
pause >nul
goto menu

:run_server
echo Starting Server...
cd /d "%~dp0Server_Machine"
start "Server" cmd /k "call run_server.bat"
echo Server started in a new window.
timeout /t 2 >nul
goto menu

:run_launcher
echo Starting Client Launcher...
cd /d "%~dp0Client_Machine"
start "Client Launcher" cmd /k "call run_launcher.bat"
echo Client Launcher started in a new window.
timeout /t 2 >nul
goto menu

:run_client
echo Starting Client in connect mode...
cd /d "%~dp0Client_Machine"
start "Client" cmd /k "call run_client.bat"
echo Client started in a new window.
timeout /t 2 >nul
goto menu

:run_all
echo Starting both Server and Client...
cd /d "%~dp0Server_Machine"
start "Server" cmd /k "call run_server.bat"
timeout /t 2 >nul
cd /d "%~dp0Client_Machine"
start "Client" cmd /k "call run_client.bat"
echo All components started.
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
