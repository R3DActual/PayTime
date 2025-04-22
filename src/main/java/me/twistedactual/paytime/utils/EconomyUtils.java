package me.twistedactual.paytime.utils;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class EconomyUtils {
    private final Economy economy;

    public EconomyUtils(Economy economy) {
        this.economy = economy;
    }

    /**
     * Checks if a player has enough money
     * @param player The player to check
     * @param amount The amount to check
     * @return true if the player has enough money, false otherwise
     */
    public boolean hasEnoughMoney(Player player, double amount) {
        return economy.has(player, amount);
    }

    /**
     * Withdraws money from a player
     * @param player The player to withdraw from
     * @param amount The amount to withdraw
     * @return true if the transaction was successful, false otherwise
     */
    public boolean withdrawMoney(Player player, double amount) {
        if (!hasEnoughMoney(player, amount)) {
            return false;
        }
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    /**
     * Deposits money to a player
     * @param player The player to deposit to
     * @param amount The amount to deposit
     * @return true if the transaction was successful, false otherwise
     */
    public boolean depositMoney(Player player, double amount) {
        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    /**
     * Gets a player's balance
     * @param player The player to check
     * @return The player's balance
     */
    public double getBalance(Player player) {
        return economy.getBalance(player);
    }

    /**
     * Formats an amount of money
     * @param amount The amount to format
     * @return The formatted amount
     */
    public String format(double amount) {
        return economy.format(amount);
    }

    /**
     * Gets the currency name plural
     * @return The plural currency name
     */
    public String currencyNamePlural() {
        return economy.currencyNamePlural();
    }

    /**
     * Gets the currency name singular
     * @return The singular currency name
     */
    public String currencyNameSingular() {
        return economy.currencyNameSingular();
    }

    /**
     * Checks if the economy is enabled
     * @return true if the economy is enabled, false otherwise
     */
    public boolean isEnabled() {
        return economy.isEnabled();
    }
} 