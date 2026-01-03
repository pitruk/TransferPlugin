# TransferPlugin

A lightweight Paper plugin for Minecraft Java Edition (1.20.5+) that enables player transfers to other servers using the native `Player#transfer(String host, int port)` method.

## Features

- 🚀 **Native Paper API** - Uses Paper's built-in transfer method, no NMS or proxy required
- 🔒 **Permission-based** - Granular control over who can transfer themselves or others
- 📡 **Public API** - Easy integration for other plugins
- ✅ **Full validation** - Port range checking, online player verification

## Requirements

- Paper 1.20.5+
- Java 21+

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/transfer <host> <port>` | Transfer yourself to another server | `transfer.use` |
| `/transfer <player> <host> <port>` | Transfer another player | `transfer.other` |

> **Note:** Console must always specify a player (3-argument form).

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `transfer.use` | Allows using `/transfer <host> <port>` | OP |
| `transfer.other` | Allows using `/transfer <player> <host> <port>` | OP |

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

### API Methods

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

## License

MIT License
