package me.twistedactual.paytime.utils;

import org.bukkit.World;

public class WeatherUtils {
    /**
     * Checks if the weather is clear
     * @param world The world to check
     * @return true if the weather is clear, false otherwise
     */
    public static boolean isClear(World world) {
        return !world.hasStorm() && !world.isThundering();
    }

    /**
     * Checks if the weather is stormy
     * @param world The world to check
     * @return true if the weather is stormy, false otherwise
     */
    public static boolean isStormy(World world) {
        return world.hasStorm() || world.isThundering();
    }

    /**
     * Sets the weather to clear
     * @param world The world to modify
     */
    public static void setClear(World world) {
        world.setStorm(false);
        world.setThundering(false);
    }

    /**
     * Sets the weather to stormy
     * @param world The world to modify
     */
    public static void setStormy(World world) {
        world.setStorm(true);
        world.setThundering(true);
    }

    /**
     * Gets the current weather state as a string
     * @param world The world to check
     * @return "clear" or "stormy"
     */
    public static String getWeatherState(World world) {
        return isClear(world) ? "clear" : "stormy";
    }

    /**
     * Gets the time until the next weather change
     * @param world The world to check
     * @return The time in ticks until the next weather change
     */
    public static long getTimeUntilWeatherChange(World world) {
        return world.getWeatherDuration();
    }

    /**
     * Gets the current weather intensity
     * @param world The world to check
     * @return A value between 0 and 1 representing the weather intensity
     */
    public static float getWeatherIntensity(World world) {
        return world.getWeatherDuration() > 0 ? 1.0f : 0.0f;
    }
} 