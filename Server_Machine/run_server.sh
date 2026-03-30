#!/bin/bash
echo "Compiling Server..."
javac server/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    read -p "Press Enter to exit..."
    exit 1
fi
echo "Starting Server on port 5000..."
java server.ServerMain 5000
