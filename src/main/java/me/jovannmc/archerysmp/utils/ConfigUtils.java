package me.jovannmc.archerysmp.utils;

import me.jovannmc.archerysmp.ArcherySMP;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigUtils {
    private final ArcherySMP plugin;

    private File dataFile;
    private FileConfiguration data;

    public ConfigUtils(ArcherySMP plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder() + File.separator, "data.yml");
        this.data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void setupDataConfig() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            plugin.saveResource("data.yml", false);
        }

        data = new YamlConfiguration();
        try {
            data.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveFile(String fileName) {
        if (fileName.equalsIgnoreCase("data")) {
            try {
                Bukkit.getLogger().info("data.yml saved!");
                data.save(dataFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration getData() {
        return data;
    }
}
