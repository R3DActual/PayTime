package me.twistedactual.paytime.utils;

import org.bukkit.World;

public class TimeUtils {
    // Minecraft time constants
    public static final long DAY_START = 0;
    public static final long DAY_END = 12000;
    public static final long NIGHT_START = 13000;
    public static final long NIGHT_END = 23000;
    public static final long FULL_DAY = 24000;

    /**
     * Checks if the current time is day
     * @param world The world to check
     * @return true if it's day, false otherwise
     */
    public static boolean isDay(World world) {
        long time = world.getTime();
        return time >= DAY_START && time < DAY_END;
    }

    /**
     * Checks if the current time is night
     * @param world The world to check
     * @return true if it's night, false otherwise
     */
    public static boolean isNight(World world) {
        long time = world.getTime();
        return time >= NIGHT_START && time < NIGHT_END;
    }

    /**
     * Gets the time until the next day/night transition
     * @param world The world to check
     * @return The time in ticks until the next transition
     */
    public static long getTimeUntilTransition(World world) {
        long time = world.getTime();
        if (isDay(world)) {
            return DAY_END - time;
        } else {
            return (time < NIGHT_START) ? NIGHT_START - time : FULL_DAY - time + DAY_START;
        }
    }

    /**
     * Gets the time until the next weather transition
     * @param world The world to check
     * @return The time in ticks until the next weather transition
     */
    public static long getTimeUntilWeatherTransition(World world) {
        return world.getWeatherDuration();
    }

    /**
     * Converts real-world seconds to Minecraft ticks
     * @param seconds The number of seconds
     * @return The equivalent number of Minecraft ticks
     */
    public static long secondsToTicks(long seconds) {
        return seconds * 20;
    }

    /**
     * Converts Minecraft ticks to real-world seconds
     * @param ticks The number of ticks
     * @return The equivalent number of seconds
     */
    public static long ticksToSeconds(long ticks) {
        return ticks / 20;
    }

    /**
     * Converts real-world minutes to Minecraft ticks
     * @param minutes The number of minutes
     * @return The equivalent number of Minecraft ticks
     */
    public static long minutesToTicks(long minutes) {
        return secondsToTicks(minutes * 60);
    }

    /**
     * Converts Minecraft ticks to real-world minutes
     * @param ticks The number of ticks
     * @return The equivalent number of minutes
     */
    public static long ticksToMinutes(long ticks) {
        return ticksToSeconds(ticks) / 60;
    }
} 