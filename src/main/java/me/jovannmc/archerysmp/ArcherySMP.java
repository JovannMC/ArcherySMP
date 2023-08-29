package me.jovannmc.archerysmp;

import me.jovannmc.archerysmp.handlers.ArcherHandler;
import me.jovannmc.archerysmp.handlers.HunterHandler;
import me.jovannmc.archerysmp.utils.ConfigUtils;
import me.jovannmc.archerysmp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class ArcherySMP extends JavaPlugin implements CommandExecutor {
    Utils utils = new Utils();

    ArcherHandler archerHandler = new ArcherHandler();
    HunterHandler hunterHandler = new HunterHandler();

    public HashMap<UUID, Long> archers = new HashMap<>();
    public HashMap<UUID, Long> hunters = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("archer").setExecutor(this);
        Bukkit.getPluginCommand("hunter").setExecutor(this);

        new ConfigUtils().configTasks();
    }

    @Override
    public void onDisable() {
        archers.clear();
        hunters.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("archer")) {
            if (!sender.hasPermission("archerysmp.archer")) { utils.sendMessage(sender, "&cYou don't have permission to use that command."); return false; }
            if (Bukkit.getOnlinePlayers().size() == 0) { utils.sendMessage(sender, "&cThere are no players online."); return false; }

            Player randomPlayer = Bukkit.getOnlinePlayers().stream().skip((int) (Bukkit.getOnlinePlayers().size() * Math.random())).findFirst().orElse(null);
            utils.sendMessage(sender, "&a" + randomPlayer.getName() + " &7is the archer!"); // Makes sure the console receives it too, if executed there
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player != sender || player != randomPlayer) {
                    utils.sendMessage(player, "&a" + randomPlayer.getName() + " &7is the archer!");
                }
            }
            archerHandler.addArcher(randomPlayer);
        } else if (cmd.getName().equals("hunter")) {
            if (!sender.hasPermission("archerysmp.hunter")) { utils.sendMessage(sender, "&cYou don't have permission to use that command."); return false; }
            if (Bukkit.getOnlinePlayers().size() == 0) { utils.sendMessage(sender, "&cThere are no players online."); return false; }

            Player randomPlayer = Bukkit.getOnlinePlayers().stream().skip((int) (Bukkit.getOnlinePlayers().size() * Math.random())).findFirst().orElse(null);
            utils.sendMessage(sender, "&a" + randomPlayer.getName() + " &7is the hunter!"); // Makes sure the console receives it too, if executed there
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player != sender || player != randomPlayer) {
                    utils.sendMessage(player, "&a" + randomPlayer.getName() + " &7is the hunter!");
                }
            }
            hunterHandler.addHunter(randomPlayer);
        }
        return false;
    }
}
