#!/bin/bash

# Start the Java server in background
java -cp . server.ServerMain &
SERVER_PID=$!

echo "Server started, connecting to localtunnel..."

# Start localtunnel and capture the URL
lt --port 5000 --subdomain phantom-transfer 2>&1 | while read line; do
    echo "$line"
    if echo "$line" | grep -q "url:"; then
        URL=$(echo "$line" | grep -o 'https://[^ ]*')
        echo ""
        echo "=========================================="
        echo "PUBLIC URL: $URL"
        echo "=========================================="
        echo "Share this URL with clients (without https://)"
    fi
done &

LT_PID=$!

# Cleanup on exit
trap "kill $SERVER_PID $LT_PID 2>/dev/null" EXIT

wait
