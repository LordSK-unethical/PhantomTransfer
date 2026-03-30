@echo off
echo Building Docker image with Tailscale...

docker build -t phantom-transfer-server-tailscale:latest .

if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b %errorlevel%
)

echo.
echo Build successful!
echo.
echo To start the server with Tailscale:
echo 1. Copy .env.example to .env
echo 2. Add your Tailscale auth key to .env
echo 3. Run: docker-compose up -d
echo.
echo Get your auth key at: https://login.tailscale.com/settings/keys
echo.
pause
