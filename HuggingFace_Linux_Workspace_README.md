# Linux Workspace on Hugging Face Spaces

## Project Overview

**Space Name:** `Raihan1234/app`  
**URL:** https://raihan1234-app.hf.space  
**Status:** Running on `cpu-basic` hardware  
**Created:** May 22, 2026

This project provides a **browser-based Linux terminal and file manager** running as a Docker container on Hugging Face Spaces. It allows users to access a full Linux command-line environment directly from their browser, install packages via npm/pip/apt-get, and persist their work through Git-based cloud backup.

---

## What Is This Project?

This is a **Linux Workspace** - a lightweight, browser-based development environment that provides:

1. **Web Terminal** - Full bash shell access via xterm.js
2. **File Manager** - Upload, download, create, edit, delete files through a web UI
3. **tmux Integration** - Terminal sessions persist even when you disconnect
4. **Package Installation** - Install npm packages, pip packages, apt packages directly
5. **Cloud Backup** - Automatic periodic backup of workspace to a Hugging Face Dataset

**Use Cases:**
- Quick coding tasks without setting up local environment
- Accessing a Linux shell from any device with a browser
- Testing CLI tools and packages in an isolated environment
- Development environment for ML projects (model training scripts, data processing)
- Learning Linux commands in a safe environment

---

## Technical Architecture

### Technology Stack

| Component | Technology |
|-----------|------------|
| **Runtime** | Node.js 20 (Docker container) |
| **Web Framework** | Express.js |
| **Terminal Emulator** | xterm.js |
| **PTY Management** | node-pty |
| **Real-time Communication** | WebSocket (ws) |
| **Session Persistence** | tmux |
| **File Storage** | Hugging Face Dataset (Git-based) |

### Dependencies (`package.json`)

```json
{
  "express": "^4.21.0",
  "node-pty": "^1.0.0",
  "ws": "^8.18.0",
  "xterm": "^5.3.0",
  "xterm-addon-fit": "^0.8.0",
  "xterm-addon-web-links": "^0.9.0"
}
```

### File Structure

```
Raihan1234/app/
├── Dockerfile           # Container definition (Node.js 20-slim based)
├── server.mjs           # Express server + WebSocket terminal backend
├── file-manager.html    # Web-based file manager UI
├── package.json         # Node.js dependencies
└── README.md            # This file
```

### System Packages Installed

- `bash`, `git`, `curl`, `nano`, `vim` - Basic Linux utilities
- `tmux` - Terminal multiplexer for session persistence
- `build-essential` - Compiler toolchain (for native modules)
- `python3` - Python runtime

### Port Configuration

- **Container Port:** 7860
- **Protocol:** HTTP

---

## How It Works

### 1. Web Terminal (xterm.js + node-pty)

The terminal interface is built using:
- **xterm.js** - JavaScript terminal emulator that runs in the browser
- **node-pty** - Node.js bindings for the pseudo-terminal (PTY) subsystem
- **WebSocket** - Real-time bidirectional communication between browser and server

When a user connects:
1. Browser establishes WebSocket connection
2. Server spawns a bash shell via node-pty inside a tmux session
3. User input is sent via WebSocket → written to PTY → shell executes → output goes back via WebSocket
4. tmux ensures the shell session survives reconnection

### 2. File Manager

A simple web-based file explorer that provides:
- **List files/directories** with metadata (size, modification date)
- **Upload files** (up to 200MB limit)
- **Download files**
- **Create/delete files and folders**
- **Navigate with breadcrumbs**

All file operations are handled via REST API endpoints on the Express server.

### 3. Cloud Backup System

The workspace includes an automatic backup system that syncs to a Hugging Face Dataset.

**Backup Target:** `Raihan1234/workspace-backup` (a Hugging Face Dataset with Git storage)

**How it works:**

```
┌─────────────────────────────────────────────────────────┐
│                    Hugging Face Infrastructure          │
│                                                         │
│  ┌──────────────┐    git push    ┌──────────────────┐  │
│  │   Container   │ ──────────────►   Dataset Repo    │  │
│  │  /data/       │                │  (workspace-     │  │
│  │  workspace    │                │   backup)        │  │
│  └──────────────┘                └──────────────────┘  │
│         │                                       │      │
│         │ git pull                              │      │
│         ◄───────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────┘
```

**Backup Triggers:**
- **Startup:** `cloudRestore()` runs once if workspace is empty - pulls latest files from Git repo
- **Every 10 minutes:** `cloudBackup()` automatically compresses and pushes workspace to Git
- **Graceful Shutdown:** SIGTERM handler triggers final backup before container stops
- **Manual:** User can click "Save to cloud" button in UI to trigger immediate backup

**Files Excluded from Backup:**
- `.git` directories (to avoid recursive commits)
- All other dotfiles are preserved

---

## Hugging Face Spaces Context

### Why Hugging Face Spaces?

This project runs on Hugging Face Spaces as a **proof-of-concept** to explore whether a general-purpose Linux workspace can be hosted on HF's free infrastructure. HF Spaces was designed primarily for ML demos, but the Docker SDK allows significant flexibility in what you can run inside a container.

### The Gray Area

**Important Context:** This project exists in a gray area regarding HF's intended use case:

| HF's Intended Use | This Project |
|-------------------|--------------|
| ML prototypes and demos | ❌ Not ML-specific |
| Interactive demos with trained models | ❌ General computing |
| Lightweight compute workloads | ⚠️ Continuous server process |
| Showcasing models | ❌ Development environment |

**What HF Allows:**
- ✅ Using Docker SDK to define custom environments
- ✅ Running web servers on exposed ports
- ✅ Installing packages (npm, pip, apt-get)
- ✅ Using tmux, bash, git, and standard Linux tools

**What Could Be Flagged:**
- ⚠️ Long-running persistent services (non-interactive for extended periods)
- ⚠️ Periodic background tasks (backup every 10 min)
- ⚠️ Non-ML workloads

**Current Status:** The space has been running successfully since May 22, 2026 without issues on the `cpu-basic` tier. HF's automated monitoring appears to tolerate this use case, but there's no guarantee this will continue indefinitely.

### Why This Isn't "Abuse"

- The container uses minimal resources (cpu-basic tier)
- Backup operations are lightweight (Git commit/push)
- The service is genuinely useful for development tasks
- No cryptomining, malicious networking, or illegal activities
- HF knowingly accepts Docker-based Spaces with custom behavior

---

## Usage Guide

### Accessing the Space

1. Open https://raihan1234-app.hf.space in your browser
2. You'll see two options:
   - **Terminal** - Full bash shell access
   - **File Manager** - Web-based file explorer

### Terminal Commands

The terminal provides a full bash environment with the following customizations:

**Aliases:**
- `ll` - List files with details (`ls -la`)
- `gs` - Git status (`git status`)
- `backup` - Manual cloud backup trigger
- `restore` - Manual cloud restore trigger

**Available Operations:**
```bash
# Install npm packages
npm install <package>

# Install Python packages  
pip install <package>

# Install system packages
apt-get install <package>

# Clone repos, manage files, run scripts, etc.
git clone https://github.com/user/repo.git
python3 train.py
node app.js
```

### File Manager Usage

1. Navigate folders using breadcrumb bar
2. **Upload** - Click upload button, select files (max 200MB)
3. **Download** - Click file to download
4. **Create** - Use "New File" or "New Folder" buttons
5. **Delete** - Use delete button on files/folders

### Cloud Backup

Your workspace is automatically backed up every 10 minutes to `Raihan1234/workspace-backup`. If you need to persist data long-term or move to a different Space, you can rely on this backup.

---

## Environment Details

### Container Environment

```
WORKSPACE_DIR=/data/workspace (persistent) or /app/workspace (fallback)
BACKUP_REPO=/app/.backup-repo
HF_TOKEN=<user-provided-token>
```

### Bash Configuration

Custom prompt and settings in `/etc/bash.bashrc`:
- Colorful PS1 prompt
- Welcome message on login
- Useful aliases

### Tmux Configuration

Settings in `/etc/tmux.conf`:
- Mouse support enabled
- Extended history (10,000 lines)
- Custom status bar styling
- UTF-8 support

---

## Known Limitations

1. **No GPU Access** - Running on cpu-basic, no CUDA support
2. **Internet Restrictions** - Some outbound connections may be blocked
3. **No Root Access** - Standard user permissions apply
4. **Cold Start Delays** - Container restart can take 30-60 seconds
5. **Resource Limits** - Free tier has CPU/memory restrictions
6. **Ephemeral by Design** - While `/data/workspace` persists, container can be recycled
7. **Not Designed for Heavy Workloads** - Intended for light development/demo use

---

## Future Considerations

If Hugging Face discontinues or restricts this type of usage, alternatives include:

- **GitHub Codespaces** - Full dev environment in cloud
- **Replit** - Cloud IDE with persistent files
- **Gitpod** - Cloud development environments
- **Cheap VPS** - DigitalOcean, Linode, Vultr ($3-5/month)

---

## Project Structure Summary

```
User's Browser
      │
      ▼
┌─────────────────────────────────┐
│   Hugging Face Space (Docker)   │
│                                 │
│  ┌───────────┐  ┌────────────┐  │
│  │  xterm.js │  │   Express  │  │
│  │  (client) │  │   Server   │  │
│  └─────┬─────┘  └──────┬─────┘  │
│        │               │        │
│  WebSocket              │        │
│        │         ┌─────▼─────┐  │
│        └────────►│  node-pty │  │
│                  └─────┬─────┘  │
│                        │        │
│                  ┌─────▼─────┐  │
│                  │    bash   │  │
│                  │   inside  │  │
│                  │   tmux    │  │
│                  └───────────┘  │
│                                 │
│  ┌────────────────────────────┐ │
│  │   Cloud Backup System      │ │
│  │   (Git → HF Dataset)       │ │
│  └────────────────────────────┘ │
└─────────────────────────────────┘
```

---

## About This README

This README was created to provide full context about what this project is, how it works, and the technical/policy considerations around running a non-ML workload on Hugging Face Spaces.

**Last Updated:** June 2026

For questions or issues with this project, refer to the code in the Space's repository or the Hugging Face community forums.