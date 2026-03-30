#!/bin/bash
echo "Building Docker image with Tailscale..."

docker build -t phantom-transfer-server-tailscale:latest .

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo ""
echo "Build successful!"
echo ""
echo "To start the server with Tailscale:"
echo "1. cp .env.example .env"
echo "2. Add your Tailscale auth key to .env"
echo "3. docker-compose up -d"
echo ""
echo "Get your auth key at: https://login.tailscale.com/settings/keys"
