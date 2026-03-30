#!/bin/bash
echo "Compiling Client..."
rm -rf classes
mkdir classes
javac -d classes -sourcepath . client/*.java client/model/*.java client/view/*.java client/controller/*.java client/network/*.java client/util/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    read -p "Press Enter to exit..."
    exit 1
fi

echo "Starting Client Launcher..."
java -cp classes client.ClientLauncher
