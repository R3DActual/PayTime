package me.twistedactual.paytime.utils;

import me.twistedactual.paytime.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {
    private static final char COLOR_CHAR = '\u00A7';
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,##0.00");
    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>");
    private static ConfigManager config;

    public static void setConfig(ConfigManager configManager) {
        config = configManager;
    }

    /**
     * Translates color codes in a string
     * @param string The string to translate
     * @return The translated string with color codes
     */
    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', translateHexColorCodes(string));
    }

    /**
     * Translates hex color codes in a string
     * @param message The message containing hex color codes
     * @return The message with translated hex color codes
     */
    public static String translateHexColorCodes(String message) {
        Pattern pattern = Pattern.compile("<#[a-fA-F0-9]{6}>");
        Matcher matcher = pattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group();
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
                + COLOR_CHAR + group.charAt(6) + COLOR_CHAR + group.charAt(7)
            );
        }
        return matcher.appendTail(buffer).toString();
    }

    /**
     * Formats a price value with the specified currency symbol
     * @param value The price value
     * @param currencySymbol The currency symbol
     * @return The formatted price string
     */
    public static String formatPrice(double value, String currencySymbol) {
        return currencySymbol + PRICE_FORMAT.format(value);
    }

    /**
     * Sends a message to a command sender with the specified prefix
     * @param sender The command sender
     * @param prefix The message prefix
     * @param message The message to send
     */
    public static void sendMessage(CommandSender sender, String prefix, String message) {
        sender.sendMessage(translate(prefix + " " + message));
    }

    /**
     * Sends a message to a command sender
     * @param sender The command sender
     * @param message The message to send
     */
    public static void sendMessage(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;
        sender.sendMessage(translateHexColors(message));
    }

    /**
     * Formats a time value into a readable string
     * @param ticks The time in ticks
     * @return A formatted time string
     */
    public static String formatTime(long ticks) {
        long hours = (ticks / 1000 + 6) % 24;
        long minutes = (ticks % 1000) * 60 / 1000;
        return String.format("%02d:%02d", hours, minutes);
    }

    /**
     * Formats a duration in ticks into a readable string
     * @param ticks The duration in ticks
     * @return A formatted duration string
     */
    public static String formatDuration(long ticks) {
        if (ticks < 0) return "permanent";
        
        long seconds = ticks / 20;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%d hour%s", hours, hours != 1 ? "s" : "");
        } else if (minutes > 0) {
            return String.format("%d minute%s", minutes, minutes != 1 ? "s" : "");
        } else {
            return String.format("%d second%s", seconds, seconds != 1 ? "s" : "");
        }
    }

    public static void sendMessage(CommandSender sender, String message, String... placeholders) {
        if (message == null || message.isEmpty()) return;
        String formattedMessage = formatMessage(message, placeholders);
        sender.sendMessage(translateHexColors(formattedMessage));
    }

    public static void broadcastMessage(String message) {
        if (message == null || message.isEmpty()) return;
        org.bukkit.Bukkit.broadcastMessage(translateHexColors(message));
    }

    public static void broadcastMessage(String message, String... placeholders) {
        if (message == null || message.isEmpty()) return;
        String formattedMessage = formatMessage(message, placeholders);
        org.bukkit.Bukkit.broadcastMessage(translateHexColors(formattedMessage));
    }

    private static String formatMessage(String message, String... placeholders) {
        if (placeholders == null || placeholders.length % 2 != 0) {
            return message;
        }

        String formattedMessage = message;
        for (int i = 0; i < placeholders.length; i += 2) {
            String placeholder = placeholders[i];
            String value = placeholders[i + 1];
            formattedMessage = formattedMessage.replace("{" + placeholder + "}", value);
        }

        return formattedMessage;
    }

    private static String translateHexColors(String message) {
        if (message == null) return "";

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.COLOR_CHAR + "x" + 
                ChatColor.COLOR_CHAR + hex.charAt(0) + ChatColor.COLOR_CHAR + hex.charAt(1) +
                ChatColor.COLOR_CHAR + hex.charAt(2) + ChatColor.COLOR_CHAR + hex.charAt(3) +
                ChatColor.COLOR_CHAR + hex.charAt(4) + ChatColor.COLOR_CHAR + hex.charAt(5));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }
} 