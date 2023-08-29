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

        if (Bukkit.getBanList(org.bukkit.BanList.Type.NAME).isBanned(e.getPlayer().getName())) {
            if (plugin.configUtils.getBans().contains(player.getUniqueId().toString())) {
                long banTime = plugin.configUtils.getBans().getLong(player.getUniqueId().toString());
                long currentTime = System.currentTimeMillis();
                if (currentTime > banTime) {
                    Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(player.getName());
                    plugin.configUtils.getBans().set(player.getUniqueId().toString(), null);
                    plugin.configUtils.saveFile("bans");
                } else {
                    // get ban time, which is in epoch time, and convert it to hours
                    long banTimeHours = (banTime - currentTime) / 3600000L;
                    player.kickPlayer(Utils.color("&cYou lost all your lives!\nYou are banned for " + banTimeHours + " hours!"));
                    return;
                }
            }
            return;
        }

        // New player
        if (!plugin.configUtils.getData().contains(player.getUniqueId().toString()) || plugin.configUtils.getData().getInt(player.getUniqueId() + ".lives") == 0) {
            plugin.lives.put(player.getUniqueId(), 5);
            plugin.configUtils.getData().set(player.getUniqueId() + ".lives", 5);
            plugin.configUtils.getData().set(player.getUniqueId() + ".role", "player");
            plugin.configUtils.saveFile("data");
            Utils.sendMessage(player, "&aWelcome to ArcherySMP!");
        }

        // Existing player
        plugin.lives.put(player.getUniqueId(), plugin.configUtils.getData().getInt(player.getUniqueId() + ".lives"));
        Bukkit.getLogger().info("Player lives: " + plugin.lives.get(player.getUniqueId()));
        if (plugin.configUtils.getData().contains(player.getUniqueId() + ".role")) {
            String role = plugin.configUtils.getData().getString(player.getUniqueId() + ".role");
            if (role.equals("archer")) {
                plugin.archerHandler.addArcher(player);
                Bukkit.getLogger().info("Player role: archer");
            } else if (role.equals("hunter")) {
                plugin.hunterHandler.addHunter(player);
                Bukkit.getLogger().info("Player role: hunter");
            }
        }
    }
}
