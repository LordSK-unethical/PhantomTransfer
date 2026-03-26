@echo off
title Phantom Transfer Server - LocalTunnel
color 0A
echo ========================================
echo   Phantom Transfer Server + LocalTunnel
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

REM Check if Node.js is installed
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Node.js is not installed!
    pause
    exit /b 1
)

REM Check if localtunnel is installed
where lt >nul 2>&1
if %errorlevel% neq 0 (
    echo Installing localtunnel...
    npm install -g localtunnel
)

REM Start server in background
echo Starting Java server on port 5000...
start "Server" cmd /k "java -cp . server.ServerMain"

timeout /t 2 >nul

REM Start localtunnel
echo Starting localtunnel...
echo.
echo Waiting for tunnel URL...
echo.
lt --port 5000 --subdomain phantom-transfer

pause
