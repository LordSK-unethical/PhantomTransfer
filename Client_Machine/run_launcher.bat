@echo off
echo Compiling Client...
if exist classes rmdir /s /q classes
mkdir classes
javac -d classes -sourcepath . client/*.java client/model/*.java client/view/*.java client/controller/*.java client/network/*.java client/util/*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Starting Client Launcher...
java -cp classes client.ClientLauncher
pause
