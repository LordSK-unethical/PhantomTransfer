@echo off
echo Compiling Server...
javac server/*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)
echo Starting Server on port 5000...
java server.ServerMain 5000
pause
