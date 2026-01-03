# TransferPlugin

A lightweight Paper plugin for Minecraft Java Edition (1.20.6+) that enables player transfers to other servers using the native `Player#transfer(String host, int port)` method.

## Features

- 🚀 **Native Paper API** - Uses Paper's built-in transfer method, no NMS or proxy required
- 🔒 **Permission-based** - Granular control over who can transfer themselves or others
- 📡 **Public API** - Easy integration for other plugins
- ⚙️ **Fully configurable** - All messages and settings editable via config files
- ✅ **Full validation** - Port range checking, online player verification

## Requirements

- Paper 1.20.6+
- Java 21+

## Server Setup

For player transfers to work, you must configure your servers to accept transfers.

### Target Server (server.properties)

On the server that will **receive** transferred players, add:

```properties
accepts-transfers=true
```

### Velocity Proxy (optional)

If using Velocity proxy, configure `velocity.toml`:

```toml
[advanced]
# Allow players to be transferred between servers
accepts-transfers = true
```

> **Important:** Both the source and target servers must have the transfer feature properly configured.

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/transfer <host>` | Transfer yourself (default port) | `transfer.use` |
| `/transfer <host> <port>` | Transfer yourself to specific port | `transfer.use` |
| `/transfer <player> <host>` | Transfer another player (default port) | `transfer.other` |
| `/transfer <player> <host> <port>` | Transfer another player to specific port | `transfer.other` |

> **Note:** Console must always specify a player. Port defaults to `25565` (configurable).

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `transfer.use` | Allows transferring yourself | OP |
| `transfer.other` | Allows transferring other players | OP |

## Configuration

### config.yml

```yaml
# Default server settings (used for tab completion and when port is omitted)
default-host: "play.example.com"
default-port: 25565

# Message prefix (supports color codes with &)
prefix: "&8[&bTransfer&8] "
```

### messages.yml

All messages are fully customizable with placeholders `{player}`, `{host}`, `{port}`:

```yaml
no-permission: "&cYou don't have permission to use this command!"
console-must-specify-player: "&cConsole must specify a player: /transfer <player> <host> [port]"
player-not-online: "&cPlayer &e{player} &cis not online!"
invalid-port: "&cPort must be a number in range 1-65535!"
usage-player: "&cUsage: /transfer <host> [port]"
usage-admin: "&cUsage: /transfer <player> <host> [port]"
transferring-self: "&aTransferring you to &e{host}:{port}&a..."
transferring-other: "&aTransferring player &e{player} &ato &e{host}:{port}&a..."
being-transferred: "&aYou are being transferred to &e{host}:{port}&a..."
```

## API Usage

Other plugins can use TransferPlugin's API to transfer players programmatically.

### Add as dependency

In your `plugin.yml`:
```yaml
depend: [TransferPlugin]
# or
softdepend: [TransferPlugin]
```

### Example code

```java
import pl.pitruk.transferPlugin.TransferPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Example {
    
    public void transferPlayer(Player player) {
        TransferPlugin plugin = (TransferPlugin) Bukkit.getPluginManager().getPlugin("TransferPlugin");
        
        if (plugin != null && plugin.isEnabled()) {
            plugin.getTransferService().transfer(player, "lobby.example.com", 25565);
        }
    }
}
```

### API Interface

```java
public interface TransferService {
    /**
     * Transfers a player to the specified server.
     *
     * @param player the player to transfer
     * @param host   target server hostname
     * @param port   target server port (1-65535)
     * @throws IllegalArgumentException if player is null, host is empty, or port is invalid
     */
    void transfer(Player player, String host, int port);
}
```

## Building

```bash
mvn clean package
```

The compiled JAR will be in `target/TransferPlugin-1.0.0.jar`.

## Installation

1. Build the plugin or download from releases
2. Place the JAR in your server's `plugins` folder
3. Restart the server
4. Edit `plugins/TransferPlugin/config.yml` and `messages.yml` as needed

## License

MIT License
