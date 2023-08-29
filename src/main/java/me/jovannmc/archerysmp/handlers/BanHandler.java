package me.jovannmc.archerysmp.handlers;

import me.jovannmc.archerysmp.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BanHandler implements Listener {
    ConfigUtils configHandler = new ConfigUtils();
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e) {
        if (Bukkit.getBanList(org.bukkit.BanList.Type.NAME).isBanned(e.getPlayer().getName())) {
            // check bans.yml for time
            // check if ban time has passed
            // if ban time has passed, unban player

        }
    }
}
