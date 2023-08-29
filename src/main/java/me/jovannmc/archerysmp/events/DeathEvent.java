package me.jovannmc.archerysmp.events;

import me.jovannmc.archerysmp.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEvent implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        if (Utils.plugin.archers.containsKey(player.getUniqueId())) {
            Utils.plugin.archers.remove(player.getUniqueId());
            Utils.sendMessage(player, "&cYou are no longer an archer!");
        } else if (Utils.plugin.hunters.containsKey(player.getUniqueId())) {
            Utils.plugin.hunters.remove(player.getUniqueId());
            Utils.sendMessage(player, "&cYou are no longer a hunter!");
        }
    }
}
