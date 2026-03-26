# ◈ PHANTOM TRANSFER

> *Invisible on your network. Unstoppable in your LAN. Accessible anywhere in the world.*

A **file transfer application** built with Java sockets — deploy the server locally or via Docker, then share files across your network or around the globe.

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
│   ├── run_server.bat              # Run server locally
│   └── run_server_bore.bat        # Run server with Bore tunnel
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
| 🌐 **LAN Sharing** | Share files with anyone on your local network |
| 🌍 **Global Access** | Share files with anyone around the world via Bore tunnel |

---

## ✦ Quick Start

### 1. Launch the Menu
```bat
run_all.bat
```

### 2. Choose Your Deployment

| Use Case | Recommended Option |
|----------|-------------------|
| Share on **same network** | Option 1 (Local) or 2 (Docker) |
| Share with **anyone globally** | Option 4 (Local) or 5 (Docker + Bore) |

### 3. Connect the Client
```bat
# Option 7 in menu
run_launcher.bat
```

---

## ✦ Deployment Modes

### 🌐 Mode 1 — LAN Only (Local)

Share files with devices on the **same network**.

**Server:**
```bat
# Option 1 in menu
cd Server_Machine
run_server.bat
```

**Client:**
- **IP:** Your server's LAN IP (e.g., `192.168.1.9`)
- **Port:** `5000`

---

### 🌐 Mode 2 — LAN Only (Docker)

Same as above but server runs in Docker.

**Server:**
```bash
# Option 2 in menu
cd Server_Machine
docker compose up -d
```

**Client:** Same as Mode 1.

---

### 🌍 Mode 3 — Global Access (Bore Tunnel)

Share files with **anyone in the world** — no router configuration needed, no accounts required.

#### How It Works

```
┌─────────────────────────────────────────────────────────┐
│                    YOUR COMPUTER                          │
│                                                         │
│   ┌─────────────┐      ┌──────────────┐                │
│   │   Server    │──────│  Bore Tunnel │─────► INTERNET │
│   │  (Port 5000)│      │              │                │
│   └─────────────┘      └──────────────┘                │
│                               │                         │
│                         Public URL                      │
│                    bore.pub:xxxxx                       │
└─────────────────────────────────────────────────────────┘
```

#### Step 1: Install Bore

**Windows:**
```powershell
# Download from GitHub releases
iwr https://github.com/ekzhang/bore/releases/download/v0.5.0/bore-v0.5.0-x86_64-pc-windows-msvc.zip -OutFile bore.zip
Expand-Archive bore.zip -DestinationPath C:\bore
# Add to PATH or use full path
```

**Linux/Mac:**
```bash
# Using cargo
cargo install bore-cli

# Or download binary
curl -fsSL https://github.com/ekzhang/bore/releases/download/v0.5.0/bore-v0.5.0-x86_64-unknown-linux-musl.tar.gz | tar -xz
./bore local 5000 --to bore.pub
```

#### Step 2: Start Server + Bore

**Option A — Local**
```bash
# Option 4 in menu
cd Server_Machine
run_server_bore.bat
```

**Option B — Docker (no installation needed)**
```bash
# Option 5 in menu
cd Server_Machine
docker compose up -d --profile bore
docker compose logs -f transfer-server-bore
```

#### Step 3: Get the Public URL

Bore will display a URL like:
```
listening on bore.pub:xxxxx
```

#### Step 4: Connect Clients

Share the bore.pub URL with anyone globally.

**Client:**
- **IP:** `bore.pub`
- **Port:** The port number shown (e.g., `xxxxx`)

---

## ✦ Project Menu — `run_all.bat`

| Option | Action | Use Case |
|--------|--------|----------|
| `1` | Run Server (Local) | LAN sharing |
| `2` | Run Server (Docker) | LAN sharing (Docker) |
| `3` | Stop Docker Server | Stop Docker server |
| `4` | Run Server + Bore Tunnel (Local) | 🌐 Global sharing |
| `5` | Run Server + Bore Tunnel (Docker) | 🌐 Global sharing (Docker) |
| `6` | Stop Docker Bore Tunnel | Stop bore tunnel server |
| `7` | Run Client Launcher | Connect to server |
| `8` | Compile All | Rebuild Java code |
| `9` | Clean Build | Remove compiled files |
| `0` | Exit | Close all windows |

---

## ✦ Finding Your IP

### LAN IP (Local Network)

**Windows:**
```cmd
ipconfig
```
Look for `IPv4 Address` under your active adapter.

**Linux:**
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
| Bore Tunnel Server | `bore.pub` | Public tunnel server |

---

## ✦ Client Connection Guide

| Deployment | IP Address | Port |
|------------|------------|------|
| Local Server | `192.168.x.x` (LAN IP) | `5000` |
| Bore Tunnel | `bore.pub` | `xxxxx` (from bore output) |

---

## ✦ Network Requirements

### For LAN Sharing
- Server and clients on the **same network**
- Port `5000` open on server machine firewall

### For Global Sharing (Bore)
- Internet connection
- Bore installed (for local mode only)
- No router configuration needed

---

## ✦ Security Considerations

> ⚠️ **Important:** This application has **no authentication**.

- Anyone with the IP/URL can connect
- No encryption on file transfers
- Suitable for trusted networks or temporary sharing

### Recommendations

1. **Use Bore tunnel** for temporary, controlled sharing
2. **Destroy rooms** after use
3. **Firewall** restricts who can connect

---

## ✦ Troubleshooting

<details>
<summary><strong>Connection Refused</strong></summary>

- Verify the server is running
- Check the IP address and port number
- Disable firewall temporarily:
  ```powershell
  netsh advfirewall firewall add rule name="Transfer" dir=in action=allow localport=5000 protocol=tcp
  ```

</details>

<details>
<summary><strong>Bore Not Working</strong></summary>

1. Make sure Bore is installed correctly
2. Check if `bore.pub` is accessible
3. Try a different tunnel command:
   ```bash
   bore local 5000 --to bore.pub
   ```

</details>

<details>
<summary><strong>Docker Container Not Starting</strong></summary>

```bash
docker logs Phantom-Transfer-Server
docker logs Phantom-Transfer-Bore
```

Check for port conflicts:
```bash
netstat -an | grep 5000
```

</details>

<details>
<summary><strong>Cannot Build Docker Image</strong></summary>

```bash
docker system prune -a
cd Server_Machine
docker build --no-cache -t phantom-transfer-server:latest .
```

</details>

---

## ✦ Requirements

| Requirement | Version | Notes |
|-------------|---------|-------|
| Java | 17+ | Required |
| Bore | Latest | Global tunnel mode only |
| Docker | Latest | Containerized server |
| OS | Windows 10/11 or Linux | |

---

## ✦ License

Released under the [MIT License](LICENSE).

---

<div align="center">

*Built for speed. Built for your LAN. Accessible anywhere.*
**◈ Phantom Transfer**

</div>
