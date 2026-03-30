#!/bin/bash

show_menu() {
    echo "========================================"
    echo "    Transfer Project - Main Menu"
    echo "========================================"
    echo ""
    echo " 1. Run Server (Local)"
    echo " 2. Run Server (Docker)"
    echo " 3. Stop Docker Server"
    echo " 4. Run Client Launcher"
    echo " 5. Compile All"
    echo " 6. Clean Build"
    echo " 0. Exit"
    echo ""
    echo "========================================"
    echo ""
}

run_server() {
    echo "Starting Server (Local)..."
    cd "$(dirname "$0")/Server_Machine"
    ./run_server.sh &
    echo "Server started."
    read -p "Press Enter to continue..."
    main
}

run_docker() {
    echo "Starting Server (Docker)..."
    cd "$(dirname "$0")/Server_Machine"
    docker compose up -d --build
    docker compose logs -f
}

stop_docker() {
    echo "Stopping Docker Server..."
    cd "$(dirname "$0")/Server_Machine"
    docker compose down
    read -p "Press Enter to continue..."
    main
}

run_launcher() {
    echo "Starting Client Launcher..."
    cd "$(dirname "$0")/Client_Machine"
    ./run_launcher.sh &
    echo "Client Launcher started."
    read -p "Press Enter to continue..."
    main
}

compile_all() {
    echo "Compiling Server..."
    cd "$(dirname "$0")/Server_Machine"
    javac server/*.java
    if [ $? -ne 0 ]; then
        echo "Server compilation failed!"
        read -p "Press Enter to continue..."
        main
    fi
    echo "Server compiled successfully."
    echo ""

    echo "Compiling Client..."
    cd "$(dirname "$0")/Client_Machine"
    rm -rf classes
    mkdir classes
    javac -d classes -sourcepath . client/*.java client/*/*.java
    if [ $? -ne 0 ]; then
        echo "Client compilation failed!"
        read -p "Press Enter to continue..."
        main
    fi
    echo "Client compiled successfully."
    echo ""

    echo "All compilation completed."
    read -p "Press Enter to continue..."
    main
}

clean() {
    echo "Cleaning build files..."
    cd "$(dirname "$0")/Server_Machine"
    rm -rf classes
    rm -f *.class
    rm -f server/*.class
    echo "Server cleaned."

    cd "$(dirname "$0")/Client_Machine"
    rm -rf classes
    rm -f *.class
    rm -f client/*.class
    rm -f client/*/*.class
    echo "Client cleaned."
    echo ""

    echo "All cleaned."
    read -p "Press Enter to continue..."
    main
}

cleanup_exit() {
    echo "Stopping Docker containers..."
    cd "$(dirname "$0")/Server_Machine"
    docker compose down 2>/dev/null
    echo "Goodbye!"
    exit 0
}

main() {
    show_menu
    read -p "Enter your choice: " choice

    case $choice in
        1) run_server ;;
        2) run_docker ;;
        3) stop_docker ;;
        4) run_launcher ;;
        5) compile_all ;;
        6) clean ;;
        0) cleanup_exit ;;
        *) echo "Invalid choice."; read -p "Press Enter to continue..."; main ;;
    esac
}

main
