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
│   ├── docker-compose.yml          # Docker Compose config with Tailscale
│   └── run_server.bat             # Run server locally
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
| 🌍 **Global Access** | Share files with anyone around the world via Tailscale |

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
| Share with **anyone globally** | Option 2 (Docker + Tailscale) |

### 3. Connect the Client
```bat
# Option 4 in menu
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

### 🌍 Mode 3 — Global Access (Tailscale)

Share files with **anyone in the world** using Tailscale VPN.

#### How It Works

```
┌─────────────────────────────────────────────────────────┐
│                    YOUR COMPUTER                        │
│                                                         │
│   ┌─────────────┐      ┌──────────────┐                 │
│   │   Server    │──────│  Tailscale    │─────► INTERNET  │
│   │  (Port 5000)│      │              │                 │
│   └─────────────┘      └──────────────┘                 │
│                               │                         │
│                         Tailscale IP                    │
└─────────────────────────────────────────────────────────┘
```

#### Step 1: Get Tailscale Auth Key

1. Create account at https://login.tailscale.com
2. Go to **Settings → Keys → Generate auth key**
3. Copy the auth key (starts with `tskey-auth-`)

#### Step 2: Configure Environment

```bash
cd Server_Machine
cp .env.example .env
# Edit .env and add your TS_AUTH_KEY
```

#### Step 3: Build & Start

```bash
# Build the Docker image
./build_docker.sh

# Start with Tailscale
docker compose up -d

# Check logs for Tailscale IP
docker logs phantom-transfer-server-TailScale
```

#### Step 4: Connect Clients

1. Install Tailscale on client machine
2. Connect to your tailnet
3. Enter the server's Tailscale IP on port `5000`

---

## ✦ Project Menu — `run_all.bat`

| Option | Action | Use Case |
|--------|--------|----------|
| `1` | Run Server (Local) | LAN sharing |
| `2` | Run Server (Docker) | LAN sharing (Docker) |
| `3` | Stop Docker Server | Stop Docker server |
| `4` | Run Client Launcher | Connect to server |
| `5` | Compile All | Rebuild Java code |
| `6` | Clean Build | Remove compiled files |
| `0` | Exit | Close all windows |

---

## ✦ Finding Your IP

### LAN IP (Local Network)

**Windows:**
```cmd
ipconfig
```
Look for `IPv4 Address` under your active adapter.

### Tailscale IP

```bash
# On server
docker exec phantom-transfer-server-TailScale tailscale ip -4

# Or on host with Tailscale installed
tailscale ip -4
```

---

## ✦ Server Configuration

| Setting | Default | Description |
|---|---|---|
| Port | `5000` | Server listening port |
| Max File Size | `10 GB` | Per-upload size limit |
| Refresh Interval | `5 sec` | Auto-refresh timer |

---

## ✦ Client Connection Guide

| Deployment | IP Address | Port |
|------------|------------|------|
| Local Server | `192.168.x.x` (LAN IP) | `5000` |
| Docker Server | `192.168.x.x` (LAN IP) | `5000` |
| Tailscale | Server's Tailscale IP | `5000` |

---

## ✦ Network Requirements

### For LAN Sharing
- Server and clients on the **same network**
- Port `5000` open on server machine firewall

### For Global Sharing (Tailscale)
- Tailscale installed on both server and client
- Valid auth key configured

---

## ✦ Security Considerations

> ⚠️ **Important:** This application has **no authentication**.

- Anyone with the IP/URL can connect
- No encryption on file transfers (within Tailscale VPN is encrypted)
- Suitable for trusted networks or temporary sharing

### Recommendations

1. **Use Tailscale** for encrypted, controlled sharing
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
<summary><strong>Tailscale Not Connecting</strong></summary>

1. Make sure `TS_AUTH_KEY` is set correctly in `.env`
2. Check container logs:
   ```bash
   docker logs phantom-transfer-server-TailScale
   ```
3. Verify your auth key is valid at https://login.tailscale.com/admin/settings/keys

</details>

<details>
<summary><strong>Docker Container Not Starting</strong></summary>

```bash
docker logs phantom-transfer-server-TailScale
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
./build_docker.sh
```

</details>

---

## ✦ Requirements

| Requirement | Version | Notes |
|-------------|---------|-------|
| Java | 17+ | Required |
| Docker | Latest | Containerized server with Tailscale |
| Tailscale | Latest | Global access (Docker image includes Tailscale) |
| OS | Windows 10/11 or Linux | |

---

## ✦ License

Released under the License of "I am a 17yo releasing real Projects while your grown ass wastes time"

---

<div align="center">

*Built for speed. Built for your LAN. Accessible anywhere.*
**◈ Phantom Transfer**

</div>
