package me.jovannmc.archerysmp.utils;

import me.jovannmc.archerysmp.ArcherySMP;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static ArcherySMP plugin = ArcherySMP.getPlugin(ArcherySMP.class);

    public static String color(String string) { return ChatColor.translateAlternateColorCodes('&', string); }

    public static List colorizeList(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String string : list) {
            newList.add(color(string));
        }
        return newList;
    }

    public static void sendMessage(CommandSender sender, String message) { sender.sendMessage(color(message)); }
}
