package me.jovannmc.archerysmp;

import me.jovannmc.archerysmp.commands.ArcherCommand;
import me.jovannmc.archerysmp.commands.HunterCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ArcherySMP extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("archer").setExecutor(new ArcherCommand());
        Bukkit.getPluginCommand("hunter").setExecutor(new HunterCommand());


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
