package me.twistedactual.paytime;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PTTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Base commands for all players
            completions.add("day");
            completions.add("night");
            completions.add("storm");
            completions.add("clear");
            completions.add("price");

            // Admin commands
            if (sender.hasPermission("paytime.admin")) {
                completions.add("reload");
                completions.add("setprice");
                completions.add("setduration");
                completions.add("toggle");
            }

            // Filter based on what the player has typed
            String input = args[0].toLowerCase();
            completions.removeIf(s -> !s.toLowerCase().startsWith(input));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setprice")) {
            // Subcommands for setprice
            completions.add("day");
            completions.add("night");
            completions.add("storm");
            completions.add("clear");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
            // Subcommands for toggle
            completions.add("broadcast");
            completions.add("debug");
        }

        return completions;
    }
} 