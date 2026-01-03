package pl.pitruk.transferPlugin.api;

import org.bukkit.entity.Player;

/**
 * Public API for transferring players to other servers.
 * <p>
 * Example usage:
 * 
 * <pre>{@code
 * TransferPlugin plugin = (TransferPlugin) Bukkit.getPluginManager().getPlugin("TransferPlugin");
 * plugin.getTransferService().transfer(player, "lobby.example.com", 25565);
 * }</pre>
 */
public interface TransferService {

    /**
     * Transfers a player to the specified server.
     *
     * @param player the player to transfer
     * @param host   target server hostname
     * @param port   target server port
     * @throws IllegalArgumentException if player is null, host is empty, or port is
     *                                  invalid
     */
    void transfer(Player player, String host, int port);
}
