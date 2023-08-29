package me.jovannmc.archerysmp.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public String color(String string) { return ChatColor.translateAlternateColorCodes('&', string); }

    public List colorizeList(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String string : list) {
            newList.add(color(string));
        }
        return newList;
    }

    public void sendMessage(CommandSender sender, String message) { sender.sendMessage(color(message)); }
}
