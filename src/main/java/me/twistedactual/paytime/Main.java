package me.twistedactual.paytime;

import me.twistedactual.paytime.utils.EconomyUtils;
import me.twistedactual.paytime.utils.MessageUtils;
import me.twistedactual.paytime.utils.TimeUtils;
import me.twistedactual.paytime.utils.WeatherUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public final class Main extends JavaPlugin implements CommandExecutor {

    private EconomyUtils economyUtils;
    private Map<UUID, BukkitTask> weatherTasks;
    private ConfigManager configManager;
    private boolean debug;
    private boolean isEnabled = false;

    @Override
    public void onEnable() {
        // Initialize configuration
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        debug = configManager.getDebugEnabled();
        
        if (debug) {
            log(Level.INFO, "Debug mode enabled - Starting initialization...");
        }

        // Check for Vault
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            log(Level.SEVERE, "Vault is required for this plugin to work!");
            log(Level.SEVERE, "Please install Vault from: https://www.spigotmc.org/resources/vault.34315/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (debug) {
            log(Level.INFO, "Vault dependency found");
        }

        // Initialize Vault economy support
        if (!setupEconomy()) {
            log(Level.SEVERE, "Failed to set up economy support!");
            log(Level.SEVERE, "Please make sure you have an economy plugin installed (e.g., EssentialsX, CMI)");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (debug) {
            log(Level.INFO, "Economy provider found: " + economyUtils.currencyNamePlural());
        }

        // Initialize weather tasks map
        weatherTasks = new HashMap<>();
        if (debug) {
            log(Level.INFO, "Weather tasks map initialized");
        }

        // Register command and tab completer
        getCommand("paytime").setExecutor(this);
        getCommand("paytime").setTabCompleter(new PTTabCompleter());
        if (debug) {
            log(Level.INFO, "Command executor and tab completer registered");
        }

        // Mark plugin as enabled
        isEnabled = true;

        // Log successful enable
        log(Level.INFO, "PayTime has been enabled successfully!");
        if (debug) {
            log(Level.INFO, "Plugin state: ENABLED");
            log(Level.INFO, "Current configuration:");
            log(Level.INFO, "- Day price: " + configManager.getDayPrice());
            log(Level.INFO, "- Night price: " + configManager.getNightPrice());
            log(Level.INFO, "- Storm price: " + configManager.getStormPrice());
            log(Level.INFO, "- Clear price: " + configManager.getClearPrice());
            log(Level.INFO, "- Weather duration: " + configManager.getWeatherDuration() + " ticks");
            log(Level.INFO, "- Broadcast enabled: " + configManager.isBroadcastEnabled());
        }
    }

    @Override
    public void onDisable() {
        if (debug) {
            log(Level.INFO, "Starting plugin shutdown...");
        }

        // Cancel all weather tasks
        if (!weatherTasks.isEmpty()) {
            if (debug) {
                log(Level.INFO, "Cancelling " + weatherTasks.size() + " active weather tasks");
            }
            weatherTasks.values().forEach(BukkitTask::cancel);
            weatherTasks.clear();
        }

        // Save configuration
        configManager.saveConfig();
        if (debug) {
            log(Level.INFO, "Configuration saved");
        }

        // Log successful disable
        log(Level.INFO, "PayTime has been disabled successfully!");
        if (debug) {
            log(Level.INFO, "Plugin state: DISABLED");
        }
    }

    private void log(Level level, String message) {
        if (level.intValue() >= configManager.getLogLevel().intValue()) {
            getLogger().log(level, message);
        }
    }

    private boolean setupEconomy() {
        if (debug) {
            log(Level.INFO, "Setting up economy provider...");
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            if (debug) {
                log(Level.WARNING, "No economy provider found in service manager");
            }
            return false;
        }
        
        Economy economy = rsp.getProvider();
        if (economy == null) {
            if (debug) {
                log(Level.WARNING, "Economy provider is null");
            }
            return false;
        }
        
        economyUtils = new EconomyUtils(economy);
        if (debug) {
            log(Level.INFO, "Economy provider setup complete");
            log(Level.INFO, "Provider details:");
            log(Level.INFO, "- Name: " + economy.getName());
            log(Level.INFO, "- Currency: " + economy.currencyNamePlural());
            log(Level.INFO, "- Enabled: " + economy.isEnabled());
        }
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (debug) {
            log(Level.INFO, "Command received from " + sender.getName() + ": /" + label + " " + String.join(" ", args));
        }

        if (!isEnabled) {
            MessageUtils.sendMessage(sender, configManager.getPrefix(), "&cThe plugin is currently disabled. Please check the console for error messages.");
            if (debug) {
                log(Level.WARNING, "Command attempted while plugin is disabled");
            }
            return true;
        }

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        // Admin commands
        if (args[0].equalsIgnoreCase("setprice") || args[0].equalsIgnoreCase("setduration") || 
            args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("paytime.admin")) {
                MessageUtils.sendMessage(sender, configManager.getPrefix(), configManager.getInvalidPermissionMessage());
                if (debug) {
                    log(Level.INFO, "Admin command attempted without permission by: " + sender.getName());
                }
                return true;
            }
        }

        switch (args[0].toLowerCase()) {
            case "day":
            case "night":
            case "storm":
            case "clear":
            case "price":
                if (!(sender instanceof Player)) {
                    MessageUtils.sendMessage(sender, configManager.getPrefix(), "This command can only be used by a player.");
                    if (debug) {
                        log(Level.WARNING, "Non-player attempted to use command: " + sender.getName());
                    }
                    return true;
                }
                handlePlayerCommand((Player) sender, args[0]);
                break;
            case "setprice":
                handleSetPrice(sender, args);
                break;
            case "setduration":
                handleSetDuration(sender, args);
                break;
            case "toggle":
                handleToggle(sender, args);
                break;
            case "reload":
                handleReload(sender);
                break;
            default:
                showHelp(sender);
                break;
        }

        return true;
    }

    private void showHelp(CommandSender sender) {
        List<String> helpMessages = new ArrayList<>();
        helpMessages.add("&6&lPayTime Help");
        helpMessages.add("&e/paytime day &7- Change time to day");
        helpMessages.add("&e/paytime night &7- Change time to night");
        helpMessages.add("&e/paytime storm &7- Change weather to storm");
        helpMessages.add("&e/paytime clear &7- Change weather to clear");
        helpMessages.add("&e/paytime price &7- Show current prices");

        if (sender.hasPermission("paytime.admin")) {
            helpMessages.add("&6&lAdmin Commands");
            helpMessages.add("&e/paytime setprice <type> <amount> &7- Set price for time/weather change");
            helpMessages.add("&e/paytime setduration <ticks> &7- Set weather duration");
            helpMessages.add("&e/paytime toggle <broadcast|debug> &7- Toggle features");
            helpMessages.add("&e/paytime reload &7- Reload configuration");
        }

        helpMessages.forEach(msg -> MessageUtils.sendMessage(sender, "", msg));
    }

    private void handlePlayerCommand(Player player, String command) {
        World world = player.getWorld();
        if (debug) {
            log(Level.INFO, "Processing command for world: " + world.getName());
            log(Level.INFO, "Current world state:");
            log(Level.INFO, "- Time: " + world.getTime());
            log(Level.INFO, "- Storm: " + world.hasStorm());
            log(Level.INFO, "- Thunder: " + world.isThundering());
        }

        switch (command.toLowerCase()) {
            case "day":
                handleTimeChange(player, "day", TimeUtils.isDay(world), configManager.getDayPrice());
                break;
            case "night":
                handleTimeChange(player, "night", TimeUtils.isNight(world), configManager.getNightPrice());
                break;
            case "storm":
                handleWeatherChange(player, "storm", WeatherUtils.isStormy(world), configManager.getStormPrice());
                break;
            case "clear":
                handleWeatherChange(player, "clear", WeatherUtils.isClear(world), configManager.getClearPrice());
                break;
            case "price":
                showPrices(player);
                break;
        }
    }

    private void handleSetPrice(CommandSender sender, String[] args) {
        if (args.length != 3) {
            MessageUtils.sendMessage(sender, configManager.getPrefix(), "Usage: /paytime setprice <day|night|storm|clear> <amount>");
            return;
        }

        try {
            double price = Double.parseDouble(args[2]);
            if (price < 0) {
                MessageUtils.sendMessage(sender, configManager.getPrefix(), "&cPrice cannot be negative!");
                return;
            }

            switch (args[1].toLowerCase()) {
                case "day":
                    configManager.setDayPrice(price);
                    break;
                case "night":
                    configManager.setNightPrice(price);
                    break;
                case "storm":
                    configManager.setStormPrice(price);
                    break;
                case "clear":
                    configManager.setClearPrice(price);
                    break;
                default:
                    MessageUtils.sendMessage(sender, configManager.getPrefix(), "&cInvalid type! Use: day, night, storm, or clear");
                    return;
            }

            MessageUtils.sendMessage(sender, configManager.getPrefix(), 
                "&aPrice for " + args[1] + " set to " + MessageUtils.formatPrice(price, configManager.getCurrencySymbol()));
            if (debug) {
                log(Level.INFO, "Price updated for " + args[1] + ": " + price);
            }
        } catch (NumberFormatException e) {
            MessageUtils.sendMessage(sender, configManager.getPrefix(), "&cInvalid amount! Please enter a valid number.");
        }
    }

    private void handleSetDuration(CommandSender sender, String[] args) {
        if (args.length != 2) {
            MessageUtils.sendMessage(sender, configManager.getPrefix(), "Usage: /paytime setduration <ticks>");
            return;
        }

        try {
            long duration = Long.parseLong(args[1]);
            if (duration < 0) {
                MessageUtils.sendMessage(sender, configManager.getPrefix(), "&cDuration cannot be negative!");
                return;
            }

            configManager.setWeatherDuration(duration);
            MessageUtils.sendMessage(sender, configManager.getPrefix(), 
                "&aWeather duration set to " + duration + " ticks (" + TimeUtils.ticksToMinutes(duration) + " minutes)");
            if (debug) {
                log(Level.INFO, "Weather duration updated: " + duration + " ticks");
            }
        } catch (NumberFormatException e) {
            MessageUtils.sendMessage(sender, configManager.getPrefix(), "&cInvalid duration! Please enter a valid number.");
        }
    }

    private void handleToggle(CommandSender sender, String[] args) {
        if (args.length != 2) {
            MessageUtils.sendMessage(sender, configManager.getPrefix(), "Usage: /paytime toggle <broadcast|debug>");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "broadcast":
                boolean newBroadcastState = !configManager.isBroadcastEnabled();
                configManager.setBroadcastEnabled(newBroadcastState);
                MessageUtils.sendMessage(sender, configManager.getPrefix(), 
                    "&aBroadcast messages " + (newBroadcastState ? "&aenabled" : "&cdisabled"));
                if (debug) {
                    log(Level.INFO, "Broadcast messages toggled: " + newBroadcastState);
                }
                break;
            case "debug":
                boolean newDebugState = !configManager.getDebugEnabled();
                configManager.setDebugEnabled(newDebugState);
                debug = newDebugState;
                MessageUtils.sendMessage(sender, configManager.getPrefix(), 
                    "&aDebug mode " + (newDebugState ? "&aenabled" : "&cdisabled"));
                if (debug) {
                    log(Level.INFO, "Debug mode toggled: " + newDebugState);
                }
                break;
            default:
                MessageUtils.sendMessage(sender, configManager.getPrefix(), "&cInvalid option! Use: broadcast or debug");
                break;
        }
    }

    private void handleReload(CommandSender sender) {
        if (debug) {
            log(Level.INFO, "Reloading configuration...");
        }
        reloadConfig();
        configManager.loadConfig();
        debug = configManager.getDebugEnabled();
        MessageUtils.sendMessage(sender, configManager.getPrefix(), configManager.getConfigReloadMessage());
        if (debug) {
            log(Level.INFO, "Configuration reloaded successfully");
        }
    }

    private void handleTimeChange(Player player, String timeType, boolean alreadySet, double price) {
        if (debug) {
            log(Level.INFO, "Processing time change request:");
            log(Level.INFO, "- Player: " + player.getName());
            log(Level.INFO, "- Type: " + timeType);
            log(Level.INFO, "- Price: " + price);
            log(Level.INFO, "- Already set: " + alreadySet);
        }

        if (!player.hasPermission("paytime." + timeType)) {
            MessageUtils.sendMessage(player, configManager.getPrefix(), configManager.getInvalidPermissionMessage());
            if (debug) {
                log(Level.INFO, "Permission check failed for: paytime." + timeType);
            }
            return;
        }

        if (alreadySet) {
            MessageUtils.sendMessage(player, configManager.getPrefix(), 
                (timeType.equals("day") ? configManager.getTimeAlreadyDayMessage() : configManager.getTimeAlreadyNightMessage()));
            if (debug) {
                log(Level.INFO, "Time change rejected - already set to requested time");
            }
            return;
        }

        if (!economyUtils.hasEnoughMoney(player, price)) {
            MessageUtils.sendMessage(player, configManager.getPrefix(), configManager.getNotEnoughMoneyMessage());
            if (debug) {
                log(Level.INFO, "Transaction failed - insufficient funds");
                log(Level.INFO, "- Required: " + price);
                log(Level.INFO, "- Balance: " + economyUtils.getBalance(player));
            }
            return;
        }

        player.getWorld().setTime(timeType.equals("day") ? configManager.getDayTime() : configManager.getNightTime());
        economyUtils.withdrawMoney(player, price);

        if (debug) {
            log(Level.INFO, "Time change successful:");
            log(Level.INFO, "- New time: " + player.getWorld().getTime());
            log(Level.INFO, "- Amount withdrawn: " + price);
            log(Level.INFO, "- New balance: " + economyUtils.getBalance(player));
        }

        String message = configManager.getTimeChangedMessage()
            .replace("%price%", MessageUtils.formatPrice(price, configManager.getCurrencySymbol()))
            .replace("%time%", timeType.substring(0, 1).toUpperCase() + timeType.substring(1));
        MessageUtils.sendMessage(player, configManager.getPrefix(), message);

        if (configManager.isBroadcastEnabled()) {
            String broadcastMessage = configManager.getBroadcastTimeMessage()
                .replace("%player%", player.getName())
                .replace("%time%", timeType.substring(0, 1).toUpperCase() + timeType.substring(1));
            Bukkit.broadcastMessage(MessageUtils.translate(configManager.getPrefix() + " " + broadcastMessage));
            if (debug) {
                log(Level.INFO, "Broadcast message sent");
            }
        }
    }

    private void handleWeatherChange(Player player, String weatherType, boolean alreadySet, double price) {
        if (debug) {
            log(Level.INFO, "Processing weather change request:");
            log(Level.INFO, "- Player: " + player.getName());
            log(Level.INFO, "- Type: " + weatherType);
            log(Level.INFO, "- Price: " + price);
            log(Level.INFO, "- Already set: " + alreadySet);
        }

        if (!player.hasPermission("paytime." + weatherType)) {
            MessageUtils.sendMessage(player, configManager.getPrefix(), configManager.getInvalidPermissionMessage());
            if (debug) {
                log(Level.INFO, "Permission check failed for: paytime." + weatherType);
            }
            return;
        }

        if (alreadySet) {
            MessageUtils.sendMessage(player, configManager.getPrefix(), 
                (weatherType.equals("clear") ? configManager.getWeatherAlreadyClearMessage() : configManager.getWeatherAlreadyStormyMessage()));
            if (debug) {
                log(Level.INFO, "Weather change rejected - already set to requested weather");
            }
            return;
        }

        if (!economyUtils.hasEnoughMoney(player, price)) {
            MessageUtils.sendMessage(player, configManager.getPrefix(), configManager.getNotEnoughMoneyMessage());
            if (debug) {
                log(Level.INFO, "Transaction failed - insufficient funds");
                log(Level.INFO, "- Required: " + price);
                log(Level.INFO, "- Balance: " + economyUtils.getBalance(player));
            }
            return;
        }

        World world = player.getWorld();
        if (weatherType.equals("storm")) {
            WeatherUtils.setStormy(world);
        } else {
            WeatherUtils.setClear(world);
        }

        economyUtils.withdrawMoney(player, price);

        if (debug) {
            log(Level.INFO, "Weather change successful:");
            log(Level.INFO, "- New weather state: " + WeatherUtils.getWeatherState(world));
            log(Level.INFO, "- Amount withdrawn: " + price);
            log(Level.INFO, "- New balance: " + economyUtils.getBalance(player));
        }

        String message = configManager.getWeatherChangedMessage()
            .replace("%price%", MessageUtils.formatPrice(price, configManager.getCurrencySymbol()))
            .replace("%weather%", weatherType.substring(0, 1).toUpperCase() + weatherType.substring(1));
        MessageUtils.sendMessage(player, configManager.getPrefix(), message);

        if (configManager.isBroadcastEnabled()) {
            String broadcastMessage = configManager.getBroadcastWeatherMessage()
                .replace("%player%", player.getName())
                .replace("%weather%", weatherType.substring(0, 1).toUpperCase() + weatherType.substring(1));
            Bukkit.broadcastMessage(MessageUtils.translate(configManager.getPrefix() + " " + broadcastMessage));
            if (debug) {
                log(Level.INFO, "Broadcast message sent");
            }
        }

        // Handle weather duration if configured
        if (configManager.getWeatherDuration() > 0) {
            UUID worldId = world.getUID();
            if (weatherTasks.containsKey(worldId)) {
                if (debug) {
                    log(Level.INFO, "Cancelling existing weather task for world: " + world.getName());
                }
                weatherTasks.get(worldId).cancel();
            }

            BukkitTask task = Bukkit.getScheduler().runTaskLater(this, () -> {
                if (debug) {
                    log(Level.INFO, "Weather duration expired for world: " + world.getName());
                }
                if (weatherType.equals("storm")) {
                    WeatherUtils.setClear(world);
                } else {
                    WeatherUtils.setStormy(world);
                }
                weatherTasks.remove(worldId);
                if (debug) {
                    log(Level.INFO, "Weather reverted to original state");
                }
            }, configManager.getWeatherDuration());

            weatherTasks.put(worldId, task);
            if (debug) {
                log(Level.INFO, "Weather task scheduled for " + configManager.getWeatherDuration() + " ticks");
            }
        }
    }

    private void showPrices(Player player) {
        if (debug) {
            log(Level.INFO, "Price check requested by: " + player.getName());
        }

        if (!player.hasPermission("paytime.price")) {
            MessageUtils.sendMessage(player, configManager.getPrefix(), configManager.getInvalidPermissionMessage());
            if (debug) {
                log(Level.INFO, "Permission check failed for: paytime.price");
            }
            return;
        }

        String message = String.format("Day: &a%s &rNight: &a%s &rStorm: &a%s &rClear: &a%s",
            MessageUtils.formatPrice(configManager.getDayPrice(), configManager.getCurrencySymbol()),
            MessageUtils.formatPrice(configManager.getNightPrice(), configManager.getCurrencySymbol()),
            MessageUtils.formatPrice(configManager.getStormPrice(), configManager.getCurrencySymbol()),
            MessageUtils.formatPrice(configManager.getClearPrice(), configManager.getCurrencySymbol()));

        MessageUtils.sendMessage(player, configManager.getPrefix(), message);
        if (debug) {
            log(Level.INFO, "Prices displayed to player");
        }
    }
}
