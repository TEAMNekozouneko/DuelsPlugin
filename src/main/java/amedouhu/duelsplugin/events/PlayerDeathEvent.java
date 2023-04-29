package amedouhu.duelsplugin.events;

import amedouhu.duelsplugin.DuelsPlugin;
import amedouhu.duelsplugin.apis.ManageGame;
import amedouhu.duelsplugin.apis.PlayerList;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PlayerDeathEvent implements Listener {
    // ゲームの勝敗判定を行う
    FileConfiguration config = DuelsPlugin.getPlugin().getConfig();
    ManageGame manageGame = new ManageGame();
    PlayerList playerList = new PlayerList();
    @EventHandler
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent e) {
        // プレイヤーが死亡したとき
        Player player = e.getEntity();
        List<Player> players = playerList.getPlayers();
        if (players.contains(player)) {
            // 死亡したプレイヤーがDuelsのプレイヤーなら
            // イベントを設定する
            e.setDeathMessage(null);
            e.setDroppedExp(0);
            e.getDrops().clear();
            e.setKeepInventory(false);
            // 死亡画面の表示を設定する
            World world = player.getWorld();
            if (! Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN))) {
                // doImmediateRespawnがfalseなら
                world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN,true);
                // タイマーを開始する
                new BukkitRunnable() {
                    // カウントダウンの秒数
                    @Override
                    public void run() {
                        // 0.05秒経過したなら
                        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN,false);
                    }
                }.runTaskTimer(DuelsPlugin.getPlugin(), 1, 0);
            }
            // ゲーム終了の処理を呼び出す
            Player winPlayer = e.getEntity().getKiller();
            if (winPlayer != null) {
                // winPlayerがnullではないなら
                manageGame.deleteGame(winPlayer,player);
            }else {
                // winPlayerがnullなら
                // ゲームを中断する
                List<Player> argsPlayers = new ArrayList<>();
                argsPlayers.add(player);
                manageGame.endGame(argsPlayers,"winPlayerの値がnullであるためPlayerDeathEventより発行されました。この問題は管理者に報告してください。");
            }
        }
    }
}
