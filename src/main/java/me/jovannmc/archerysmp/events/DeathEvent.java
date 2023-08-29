package me.jovannmc.archerysmp.events;

import me.jovannmc.archerysmp.ArcherySMP;
import me.jovannmc.archerysmp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEvent implements Listener {
    private final ArcherySMP plugin = ArcherySMP.getPlugin(ArcherySMP.class);

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        // Remove life
        if (plugin.lives.containsKey(player.getUniqueId())) {
            int lives = plugin.lives.get(player.getUniqueId());
            lives--;
            plugin.lives.put(player.getUniqueId(), lives);
            plugin.configUtils.getData().set(player.getUniqueId() + ".lives", lives);
            Utils.sendMessage(player, "&cYou have &4" + lives + " &clives remaining!");

            // Check if player has no lives
            if (lives == 0) {
                // Ban player for 3 days
                plugin.configUtils.getBans().set(player.getUniqueId().toString(), System.currentTimeMillis() + 259200000L);
                plugin.configUtils.saveFile("bans");
                player.kickPlayer(Utils.color("&cYou lost all your lives!\nYou are now banned for 3 days!"));

                long banTime = plugin.configUtils.getBans().getLong(player.getUniqueId().toString());
                long currentTime = System.currentTimeMillis();
                long banTimeHours = (banTime - currentTime) / 3600000L;
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(player.getName(), Utils.color("&cYou lost all your lives!\nYou are banned for " + banTimeHours + " hours!"), null, null);
            }
        }

        // Add life to killer
        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            if (plugin.lives.containsKey(killer.getUniqueId())) {
                int lives = plugin.lives.get(killer.getUniqueId());
                lives++;
                plugin.lives.put(killer.getUniqueId(), lives);
                plugin.configUtils.getData().set(killer.getUniqueId() + ".lives", lives);
                Utils.sendMessage(killer, "&aYou have &2" + lives + " &alives remaining!");
            }
        }

        // Check if archer or hunter
        if (plugin.archers.contains(player.getUniqueId())) {
            plugin.archers.remove(player.getUniqueId());
            Utils.sendMessage(player, "&cYou are no longer an archer!");
            plugin.configUtils.getData().set(player.getUniqueId() + ".role", "player");
            plugin.configUtils.saveFile("data");
        } else if (plugin.hunters.contains(player.getUniqueId())) {
            plugin.hunters.remove(player.getUniqueId());
            Utils.sendMessage(player, "&cYou are no longer a hunter!");
            plugin.configUtils.getData().set(player.getUniqueId() + ".role", "player");
            plugin.configUtils.saveFile("data");
        }

    }
}
