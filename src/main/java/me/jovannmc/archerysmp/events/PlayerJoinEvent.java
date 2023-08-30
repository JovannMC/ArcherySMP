package me.jovannmc.archerysmp.events;

import me.jovannmc.archerysmp.ArcherySMP;
import me.jovannmc.archerysmp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoinEvent implements Listener {
    private final ArcherySMP plugin = ArcherySMP.getPlugin(ArcherySMP.class);

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();

        // New player
        if (!plugin.configUtils.getData().contains(player.getUniqueId().toString()) || plugin.configUtils.getData().getInt(player.getUniqueId() + ".lives") == 0) {
            Bukkit.getLogger().info("New player");
            int defaultLives = plugin.getConfig().getInt("defaultLives");
            plugin.lives.put(player.getUniqueId(), defaultLives);
            plugin.configUtils.getData().set(player.getUniqueId() + ".lives", defaultLives);
            plugin.configUtils.getData().set(player.getUniqueId() + ".role", "player");
            Bukkit.getLogger().info("Player lives: " + plugin.lives.get(player.getUniqueId()));
            Bukkit.getLogger().info("Player role: player");
            Bukkit.getLogger().info("defaultLives: " + defaultLives);
            plugin.configUtils.saveFile("data");
            Utils.sendMessage(player, "&aWelcome to the ArcherySMP!");
            Bukkit.getLogger().info("config: " + plugin.getConfig().saveToString());
            Bukkit.getLogger().info("data: " + plugin.configUtils.getData().saveToString());
        }

        // Existing player
        plugin.lives.put(player.getUniqueId(), plugin.configUtils.getData().getInt(player.getUniqueId() + ".lives"));
        Bukkit.getLogger().info("Player lives: " + plugin.lives.get(player.getUniqueId()));
        if (plugin.configUtils.getData().contains(player.getUniqueId() + ".role")) {
            String role = plugin.configUtils.getData().getString(player.getUniqueId() + ".role");
            if (role.equals("archer")) {
                plugin.archerHandler.addArcher(player, false);
                Bukkit.getLogger().info("Player role: archer");
            } else if (role.equals("hunter")) {
                plugin.hunterHandler.addHunter(player, false);
                Bukkit.getLogger().info("Player role: hunter");
            }
        }
    }
}
