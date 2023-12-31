package me.jovannmc.archerysmp.events;

import me.jovannmc.archerysmp.ArcherySMP;
import me.jovannmc.archerysmp.utils.Utils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
            plugin.configUtils.saveFile("data");
            Utils.sendMessage(player, "&cYou have &4" + lives + " &clives remaining!");

            if (lives == 0) {
                int banLength = plugin.getConfig().getInt("banLength");
                BanList banList = Bukkit.getBanList(BanList.Type.NAME);

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, banLength);
                Date expirationDate = calendar.getTime();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                dateFormat.setTimeZone(TimeZone.getTimeZone("your_timezone_here"));

                String formattedExpiration = dateFormat.format(expirationDate);

                banList.addBan(player.getName(), Utils.color("&cYou lost all your lives!"), expirationDate, null);
                player.kickPlayer(Utils.color("&cYou lost all your lives!\nYou are banned until " + formattedExpiration));
            }
        }

        // Add life to killer
        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            if (plugin.lives.containsKey(killer.getUniqueId())) {
                int lives = plugin.lives.get(killer.getUniqueId());
                int maxLives = plugin.getConfig().getInt("maxLives");
                if (lives >= maxLives) { return; }
                lives++;
                plugin.lives.put(killer.getUniqueId(), lives);
                plugin.configUtils.getData().set(killer.getUniqueId() + ".lives", lives);
                Utils.sendMessage(killer, "&aYou have &2" + lives + " &alives remaining!");
            }
        }

        // Check if archer or hunter
        if (plugin.archers.contains(player.getUniqueId())) {
            plugin.archerHandler.removeArcher(player);
        } else if (plugin.hunters.contains(player.getUniqueId())) {
            plugin.hunterHandler.removeHunter(player);
        }

    }
}
