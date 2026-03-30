#!/bin/bash
echo "========================================"
echo "  Phantom Transfer Server + Bore Tunnel"
echo "========================================"
echo ""

cd "$(dirname "$0")"

if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed!"
    read -p "Press Enter to exit..."
    exit 1
fi

if ! command -v bore &> /dev/null; then
    echo "ERROR: Bore is not installed!"
    echo ""
    echo "To install bore, download from:"
    echo "https://github.com/ekzhang/bore/releases"
    echo ""
    echo "Or run:"
    echo "  cargo install bore-cli"
    read -p "Press Enter to exit..."
    exit 1
fi

echo "Starting Java server on port 5000..."
java -cp . server.ServerMain &
SERVER_PID=$!

sleep 2

echo "Starting bore tunnel..."
echo ""
echo "Waiting for tunnel..."
echo ""
bore local 5000 --to bore.pub

wait $SERVER_PID
