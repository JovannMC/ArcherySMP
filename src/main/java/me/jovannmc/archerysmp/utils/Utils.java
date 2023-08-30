package me.jovannmc.archerysmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String color(String string) { return ChatColor.translateAlternateColorCodes('&', string); }

    public static List colorizeList(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String string : list) {
            newList.add(color(string));
        }
        return newList;
    }

    public static void sendMessage(CommandSender sender, String message) { sender.sendMessage(color(message)); }
    public static void announceMessage(String message) { Bukkit.broadcastMessage(color(message)); }

    public static BossBar createBossBar(String title, BarColor color, BarStyle style) {
        BossBar bossBar = Bukkit.createBossBar(title, color, style);
        bossBar.setVisible(true);
        return bossBar;
    }
}
