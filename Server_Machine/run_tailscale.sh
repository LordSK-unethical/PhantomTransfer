#!/bin/bash
echo "=== Starting Phantom Transfer with Tailscale ==="

if ! command -v tailscale &> /dev/null; then
    echo "Error: Tailscale is not installed"
    echo "Install from: https://tailscale.com/download"
    exit 1
fi

export TS_AUTH_KEY=${TS_AUTH_KEY:-}

if [ -z "$TS_AUTH_KEY" ]; then
    echo "Error: TS_AUTH_KEY environment variable not set"
    echo "Set it with: export TS_AUTH_KEY=tskey-auth-..."
    exit 1
fi

echo "Starting Tailscale..."
tailscale up --authkey="$TS_AUTH_KEY" --accept-routes 2>/dev/null || true

TAILSCALE_IP=$(tailscale ip -4 2>/dev/null)

echo ""
echo "Starting server on port 5000..."
nohup java server.ServerMain 5000 > server.log 2>&1 &

sleep 2

echo ""
echo "=== Setup Complete ==="
echo "Server running on Tailscale IP: $TAILSCALE_IP:5000"
echo ""
echo "To connect clients:"
echo "1. Install Tailscale on client machine"
echo "2. Connect to your tailnet"
echo "3. Enter server Tailscale IP: $TAILSCALE_IP"
echo "4. Port: 5000"
echo ""
echo "Or use MagicDNS: Connect to: <your-hostname>.ts.net on port 5000"
echo ""
echo "To stop: pkill -f ServerMain && tailscale down"
