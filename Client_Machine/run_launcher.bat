@echo off
echo Compiling Client...
javac client/*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Starting Client Launcher...
java client.ClientLauncher
pause
