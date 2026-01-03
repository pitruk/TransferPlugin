package pl.pitruk.transferPlugin;

import pl.pitruk.transferPlugin.api.TransferService;
import pl.pitruk.transferPlugin.api.TransferServiceImpl;
import pl.pitruk.transferPlugin.command.TransferCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for TransferPlugin.
 * <p>
 * This plugin enables player transfers to other servers using
 * the native Paper API method: Player#transfer(String host, int port)
 * <p>
 * Example API usage by other plugins:
 * 
 * <pre>{@code
 * TransferPlugin plugin = (TransferPlugin) Bukkit.getPluginManager().getPlugin("TransferPlugin");
 * plugin.getTransferService().transfer(player, "lobby.example.com", 25565);
 * }</pre>
 */
public class TransferPlugin extends JavaPlugin {

    private TransferService transferService;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Initialize config manager
        this.configManager = new ConfigManager(this);
        this.configManager.load();

        // Initialize transfer service
        this.transferService = new TransferServiceImpl();

        // Register command
        TransferCommand transferCommand = new TransferCommand(this);
        getCommand("transfer").setExecutor(transferCommand);
        getCommand("transfer").setTabCompleter(transferCommand);

        getLogger().info("TransferPlugin has been enabled!");
        getLogger().info("API available via getTransferService()");
    }

    @Override
    public void onDisable() {
        getLogger().info("TransferPlugin has been disabled!");
    }

    /**
     * Returns the transfer service instance.
     * Can be used by other plugins to transfer players.
     *
     * @return TransferService instance
     */
    public TransferService getTransferService() {
        return transferService;
    }

    /**
     * Returns the configuration manager.
     *
     * @return ConfigManager instance
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
