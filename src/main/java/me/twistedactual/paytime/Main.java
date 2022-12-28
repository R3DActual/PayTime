package me.twistedactual.paytime;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Main extends JavaPlugin implements CommandExecutor {

    private Economy economy;
    private double dayPrice;
    private double nightPrice;
    private String prefix;
    private String notEnoughMoneyMessage;
    private String timeChangedMessage;
    private String invalidPermissionMessage;
    private String configReload;
    private String broadcastMessage;
    private String timeAlreadyDay;
    private String timeAlreadyNight;

    @Override
    public void onEnable() {
        // Load config.yml file and get necessary values
        saveDefaultConfig();
        prefix = getConfig().getString("prefix");
        dayPrice = getConfig().getDouble("day_price");
        nightPrice = getConfig().getDouble("night_price");
        notEnoughMoneyMessage = getConfig().getString("not_enough_money");
        timeChangedMessage = getConfig().getString("time_changed");
        invalidPermissionMessage = getConfig().getString("invalid_permission");
        configReload = getConfig().getString("config_reload");
        broadcastMessage = getConfig().getString("broadcast_message");
        timeAlreadyDay = getConfig().getString("time_already_day");
        timeAlreadyNight = getConfig().getString("time_already_night");

        // Initialize Vault economy support
        if (!setupEconomy()) {
            getLogger().severe("Failed to set up economy support. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register command
        getCommand("paytime").setExecutor(this);
    }

    @Override
    public void onDisable() {
        // Save config.yml file
        saveConfig();
    }

    private boolean setupEconomy() {
        // Set up Vault economy support
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private void reloadCon() {
        reloadConfig();
        prefix = getConfig().getString("prefix");
        dayPrice = getConfig().getDouble("day_price");
        nightPrice = getConfig().getDouble("night_price");
        notEnoughMoneyMessage = getConfig().getString("not_enough_money");
        timeChangedMessage = getConfig().getString("time_changed");
        invalidPermissionMessage = getConfig().getString("invalid_permission");
        configReload = getConfig().getString("config_reload");
        broadcastMessage = getConfig().getString("broadcast_message");
        timeAlreadyDay = getConfig().getString("time_already_day");
        timeAlreadyNight = getConfig().getString("time_already_night");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            // Command can only be executed by a player
            sender.sendMessage(prefix + " " + "This command can only be used by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            // Incorrect number of arguments
            player.sendMessage(translate(prefix + " " + "Usage: /paytime <day | night>"));
            return true;
        }

        long currentTime = player.getWorld().getTime();
        if (args[0].equalsIgnoreCase("day")) {
            if (!sender.hasPermission("paytime.day")) {
                // Player does not have permission to execute command
                sender.sendMessage(translate(prefix + " " + invalidPermissionMessage));
                return true;
            }
            // Set time to day
            if (currentTime >= 0 && player.getWorld().getTime() < 12000) {
                player.sendMessage(translate(prefix + " " + timeAlreadyDay));
                return true;
            }
            if (economy.getBalance(player) < dayPrice) {
                player.sendMessage(translate(prefix + " " + notEnoughMoneyMessage));
                return true;
            }
            player.getWorld().setTime(0);
            economy.withdrawPlayer(player, dayPrice);
            String message = timeChangedMessage.replace("%price%", economy.format(dayPrice)).replace("%time%", "Day");
            player.sendMessage(translate(prefix + " " + message));
            // Broadcast message to server
            String broadcastedMessage = broadcastMessage.replace("%player%", player.getName()).replace("%time%", "Day");
            Bukkit.broadcastMessage(translate(prefix + " " + broadcastedMessage));
        } else if (args[0].equalsIgnoreCase("night")) {
            if (!sender.hasPermission("paytime.night")) {
                // Player does not have permission to execute command
                sender.sendMessage(translate(prefix + " " + invalidPermissionMessage));
                return true;
            }
            // Set time to night
            if (currentTime >= 12880 && currentTime <= 22280) {
                player.sendMessage(translate(prefix + " " + timeAlreadyNight));
                return true;
            }
            if (economy.getBalance(player) < nightPrice) {
                player.sendMessage(translate(prefix + " " + notEnoughMoneyMessage));
                return true;
            }
            player.getWorld().setTime(14000);
            economy.withdrawPlayer(player, nightPrice);
            String message = timeChangedMessage.replace("%price%", economy.format(nightPrice)).replace("%time%", "Night");
            player.sendMessage(translate(prefix + " " + message));
            // Broadcast message to server
            String broadcastedMessage = broadcastMessage.replace("%player%", player.getName()).replace("%time%", "Night");
            Bukkit.broadcastMessage(translate(prefix + " " + broadcastedMessage));
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("paytime.reload")) {
                // Player does not have permission to execute command
                sender.sendMessage(translate(prefix + " " + invalidPermissionMessage));
                return true;
            }
            // Reload config.yml file
            reloadCon();
            sender.sendMessage(translate(prefix + " " + configReload));
        } else {
            // Invalid argument
            player.sendMessage(translate(prefix + " " + "Usage: /paytime <day | night>"));
            return true;
        }

        return true;
    }


    public static final char COLOR_CHAR = '\u00A7';

    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', translateHexColorCodes(string));
    }

    public static String translateHexColorCodes(String message)
    {
        final Pattern hexPattern = Pattern.compile("%#" + "([A-Fa-f0-9]{6})" + "%");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }
}
