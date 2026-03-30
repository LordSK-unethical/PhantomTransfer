#!/bin/bash
echo "========================================"
echo "  Phantom Transfer Server + LocalTunnel"
echo "========================================"
echo ""

cd "$(dirname "$0")"

if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed!"
    read -p "Press Enter to exit..."
    exit 1
fi

if ! command -v node &> /dev/null; then
    echo "ERROR: Node.js is not installed!"
    read -p "Press Enter to exit..."
    exit 1
fi

if ! command -v lt &> /dev/null; then
    echo "Installing localtunnel..."
    npm install -g localtunnel
fi

echo "Starting Java server on port 5000..."
java -cp . server.ServerMain &
SERVER_PID=$!

sleep 2

echo "Starting localtunnel..."
echo ""
echo "Waiting for tunnel URL..."
echo ""
lt --port 5000 --subdomain phantom-transfer

wait $SERVER_PID
