package me.jovannmc.archerysmp.utils;

import me.jovannmc.archerysmp.ArcherySMP;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ConfigUtils {
    private JavaPlugin plugin = ArcherySMP.getPlugin(ArcherySMP.class);

    private File configFile = new File(plugin.getDataFolder() + File.separator, "config.yml");
    private File dataFile = new File(plugin.getDataFolder() + File.separator, "data.yml");
    private File bansFile = new File(plugin.getDataFolder() + File.separator, "bans.yml");

    private FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    private FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
    private FileConfiguration bans = YamlConfiguration.loadConfiguration(bansFile);

    public void configTasks() {
        if (!configFile.exists() && configFile != null) {
            configFile.getParentFile().mkdirs();
            save(plugin.getResource("config.yml"), configFile);
        }

        if (!dataFile.exists() && dataFile != null	) {
            dataFile.getParentFile().mkdir();
            save(plugin.getResource("data.yml"), dataFile);
        }

        if (!bansFile.exists() && bansFile != null	) {
            bansFile.getParentFile().mkdir();
            save(plugin.getResource("bans.yml"), bansFile);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getData() {
        return data;
    }
    public FileConfiguration getBans() { return bans; }

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
}
