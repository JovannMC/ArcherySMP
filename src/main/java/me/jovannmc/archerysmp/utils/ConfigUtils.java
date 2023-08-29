package me.jovannmc.archerysmp.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ConfigUtils {
    private File configFile = new File(Utils.plugin.getDataFolder() + File.separator, "config.yml");
    private File dataFile = new File(Utils.plugin.getDataFolder() + File.separator, "data.yml");
    private File bansFile = new File(Utils.plugin.getDataFolder() + File.separator, "bans.yml");

    private FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    private FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
    private FileConfiguration bans = YamlConfiguration.loadConfiguration(bansFile);

    public void configTasks() {
        if (!configFile.exists() && configFile != null) {
            configFile.getParentFile().mkdirs();
            save(Utils.plugin.getResource("config.yml"), configFile);
        }

        if (!dataFile.exists() && dataFile != null	) {
            dataFile.getParentFile().mkdir();
            save(Utils.plugin.getResource("data.yml"), dataFile);
        }

        if (!bansFile.exists() && bansFile != null	) {
            bansFile.getParentFile().mkdir();
            save(Utils.plugin.getResource("bans.yml"), bansFile);
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
