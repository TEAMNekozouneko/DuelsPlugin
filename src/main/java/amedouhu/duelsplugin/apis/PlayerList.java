package amedouhu.duelsplugin.apis;

import amedouhu.duelsplugin.DuelsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerList {
    // プレイヤーリストの処理をするクラス
    FileConfiguration config = DuelsPlugin.getPlugin().getConfig();
    public List<Player> getStandbys() {
        // 待機中のプレイヤーを取得する
        List<Player> standbys = new ArrayList<>();
        if (config.get("players") != null) {
            for (String player : config.getConfigurationSection("players").getKeys(false)) {
                if (config.get("players." + player + ".state").equals("standby")) {
                    // ループ内のプレイヤーが待機中なら
                    // 戻り値に追加する
                    standbys.add(Bukkit.getPlayer(player));
                }
            }
        }
        return standbys;
    }
    public List<Player> getPlayings() {
        // プレイ中のプレイヤーを取得する
        List<Player> playings = new ArrayList<>();
        if (config.get("players") != null) {
            for (String player : config.getConfigurationSection("players").getKeys(false)) {
                if (config.get("players." + player + ".state").equals("playing")) {
                    // ループ内のプレイヤーがプレイ中なら
                    // 戻り値に追加する
                    playings.add(Bukkit.getPlayer(player));
                }
            }
        }
        return playings;
    }
    public List<Player> getPlayers() {
        // 全てのプレイヤーを取得する
        List<Player> players = new ArrayList<>();
        if (config.get("players") != null) {
            for (String player : config.getConfigurationSection("players").getKeys(false)) {
                // 戻り値に追加する
                players.add(Bukkit.getPlayer(player));
            }
        }
        return players;
    }
}
