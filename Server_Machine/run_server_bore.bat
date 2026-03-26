@echo off
title Phantom Transfer Server - Bore Tunnel
color 0A
echo ========================================
echo   Phantom Transfer Server + Bore Tunnel
echo ========================================
echo.

cd /d "%~dp0"

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed!
    pause
    exit /b 1
)

REM Check if bore is installed
where bore >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Bore is not installed!
    echo.
    echo To install bore, download from:
    echo https://github.com/ekzhang/bore/releases
    echo.
    echo Or run:
    echo   cargo install bore-cli
    pause
    exit /b 1
)

REM Start server in background
echo Starting Java server on port 5000...
start "Server" cmd /k "java -cp . server.ServerMain"

timeout /t 2 >nul

REM Start bore tunnel
echo Starting bore tunnel...
echo.
echo Waiting for tunnel...
echo.
bore local 5000 --to bore.pub

pause
