package dev.o8o1o5.myEconomy.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DataManager {
    private final File file;
    private final FileConfiguration config;

    public DataManager(JavaPlugin plugin) {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        this.file = new File(plugin.getDataFolder(), "userdata.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public double getBalance(UUID uuid) {
        return config.getDouble(uuid.toString(), 0.0);
    }

    public void setBalance(UUID uuid, double amount) {
        config.set(uuid.toString(), amount);
        save();
    }

    private void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}
