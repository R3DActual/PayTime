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

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Main extends JavaPlugin implements CommandExecutor {

    private Economy economy;
    private double dayPrice;
    private double nightPrice;
    private double stormPrice;
    private double clearPrice;
    private boolean broadcast;
    private String prefix;
    private String notEnoughMoneyMessage;
    private String timeChangedMessage;
    private String weatherChangedMessage;
    private String invalidPermissionMessage;
    private String configReload;
    private String broadcastTimeMessage;
    private String broadcastWeatherMessage;
    private String currencySymbol;
    private String timeAlreadyDay;
    private String timeAlreadyNight;
    private String weatherAlreadyStormy;
    private String weatherAlreadyClear;

    @Override
    public void onEnable() {
        // Load config.yml file and get necessary values

        saveDefaultConfig();
        broadcast = getConfig().getBoolean("broadcast");
        prefix = getConfig().getString("prefix");
        dayPrice = getConfig().getDouble("day_price");
        nightPrice = getConfig().getDouble("night_price");
        stormPrice = getConfig().getDouble("storm_price");
        clearPrice = getConfig().getDouble("clear_price");
        currencySymbol = getConfig().getString("currency_symbol");
        notEnoughMoneyMessage = getConfig().getString("not_enough_money");
        timeChangedMessage = getConfig().getString("time_changed");
        weatherChangedMessage = getConfig().getString("weather_changed");
        invalidPermissionMessage = getConfig().getString("invalid_permission");
        configReload = getConfig().getString("config_reload");
        broadcastTimeMessage = getConfig().getString("broadcast_time_message");
        broadcastWeatherMessage = getConfig().getString("broadcast_weather_message");
        timeAlreadyDay = getConfig().getString("time_already_day");
        timeAlreadyNight = getConfig().getString("time_already_night");
        weatherAlreadyStormy = getConfig().getString("weather_already_stormy");
        weatherAlreadyClear = getConfig().getString("weather_already_clear");

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
        broadcast = getConfig().getBoolean("broadcast");
        prefix = getConfig().getString("prefix");
        dayPrice = getConfig().getDouble("day_price");
        nightPrice = getConfig().getDouble("night_price");
        stormPrice = getConfig().getDouble("storm_price");
        clearPrice = getConfig().getDouble("clear_price");
        currencySymbol = getConfig().getString("currency_symbol");
        notEnoughMoneyMessage = getConfig().getString("not_enough_money");
        timeChangedMessage = getConfig().getString("time_changed");
        weatherChangedMessage = getConfig().getString("weather_changed");
        invalidPermissionMessage = getConfig().getString("invalid_permission");
        configReload = getConfig().getString("config_reload");
        broadcastTimeMessage = getConfig().getString("broadcast_time_message");
        broadcastWeatherMessage = getConfig().getString("broadcast_weather_message");
        timeAlreadyDay = getConfig().getString("time_already_day");
        timeAlreadyNight = getConfig().getString("time_already_night");
        weatherAlreadyStormy = getConfig().getString("weather_already_stormy");
        weatherAlreadyClear = getConfig().getString("weather_already_clear");
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
            player.sendMessage(translate(prefix + " " + "Usage: /paytime <day | night | storm | clear | price>"));
            return true;
        }

        long currentTime = player.getWorld().getTime();
        boolean isWeatherClear = player.getWorld().isClearWeather();
        boolean isThundering = player.getWorld().isThundering();
        boolean isStorming = player.getWorld().hasStorm();
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
            String message = timeChangedMessage.replace("%price%", currencySymbol + formatDouble(dayPrice)).replace("%time%", "Day");
            player.sendMessage(translate(prefix + " " + message));

            // Broadcast message to server
            if(broadcast){
                String broadcastedMessage = broadcastTimeMessage.replace("%player%", player.getName()).replace("%time%", "Day");
                Bukkit.broadcastMessage(translate(prefix + " " + broadcastedMessage));
            }
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
            String message = timeChangedMessage.replace("%price%", currencySymbol + formatDouble(nightPrice)).replace("%time%", "Night");
            player.sendMessage(translate(prefix + " " + message));

            // Broadcast message to server
            if(broadcast){
                String broadcastedMessage = broadcastTimeMessage.replace("%player%", player.getName()).replace("%time%", "Night");
                Bukkit.broadcastMessage(translate(prefix + " " + broadcastedMessage));
            }
        } else if (args[0].equalsIgnoreCase("storm")) {
            if (!sender.hasPermission("paytime.storm")) {
                // Player does not have permission to execute command
                sender.sendMessage(translate(prefix + " " + invalidPermissionMessage));
                return true;
            }

            if(isStorming == true){
                player.sendMessage(translate(prefix + " " + weatherAlreadyStormy));
                return true;
            }

            if (economy.getBalance(player) < stormPrice) {
                player.sendMessage(translate(prefix + " " + notEnoughMoneyMessage));
                return true;
            }

            player.getWorld().setStorm(true);
            player.getWorld().setThundering(true);
            economy.withdrawPlayer(player, stormPrice);
            String message = weatherChangedMessage.replace("%price%", currencySymbol + formatDouble(stormPrice)).replace("%weather%", "Storm");
            player.sendMessage(translate(prefix + " " + message));

            // Broadcast message to server
            if(broadcast){
                String broadcastedMessage = broadcastWeatherMessage.replace("%player%", player.getName()).replace("%weather%", "Storm");
                Bukkit.broadcastMessage(translate(prefix + " " + broadcastedMessage));
            }
        } else if (args[0].equalsIgnoreCase("clear")) {
            if (!sender.hasPermission("paytime.clear")) {
                // Player does not have permission to execute command
                sender.sendMessage(translate(prefix + " " + invalidPermissionMessage));
                return true;
            }

            if(!isStorming == true){
                player.sendMessage(translate(prefix + " " + weatherAlreadyClear));
                return true;
            }

            if (economy.getBalance(player) < clearPrice) {
                player.sendMessage(translate(prefix + " " + notEnoughMoneyMessage));
                return true;
            }

            player.getWorld().setStorm(false);
            player.getWorld().setThundering(false);
            economy.withdrawPlayer(player, clearPrice);
            String message = weatherChangedMessage.replace("%price%", currencySymbol + formatDouble(clearPrice)).replace("%weather%", "Clear");
            player.sendMessage(translate(prefix + " " + message));

            // Broadcast message to server
            if(broadcast){
                String broadcastedMessage = broadcastWeatherMessage.replace("%player%", player.getName()).replace("%weather%", "Clear");
                Bukkit.broadcastMessage(translate(prefix + " " + broadcastedMessage));
            }
        } else if (args[0].equalsIgnoreCase("price")) {
            if (!sender.hasPermission("paytime.price")) {
                // Player does not have permission to execute command
                sender.sendMessage(translate(prefix + " " + invalidPermissionMessage));
                return true;
            }

            String message = "Day: &a" + currencySymbol + formatDouble(dayPrice)+ " &rNight: &a" + currencySymbol + formatDouble(nightPrice);

            player.sendMessage(translate(prefix + " " + message));
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
            player.sendMessage(translate(prefix + " " + "Usage: /paytime <day | night | storm | clear | price>"));
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

    public static String formatDouble(double value) {
        if(value >= 1000000000000L){
            return String.format("%.1f", value / 1000000000000L) + "T";
        }else if (value >= 1000000000) {
            return String.format("%.1f", value / 1000000000) + "B";
        } else if (value >= 1000000) {
            return String.format("%.2f", value / 1000000) + "M";
        } else if (value >= 1000) {
            return String.format("%.1f", value / 1000) + "K";
        } else {
            return String.format("%.1f", value);
        }
    }
}
