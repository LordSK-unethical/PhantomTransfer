@echo off
echo Compiling Client...
javac client/*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)
REM Set the Server IP here. Change 127.0.0.1 to the actual Server IP address.
set ip=10.29.209.230


echo Starting Client connecting to %ip%:5000...
java client.ClientMain %ip% 5000
echo Client exited.
pause
