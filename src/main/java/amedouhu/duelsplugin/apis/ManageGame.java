package amedouhu.duelsplugin.apis;

import amedouhu.duelsplugin.DuelsPlugin;
import amedouhu.duelsplugin.commands.Duels;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ManageGame {
    // ゲームの管理を行うクラス
    FileConfiguration config = DuelsPlugin.getPlugin().getConfig();
    ArenaList arenaList = new ArenaList();
    SendMessage sendMessage = new SendMessage();
    public void createGame(Player playerA,Player playerB) {
        // ゲームを作成する
        // 空いているアリーナを探す
        List<String> unUsedArenas = arenaList.getUnUsedArenas();
        if (1 <= unUsedArenas.size()) {
            // 空いているアリーナがあるなら
            String unUsedArena = unUsedArenas.get(0);
            // アリーナにテレポートする
            String locationString;
            String[] locParts;
            Location location;
            // spawnAを取得する
            locationString = config.getString("arenas." + unUsedArena + ".spawnA");
            locParts = locationString.split(","); // カンマで区切る
            location= new Location(Bukkit.getWorld(locParts[0]), Double.parseDouble(locParts[1]), Double.parseDouble(locParts[2]), Double.parseDouble(locParts[3]), Float.parseFloat(locParts[4]), Float.parseFloat(locParts[5])); // Location型の変数を宣言
            playerA.teleport(location);
            // spawnBを取得する
            locationString = config.getString("arenas." + unUsedArena + ".spawnB");
            locParts = locationString.split(","); // カンマで区切る
            location= new Location(Bukkit.getWorld(locParts[0]), Double.parseDouble(locParts[1]), Double.parseDouble(locParts[2]), Double.parseDouble(locParts[3]), Float.parseFloat(locParts[4]), Float.parseFloat(locParts[5])); // Location型の変数を宣言
            playerB.teleport(location);
            // プレイヤーのゲームモードを変更
            playerA.setGameMode(GameMode.ADVENTURE);
            playerB.setGameMode(GameMode.ADVENTURE);
            // 開始メッセージを送信する
            playerA.sendTitle(ChatColor.RED + "Duels",ChatColor.YELLOW + "相手を倒して勝利を勝ち取りましょう！",10,40,10);
            playerA.playSound(playerA, Sound.UI_BUTTON_CLICK,1.0f,1.0f);
            playerB.sendTitle(ChatColor.RED + "Duels",ChatColor.YELLOW + "相手を倒して勝利を勝ち取りましょう！",10,40,10);
            playerB.playSound(playerB, Sound.UI_BUTTON_CLICK,1.0f,1.0f);
            // 体力・空腹ゲージを設定する
            playerA.setHealth(20);
            playerB.setHealth(20);
            playerA.setFoodLevel(20);
            playerB.setFoodLevel(20);
            // config.ymlを上書きする
            config.set("arenas." + unUsedArena + ".isUse",true);
            config.set("players." + playerA.getName() + ".state","playing");
            config.set("players." + playerB.getName() + ".state","playing");
            config.set("players." + playerA.getName() + ".arena",unUsedArena);
            config.set("players." + playerB.getName() + ".arena",unUsedArena);
            DuelsPlugin.getPlugin().saveConfig();
            // インベントリを設定する
            Inventory inventoryA = playerA.getInventory();
            Inventory inventoryB = playerB.getInventory();
            inventoryA.clear();
            inventoryB.clear();
            for (int i=0;i<43;i++) {
                // スロットにアイテムを設置する
                if (config.get("inventory." + i) != null) {
                    // ループ中のスロットにアイテムが設定されているなら
                    Map<String, Object> itemMap = config.getConfigurationSection("inventory." + i).getValues(true);
                    ItemStack itemStack = ItemStack.deserialize(itemMap);
                    inventoryA.setItem(i,itemStack);
                    inventoryB.setItem(i,itemStack);
                }
            }
            // 制限時間を処理する
            // タイマーを開始する
            new BukkitRunnable() {
                // カウントダウンの秒数
                int count = config.getInt("limit") * 60;
                @Override
                public void run() {
                    // カウントダウンが0になったら
                    if (count == 0) {
                        // タスクをキャンセル
                        cancel();
                        // 制限時間切れで終了させる
                        List<Player> argsPlayers = new ArrayList<>();
                        argsPlayers.add(playerA);
                        argsPlayers.add(playerB);
                        endGame(argsPlayers,"制限時間の超えたため、ManageGameより発行されました。");
                    } else {
                        if (config.get("arenas." + unUsedArena) != null) {
                            boolean isUse = config.getBoolean("arenas." + unUsedArena + ".isUse");
                            if (isUse) {
                                count --;
                            }else {
                                cancel();
                            }
                        }else {
                            // アリーナが存在しないなら
                            List<Player> argsPlayers = new ArrayList<>();
                            argsPlayers.add(playerA);
                            argsPlayers.add(playerB);
                            endGame(argsPlayers,"アリーナが削除されたため、ManageGameより発行されました。");
                            cancel();
                        }
                    }
                }
            }.runTaskTimer(DuelsPlugin.getPlugin(), 0, 20);
        }else {
            // 実行メッセージを送信する
            sendMessage.sendPlayer(playerA,"使用できるアリーナが見つからないため、ゲームを開始できませんでした。");
            sendMessage.sendPlayer(playerB,"使用できるアリーナが見つからないため、ゲームを開始できませんでした。");
            // プレイヤーリストから削除する
            config.set("players." + playerA.getName(),null);
            config.set("players." + playerB.getName(),null);
            DuelsPlugin.getPlugin().saveConfig();
        }
    }
    public void deleteGame(Player winPlayer,Player losePlayer) {
        // ゲームを削除する
        // 勝敗メッセージを送信する
        winPlayer.sendTitle(ChatColor.GOLD + "§lWIN","ロビーに戻ります",10,40,10);
        sendMessage.sendPlayer(winPlayer,"thank you for playing!");
        losePlayer.sendTitle(ChatColor.BLUE + "§lLOSE","ロビーに戻ります",10,40,10);
        sendMessage.sendPlayer(losePlayer,"thank you for playing!");
        // インベントリを初期化する
        winPlayer.getInventory().clear();
        losePlayer.getInventory().clear();
        // 体力・空腹ゲージを設定する
        winPlayer.setHealth(20);
        losePlayer.setHealth(20);
        winPlayer.setFoodLevel(20);
        losePlayer.setFoodLevel(20);
        // ロビーにテレポートさせる
        String locationString = config.getString("lobby");
        String[] locParts = Objects.requireNonNull(locationString).split(","); // カンマで区切る
        Location location= new Location(Bukkit.getWorld(locParts[0]), Double.parseDouble(locParts[1]), Double.parseDouble(locParts[2]), Double.parseDouble(locParts[3]), Float.parseFloat(locParts[4]), Float.parseFloat(locParts[5])); // Location型の変数を宣言
        winPlayer.teleport(location);
        losePlayer.teleport(location);
        // config.ymlを上書きする
        String arena = config.getString("players." + winPlayer.getName() + ".arena");
        if (config.get("arenas." + arena) != null) {
            config.set("arenas." + arena + ".isUse",false);
        }
        config.set("players." + winPlayer.getName(),null);
        config.set("players." + losePlayer.getName(),null);
        DuelsPlugin.getPlugin().saveConfig();
        DuelsPlugin.getPlugin().saveConfig();
    }

    public void endGame(List<Player> players,String about) {
        // ゲームを強制終了する
        for (Player player : players) {
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            String locationString = config.getString("lobby");
            String[] locParts = Objects.requireNonNull(locationString).split(","); // カンマで区切る
            Location location= new Location(Bukkit.getWorld(locParts[0]), Double.parseDouble(locParts[1]), Double.parseDouble(locParts[2]), Double.parseDouble(locParts[3]), Float.parseFloat(locParts[4]), Float.parseFloat(locParts[5])); // Location型の変数を宣言
            player.teleport(location);
            String arena = config.getString("players." + player.getName() + ".arena");
            if (config.get("arenas." + arena) != null) {
                config.set("arenas." + arena + ".isUse",false);
            }
            config.set("players." + player.getName(),null);
            DuelsPlugin.getPlugin().saveConfig();
            sendMessage.sendPlayer(player,"ゲームが終了したためロビーに移動しました。発行元:" + about);
        }
    }

}
