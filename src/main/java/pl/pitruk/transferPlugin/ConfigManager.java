package pl.pitruk.transferPlugin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Configuration manager for TransferPlugin.
 * Handles loading and accessing config.yml and messages.yml
 */
public class ConfigManager {

    private final TransferPlugin plugin;
    private FileConfiguration messagesConfig;
    private String prefix;
    private String defaultHost;
    private int defaultPort;

    public ConfigManager(TransferPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads all configuration files
     */
    public void load() {
        // Save default configs if they don't exist
        plugin.saveDefaultConfig();
        saveDefaultMessages();

        // Load main config
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        this.prefix = colorize(config.getString("prefix", "&8[&bTransfer&8] "));
        this.defaultHost = config.getString("default-host", "play.example.com");
        this.defaultPort = config.getInt("default-port", 25565);

        // Load messages
        loadMessages();
    }

    /**
     * Saves default messages.yml if it doesn't exist
     */
    private void saveDefaultMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }

    /**
     * Loads messages.yml configuration
     */
    private void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Set defaults from jar
        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            messagesConfig.setDefaults(defaultConfig);
        }
    }

    /**
     * Gets a message from messages.yml with prefix
     */
    public String getMessage(String key) {
        String message = messagesConfig.getString(key, "Missing message: " + key);
        return prefix + colorize(message);
    }

    /**
     * Gets a message with placeholder replacements
     */
    public String getMessage(String key, String player, String host, int port) {
        String message = getMessage(key);
        if (player != null) {
            message = message.replace("{player}", player);
        }
        if (host != null) {
            message = message.replace("{host}", host);
        }
        message = message.replace("{port}", String.valueOf(port));
        return message;
    }

    /**
     * Converts color codes (&) to Minecraft color codes
     */
    private String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDefaultHost() {
        return defaultHost;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    /**
     * Reloads all configuration files
     */
    public void reload() {
        load();
    }
}
