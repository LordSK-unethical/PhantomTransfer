#!/bin/bash
# Tailscale Setup Script for Phantom Transfer Server

echo "=== Tailscale Setup for Phantom Transfer ==="
echo ""
echo "Prerequisites:"
echo "1. Create a Tailscale account at https://login.tailscale.com"
echo "2. Get your auth key from: Settings -> Keys -> Generate auth key"
echo ""
echo "Setup options:"
echo "1. Run on host machine with Tailscale installed"
echo "2. Run inside Docker container"
echo ""

read -p "Enter your Tailscale auth key (tskey-auth-...): " AUTH_KEY

if [ -z "$AUTH_KEY" ]; then
    echo "Error: Auth key is required"
    exit 1
fi

export TS_AUTH_KEY="$AUTH_KEY"

echo ""
echo "Starting Tailscale..."
tailscale up --authkey="$TS_AUTH_KEY" --advertise-exit-node --accept-routes

echo ""
echo "Starting the server and exposing via Tailscale..."
tailscale serve --bg https+insecure://localhost:5000

echo ""
echo "Your server is now accessible via your Tailscale device IP on port 443"
echo "Tailscale hostname: $(tailscale status --self | grep -oP '(?<=Self ).*?(?=,)')"
echo ""
