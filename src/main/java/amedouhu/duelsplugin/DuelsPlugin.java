package amedouhu.duelsplugin;

import amedouhu.duelsplugin.commands.Developer;
import amedouhu.duelsplugin.commands.Duels;
import amedouhu.duelsplugin.commands.Settings;
import amedouhu.duelsplugin.events.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class DuelsPlugin extends JavaPlugin {
    private static JavaPlugin plugin;
    FileConfiguration config = getConfig();
    @Override
    public void onEnable() {
        // プラグイン起動時の処理
        super.onEnable();
        // config.ymlを生成する
        saveDefaultConfig();
        // config.ymlを初期化する
        config.set("players",null);
        if (config.get("arenas") != null) {
            for (String arena : config.getConfigurationSection("arenas").getKeys(false)) {
                if (Objects.requireNonNull(config.get("arenas." + arena + ".isUse")).equals(true)) {
                    config.set("arenas." + arena + ".isUse",false);
                }
            }
        }
        if (config.get("damage") == null) {
            config.set("damage",true);
        }
        if (config.get("limit") == null) {
            config.set("limit",3);
        }
        if (config.get("lobby") == null) {
            config.set("lobby","world,0.0,0.0,0.0,0.0,0.0");
        }
        saveConfig();
        // pluginを定義する
        plugin = this;
        // コマンドを登録する
        Objects.requireNonNull(getCommand("developer")).setExecutor(new Developer());
        Objects.requireNonNull(getCommand("duels")).setExecutor(new Duels());
        Objects.requireNonNull(getCommand("settings")).setExecutor(new Settings());
        // イベントを登録する
        Bukkit.getServer().getPluginManager().registerEvents(new EntityDamageEvent(),this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractEvent(),this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathEvent(),this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerQuitEvent(),this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerStatisticIncrementEvent(),this);
    }

    @Override
    public void onDisable() {
        // プラグイン終了時の処理
        super.onDisable();
        // config.ymlを保存する
        saveConfig();
        // config.ymlを初期化する
        config.set("players",null);
        saveConfig();
    }
    public static JavaPlugin getPlugin() {return plugin;}
}
