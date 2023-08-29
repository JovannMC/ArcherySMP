package me.jovannmc.archerysmp;

import me.jovannmc.archerysmp.events.DeathEvent;
import me.jovannmc.archerysmp.events.PlayerJoinEvent;
import me.jovannmc.archerysmp.events.SelectedItemPickupEvent;
import me.jovannmc.archerysmp.handlers.ArcherHandler;
import me.jovannmc.archerysmp.handlers.HunterHandler;
import me.jovannmc.archerysmp.utils.ConfigUtils;
import me.jovannmc.archerysmp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class ArcherySMP extends JavaPlugin implements CommandExecutor {
    public ArrayList<UUID> archers = new ArrayList<>();
    public ArrayList<UUID> hunters = new ArrayList<>();
    public HashMap<UUID, Integer> lives = new HashMap<>();
    private NamespacedKey roleKey;

    public ConfigUtils configUtils = new ConfigUtils(this);
    public ArcherHandler archerHandler = new ArcherHandler(this);
    public HunterHandler hunterHandler = new HunterHandler(this);

    @Override
    public void onEnable() {
        // Register commands
        Bukkit.getPluginCommand("archer").setExecutor(this);
        Bukkit.getPluginCommand("hunter").setExecutor(this);

        // Register events
        Bukkit.getPluginManager().registerEvents(archerHandler, this);
        Bukkit.getPluginManager().registerEvents(hunterHandler, this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new SelectedItemPickupEvent(), this);
        Bukkit.getPluginManager().registerEvents(new DeathEvent(), this);

        roleKey = new NamespacedKey(this, "role");

        configUtils.configTasks();
    }

    @Override
    public void onDisable() {
        archers.clear();
        hunters.clear();
        lives.clear();

        Bukkit.getScheduler().cancelTasks(this);

        configUtils.saveFile("data");
        configUtils.saveFile("bans");
        configUtils.saveFile("config");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("archer")) {
            if (!sender.hasPermission("archerysmp.archer")) { Utils.sendMessage(sender, "&cYou don't have permission to use that command."); return false; }
            if (Bukkit.getOnlinePlayers().size() == 0) { Utils.sendMessage(sender, "&cThere are no players online."); return false; }

            Player randomPlayer = Bukkit.getOnlinePlayers().stream().skip((int) (Bukkit.getOnlinePlayers().size() * Math.random())).findFirst().orElse(null);
            archerHandler.addArcher(randomPlayer);
        } else if (cmd.getName().equals("hunter")) {
            if (!sender.hasPermission("archerysmp.hunter")) { Utils.sendMessage(sender, "&cYou don't have permission to use that command."); return false; }
            if (Bukkit.getOnlinePlayers().size() == 0) { Utils.sendMessage(sender, "&cThere are no players online."); return false; }

            Player randomPlayer = Bukkit.getOnlinePlayers().stream().skip((int) (Bukkit.getOnlinePlayers().size() * Math.random())).findFirst().orElse(null);
            hunterHandler.addHunter(randomPlayer);
        }
        return false;
    }

    public NamespacedKey getRoleKey() {
        return roleKey;
    }
}
