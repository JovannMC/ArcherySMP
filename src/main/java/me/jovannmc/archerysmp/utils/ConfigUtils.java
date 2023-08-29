package me.jovannmc.archerysmp.utils;

import me.jovannmc.archerysmp.ArcherySMP;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ConfigUtils {
    private final ArcherySMP plugin;

    private File configFile;
    private File dataFile;
    private FileConfiguration config;
    private FileConfiguration data;

    public ConfigUtils(ArcherySMP plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder() + File.separator, "config.yml");
        this.dataFile = new File(plugin.getDataFolder() + File.separator, "data.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void configTasks() {
        if (!configFile.exists() && configFile != null) {
            configFile.getParentFile().mkdirs();
            save(plugin.getResource("config.yml"), configFile);
        }

        if (!dataFile.exists() && dataFile != null	) {
            dataFile.getParentFile().mkdirs();
            save(plugin.getResource("data.yml"), dataFile);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getData() {
        return data;
    }

    public void save(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveFile(String fileName) {
        if (fileName.equalsIgnoreCase("config")) {
            try {
                Bukkit.getLogger().info("Config saved!");
                config.save(configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bukkit.getLogger().info("Config saved!");
        } else if (fileName.equalsIgnoreCase("data")) {
            try {
                Bukkit.getLogger().info("Data saved!");
                data.save(dataFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bukkit.getLogger().info("Data saved!");
        }
    }
}
