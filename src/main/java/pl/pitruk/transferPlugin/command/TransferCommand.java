package pl.pitruk.transferPlugin.command;

import pl.pitruk.transferPlugin.ConfigManager;
import pl.pitruk.transferPlugin.TransferPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler for the /transfer command.
 * <p>
 * Usage:
 * - /transfer <host> [port] - transfers the executing player (requires
 * transfer.use)
 * - /transfer <player> <host> [port] - transfers the specified player (requires
 * transfer.other)
 */
public class TransferCommand implements CommandExecutor, TabCompleter {

    private static final String PERMISSION_USE = "transfer.use";
    private static final String PERMISSION_OTHER = "transfer.other";

    private final TransferPlugin plugin;

    public TransferCommand(TransferPlugin plugin) {
        this.plugin = plugin;
    }

    private ConfigManager config() {
        return plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {

        // /transfer <host> - player transfers themselves (default port)
        if (args.length == 1) {
            return handleSelfTransfer(sender, args[0], null);
        }

        // /transfer <host> <port> OR /transfer <player> <host>
        if (args.length == 2) {
            // Check if first arg is a player name
            Player targetPlayer = Bukkit.getPlayerExact(args[0]);
            if (targetPlayer != null) {
                // /transfer <player> <host> (default port)
                return handleOtherTransfer(sender, args[0], args[1], null);
            } else {
                // /transfer <host> <port>
                return handleSelfTransfer(sender, args[0], args[1]);
            }
        }

        // /transfer <player> <host> <port>
        if (args.length == 3) {
            return handleOtherTransfer(sender, args[0], args[1], args[2]);
        }

        // Invalid number of arguments
        sendUsageMessage(sender);
        return true;
    }

    /**
     * Handles /transfer <host> [port]
     */
    private boolean handleSelfTransfer(CommandSender sender, String host, @Nullable String portArg) {
        // Console cannot transfer "itself"
        if (!(sender instanceof Player player)) {
            sender.sendMessage(config().getMessage("console-must-specify-player"));
            return true;
        }

        // Permission check
        if (!player.hasPermission(PERMISSION_USE)) {
            player.sendMessage(config().getMessage("no-permission"));
            return true;
        }

        // Parse port or use default
        int port;
        if (portArg == null) {
            port = config().getDefaultPort();
        } else {
            Integer parsedPort = parsePort(portArg);
            if (parsedPort == null) {
                player.sendMessage(config().getMessage("invalid-port"));
                return true;
            }
            port = parsedPort;
        }

        // Execute transfer
        player.sendMessage(config().getMessage("transferring-self", null, host, port));
        plugin.getTransferService().transfer(player, host, port);
        return true;
    }

    /**
     * Handles /transfer <player> <host> [port]
     */
    private boolean handleOtherTransfer(CommandSender sender, String targetName, String host,
            @Nullable String portArg) {
        // Permission check (console bypasses)
        if (sender instanceof Player player && !player.hasPermission(PERMISSION_OTHER)) {
            player.sendMessage(config().getMessage("no-permission"));
            return true;
        }

        // Check if player is online
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            sender.sendMessage(config().getMessage("player-not-online", targetName, null, 0));
            return true;
        }

        // Parse port or use default
        int port;
        if (portArg == null) {
            port = config().getDefaultPort();
        } else {
            Integer parsedPort = parsePort(portArg);
            if (parsedPort == null) {
                sender.sendMessage(config().getMessage("invalid-port"));
                return true;
            }
            port = parsedPort;
        }

        // Execute transfer
        sender.sendMessage(config().getMessage("transferring-other", target.getName(), host, port));
        target.sendMessage(config().getMessage("being-transferred", null, host, port));
        plugin.getTransferService().transfer(target, host, port);
        return true;
    }

    /**
     * Parses string to port, returns null if invalid
     */
    private Integer parsePort(String portStr) {
        try {
            int port = Integer.parseInt(portStr);
            if (port >= 1 && port <= 65535) {
                return port;
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    /**
     * Sends appropriate usage message
     */
    private void sendUsageMessage(CommandSender sender) {
        if (sender instanceof Player player) {
            if (player.hasPermission(PERMISSION_OTHER)) {
                player.sendMessage(config().getMessage("usage-admin"));
            } else if (player.hasPermission(PERMISSION_USE)) {
                player.sendMessage(config().getMessage("usage-player"));
            } else {
                player.sendMessage(config().getMessage("no-permission"));
            }
        } else {
            sender.sendMessage(config().getMessage("usage-admin"));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        String defaultHost = config().getDefaultHost();
        int defaultPort = config().getDefaultPort();

        if (args.length == 1) {
            // First argument: can be host (for self) or player (for others)
            if (sender instanceof Player player) {
                // If has permission for others - show players
                if (player.hasPermission(PERMISSION_OTHER)) {
                    completions.addAll(getOnlinePlayerNames());
                }
                // Also show default host
                if (player.hasPermission(PERMISSION_USE) || player.hasPermission(PERMISSION_OTHER)) {
                    completions.add(defaultHost);
                }
            } else {
                // Console - show players
                completions.addAll(getOnlinePlayerNames());
            }
        } else if (args.length == 2) {
            // Second argument: port (for self) or host (for others)
            if (isPlayerName(args[0])) {
                completions.add(defaultHost);
            } else {
                completions.add(String.valueOf(defaultPort));
            }
        } else if (args.length == 3) {
            // Third argument: port (for others)
            completions.add(String.valueOf(defaultPort));
        }

        // Filter by current input
        String currentArg = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(currentArg))
                .collect(Collectors.toList());
    }

    private List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    private boolean isPlayerName(String name) {
        return Bukkit.getPlayerExact(name) != null;
    }
}
