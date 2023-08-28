package me.jovannmc.archerysmp.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Utils {
    public String color(String string) { return ChatColor.translateAlternateColorCodes('&', string); }

    public void sendMessage(CommandSender sender, String message) { sender.sendMessage(color(message)); }
}
