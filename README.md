# ◈ PHANTOM TRANSFER

> *Invisible on your network. Unstoppable in your LAN.*

A **LAN-based file transfer application** built with Java sockets — deploy the server locally or via Docker, then share files across your network instantly.

---

## ✦ Architecture

```
phantom-transfer/
│
├── 📁 Client_Machine/              # Client application
│   ├── client/
│   │   ├── controller/             # MVC Controller
│   │   ├── model/                  # MVC Model
│   │   ├── view/                   # MVC View (Swing UI)
│   │   └── network/                # Network utilities
│   ├── downloads/                  # Downloaded files land here
│   ├── run_client.bat              # Launch in connect mode
│   └── run_launcher.bat            # Launch GUI mode
│
├── 📁 Server_Machine/              # Server application
│   ├── server/                     # Java source code
│   ├── rooms/                      # Room file storage
│   ├── Dockerfile                  # Docker image definition
│   ├── docker-compose.yml          # Docker Compose config
│   └── run_server.bat              # Run server locally
│
└── 🚀 run_all.bat                  # Main menu launcher
```

---

## ✦ Features

| Feature | Description |
|---|---|
| 🏠 **Rooms** | Generate unique room codes to organize file sharing |
| 📤 **Upload / Download** | Transfer files between clients through the server |
| 👑 **Host Controls** | Room host can destroy rooms at will |
| 🔄 **Auto-Refresh** | File lists update automatically every 5 seconds |
| 🐳 **Docker Support** | Deploy server as a containerized service |

---

## ✦ Quick Start

### Option 1 — Local Deployment

**1. Start the Server**
```bat
cd Server_Machine
run_server.bat
```

**2. Launch the Client**
```bat
cd Client_Machine
run_launcher.bat     # GUI mode (recommended)
run_client.bat       # Connect mode — edit the IP first
```

**3. Connect**
- Enter your Server IP and Port (default: `5000`)
- Create a new room or join an existing one with a code

---

### Option 2 — Docker Deployment

**1. Build the Image**
```bash
cd Server_Machine
docker build -t phantom-transfer-server:latest .
```

**2. Run the Container**
```bash
docker run -d --name Phantom-Transfer-Server \
  -p 5000:5000 \
  -v C:\Transfer\Server_Machine\rooms:/app/rooms \
  phantom-transfer-server:latest
```

Or with Docker Compose:
```bash
cd Server_Machine
docker compose up -d
```

**3. Connect the Client**
- Enter your machine's LAN IP (e.g. `192.168.1.9`)
- Port: `5000`

---

## ✦ Project Menu — `run_all.bat`

| Option | Action |
|---|---|
| `1` | Run Server (Local) |
| `2` | Run Server (Docker) |
| `3` | Stop Docker Server |
| `4` | Run Client Launcher |
| `5` | Compile All |
| `6` | Clean Build |
| `0` | Exit — closes all windows |

---

## ✦ Finding Your LAN IP

**Windows**
```cmd
ipconfig
```
Look for `IPv4 Address` under your active adapter (Wi-Fi or Ethernet).

**Linux**
```bash
ip addr show
```

---

## ✦ Server Configuration

| Setting | Default | Description |
|---|---|---|
| Port | `5000` | Server listening port |
| Max File Size | `10 GB` | Per-upload size limit |
| Refresh Interval | `5 sec` | Auto-refresh timer |

---

## ✦ Network Requirements

- Server and all clients must be on the **same LAN**
- Port `5000` must be open — check your firewall rules
- Docker deployments: ensure container port is mapped to host

---

## ✦ Troubleshooting

<details>
<summary><strong>Connection Refused</strong></summary>

- Verify the server is running
- Double-check the IP address and port number
- Disable your firewall temporarily, or add a firewall exception for port `5000`

</details>

<details>
<summary><strong>Docker Port Mapping Missing</strong></summary>

```bash
docker stop Phantom-Transfer-Server
docker rm Phantom-Transfer-Server
docker run -d --name Phantom-Transfer-Server -p 5000:5000 phantom-transfer-server:latest
```

</details>

<details>
<summary><strong>Cannot Build Docker Image</strong></summary>

```bash
docker system prune -a
docker build --no-cache -t phantom-transfer-server:latest .
```

</details>

---

## ✦ Requirements

- **Java** 17+
- **Docker** *(for containerized server)*
- **OS:** Windows 10/11 or Linux
- **Network:** LAN connectivity between server and clients

---

## ✦ License

Released under the [MIT License](LICENSE).

---

<div align="center">

*Built for speed. Built for your LAN.*
**◈ Phantom Transfer**

</div>