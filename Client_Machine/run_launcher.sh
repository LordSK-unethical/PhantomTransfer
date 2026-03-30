#!/bin/bash
echo "Compiling Client..."
javac client/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    read -p "Press Enter to exit..."
    exit 1
fi

echo "Starting Client Launcher..."
java client.ClientLauncher
