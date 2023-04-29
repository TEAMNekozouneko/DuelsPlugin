package amedouhu.duelsplugin.apis;

import amedouhu.duelsplugin.DuelsPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArenaList {
    // アリーナリストの処理をするクラス
    FileConfiguration config = DuelsPlugin.getPlugin().getConfig();
    public List<String> getArenas() {
        // アリーナのリストを返す
        List<String> arenas = new ArrayList<>();
        if (config.get("arenas") != null) {
            // アリーナが存在するなら
            // 戻り値に追加する
            arenas.addAll(Objects.requireNonNull(config.getConfigurationSection("arenas")).getKeys(false));
        }
        return arenas;
    }
    public List<String> getUsedArenas() {
        // 使用中のアリーナを返す
        List<String> usedArenas = new ArrayList<>();
        if (config.get("arenas") != null) {
            // アリーナが存在するなら
            for (String usedArena : Objects.requireNonNull(config.getConfigurationSection("arenas")).getKeys(false)) {
                // 戻り値に追加する
                if (Objects.requireNonNull(config.get("arenas." + usedArena + ".isUse")).equals(true)) {
                    // 使用中のアリーナなら
                    if (config.get("arenas." + usedArena + ".spawnA") != null && config.get("arenas." + usedArena + ".spawnB") != null) {
                        // 未使用のアリーナなら
                        usedArenas.add(usedArena);
                    }
                }
            }
        }
        return usedArenas;
    }
    public List<String> getUnUsedArenas() {
        // 未使用のアリーナを返す
        List<String> unUsedArenas = new ArrayList<>();
        if (config.get("arenas") != null) {
            // アリーナが存在するなら
            for (String unUsedArena : Objects.requireNonNull(config.getConfigurationSection("arenas")).getKeys(false)) {
                // 戻り値に追加する
                if (Objects.requireNonNull(config.get("arenas." + unUsedArena + ".isUse")).equals(false)) {
                    if (config.get("arenas." + unUsedArena + ".spawnA") != null && config.get("arenas." + unUsedArena + ".spawnB") != null) {
                        // 未使用のアリーナなら
                        unUsedArenas.add(unUsedArena);
                    }

                }
            }
        }
        return unUsedArenas;
    }
}
