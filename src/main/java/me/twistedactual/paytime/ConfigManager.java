package me.twistedactual.paytime;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;
    private boolean debug;
    private Level logLevel;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        createDefaultConfig();
        loadConfig();
    }

    private void createDefaultConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!configFile.exists()) {
            plugin.getLogger().info("Creating default config.yml...");
            try {
                configFile.createNewFile();
                config = YamlConfiguration.loadConfiguration(configFile);
                setDefaults();
                saveConfig();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create config.yml!");
                e.printStackTrace();
            }
        }
    }

    private void setDefaults() {
        // Price Settings
        config.set("prices.day", 10000);
        config.set("prices.night", 1000);
        config.set("prices.storm", 10000);
        config.set("prices.clear", 1000);

        // Message Settings
        config.set("messages.prefix", "%#808080%[%#FFA500%PayTime%#808080%]%#FFFFFF%");
        config.set("messages.currency_symbol", "$");
        
        // Broadcast settings
        config.set("messages.broadcast.enabled", true);
        config.set("messages.broadcast.time", "%#FFFF00%{player}%#FFFFFF% has paid to change the time to %#FFFF00%{time}%#FFFFFF%.");
        config.set("messages.broadcast.weather", "%#FFFF00%{player}%#FFFFFF% has paid to change the weather to %#FFFF00%{weather}%#FFFFFF%.");
        
        // Error messages
        config.set("messages.errors.not_enough_money", "%#FF0000%You do not have enough money to change the time.");
        config.set("messages.errors.invalid_permission", "%#FF0000%You do not have permission to execute this command.");
        config.set("messages.errors.already_day", "%#FF0000%The time is already day.");
        config.set("messages.errors.already_night", "%#FF0000%The time is already night.");
        config.set("messages.errors.already_clear", "%#FF0000%The weather is already clear.");
        config.set("messages.errors.already_stormy", "%#FF0000%The weather is already stormy.");
        
        // Success messages
        config.set("messages.success.time_changed", "%#00FF00%You have paid {price} to change the time to %#FFFF00%{time}%#FFFFFF%.");
        config.set("messages.success.weather_changed", "%#00FF00%You have paid {price} to change the weather to %#FFFF00%{weather}%#FFFFFF%.");
        config.set("messages.success.config_reload", "%#00FF00%Configuration reloaded successfully.");

        // Time Settings
        config.set("time.day", 0);
        config.set("time.night", 14000);

        // Weather Settings
        config.set("weather.duration", -1);

        // Feature Settings
        config.set("features.time_changes", true);
        config.set("features.weather_changes", true);
        config.set("features.price_command", true);
        config.set("features.reload_command", true);

        // Debug Settings
        config.set("debug.enabled", false);
        config.set("debug.log_level", "INFO");
    }

    public void loadConfig() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            debug = config.getBoolean("debug.enabled");
            logLevel = parseLogLevel(config.getString("debug.log_level"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not load config.yml!");
            e.printStackTrace();
        }
    }

    private Level parseLogLevel(String level) {
        try {
            return Level.parse(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid log level '" + level + "'. Using default level INFO.");
            return Level.INFO;
        }
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config.yml!");
            e.printStackTrace();
        }
    }

    // Price getters
    public double getDayPrice() {
        return config.getDouble("prices.day");
    }

    public double getNightPrice() {
        return config.getDouble("prices.night");
    }

    public double getStormPrice() {
        return config.getDouble("prices.storm");
    }

    public double getClearPrice() {
        return config.getDouble("prices.clear");
    }

    // Message getters
    public String getPrefix() {
        return config.getString("messages.prefix");
    }

    public String getCurrencySymbol() {
        return config.getString("messages.currency_symbol");
    }

    public boolean isBroadcastEnabled() {
        return config.getBoolean("messages.broadcast.enabled");
    }

    public String getBroadcastTimeMessage() {
        return config.getString("messages.broadcast.time");
    }

    public String getBroadcastWeatherMessage() {
        return config.getString("messages.broadcast.weather");
    }

    public String getNotEnoughMoneyMessage() {
        return config.getString("messages.errors.not_enough_money");
    }

    public String getInvalidPermissionMessage() {
        return config.getString("messages.errors.invalid_permission");
    }

    public String getTimeAlreadyDayMessage() {
        return config.getString("messages.errors.already_day");
    }

    public String getTimeAlreadyNightMessage() {
        return config.getString("messages.errors.already_night");
    }

    public String getWeatherAlreadyClearMessage() {
        return config.getString("messages.errors.already_clear");
    }

    public String getWeatherAlreadyStormyMessage() {
        return config.getString("messages.errors.already_stormy");
    }

    public String getTimeChangedMessage() {
        return config.getString("messages.success.time_changed");
    }

    public String getWeatherChangedMessage() {
        return config.getString("messages.success.weather_changed");
    }

    public String getConfigReloadMessage() {
        return config.getString("messages.success.config_reload");
    }

    // Time getters
    public long getDayTime() {
        return config.getLong("time.day");
    }

    public long getNightTime() {
        return config.getLong("time.night");
    }

    // Weather getters
    public long getWeatherDuration() {
        return config.getLong("weather.duration");
    }

    // Feature getters
    public boolean isTimeChangesEnabled() {
        return config.getBoolean("features.time_changes");
    }

    public boolean isWeatherChangesEnabled() {
        return config.getBoolean("features.weather_changes");
    }

    public boolean isPriceCommandEnabled() {
        return config.getBoolean("features.price_command");
    }

    public boolean isReloadCommandEnabled() {
        return config.getBoolean("features.reload_command");
    }

    // Debug getters
    public boolean getDebugEnabled() {
        return debug;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    // Setters
    public void setDayPrice(double price) {
        config.set("prices.day", price);
        saveConfig();
    }

    public void setNightPrice(double price) {
        config.set("prices.night", price);
        saveConfig();
    }

    public void setStormPrice(double price) {
        config.set("prices.storm", price);
        saveConfig();
    }

    public void setClearPrice(double price) {
        config.set("prices.clear", price);
        saveConfig();
    }

    public void setWeatherDuration(long duration) {
        config.set("weather.duration", duration);
        saveConfig();
    }

    public void setBroadcastEnabled(boolean enabled) {
        config.set("messages.broadcast.enabled", enabled);
        saveConfig();
    }

    public void setDebugEnabled(boolean enabled) {
        config.set("debug.enabled", enabled);
        debug = enabled;
        saveConfig();
    }
} 