package pl.pitruk.transferPlugin.api;

import org.bukkit.entity.Player;

/**
 * Implementation of the player transfer service.
 * Uses native Paper API method: Player#transfer(String host, int port)
 */
public class TransferServiceImpl implements TransferService {

    @Override
    public void transfer(Player player, String host, int port) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null!");
        }
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Host cannot be empty!");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port must be in range 1-65535!");
        }

        // Use native Paper API method
        player.transfer(host, port);
    }
}
