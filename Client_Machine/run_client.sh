#!/bin/bash
echo "Compiling Client..."
javac client/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    read -p "Press Enter to exit..."
    exit 1
fi

ip="127.0.0.1"

echo "Starting Client connecting to $ip:5000..."
java client.ClientMain $ip 5000
echo "Client exited."
