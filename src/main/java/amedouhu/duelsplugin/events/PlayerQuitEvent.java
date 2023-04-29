package amedouhu.duelsplugin.events;

import amedouhu.duelsplugin.DuelsPlugin;
import amedouhu.duelsplugin.apis.ManageGame;
import amedouhu.duelsplugin.apis.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerQuitEvent implements Listener {
    // ゲームの途中に切断された場合の処理を行う
    FileConfiguration config = DuelsPlugin.getPlugin().getConfig();
    ManageGame manageGame = new ManageGame();
    //PlayerList playerList = (PlayerList) Bukkit.getOnlinePlayers();
    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent e) {
        // プレイヤーが退出したとき
        Player player = e.getPlayer();
        if (config.get("players." + player.getName()) != null) {
            // 退出したのがDuelsのプレイヤーなら
            if (Objects.requireNonNull(config.getString("players." + player.getName() + ".state")).equals("playing")) {
                // ゲームをプレイ中なら
                String arena = config.getString("players." + player.getName() + ".arena");
                for (Player playing : Bukkit.getOnlinePlayers()) {
                    if (Objects.requireNonNull(config.get("players." + playing.getName() + ".arena")).equals(arena)) {
                        // 同一アリーナでプレイ中なら
                        if (! playing.equals(player)) {
                            List<Player> argsPlayers = new ArrayList<>();
                            argsPlayers.add(playing);
                            config.set("players." + player.getName(),null);
                            DuelsPlugin.getPlugin().saveConfig();
                            manageGame.endGame(argsPlayers,player + "が退出したため、PlayerQuitEventより発行されました。");
                            break;
                        }
                    }
                }
            }
            config.set("players." + player.getName(),null);
            DuelsPlugin.getPlugin().saveConfig();
        }
    }
}
