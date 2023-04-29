package amedouhu.duelsplugin.commands;

import amedouhu.duelsplugin.DuelsPlugin;
import amedouhu.duelsplugin.apis.ManageGame;
import amedouhu.duelsplugin.apis.PlayerList;
import amedouhu.duelsplugin.apis.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Duels implements CommandExecutor, TabCompleter {
    // 「duels」コマンドの処理クラス
    FileConfiguration config = DuelsPlugin.getPlugin().getConfig();
    ManageGame manageGame = new ManageGame();
    PlayerList playerList = new PlayerList();
    SendMessage sendMessage = new SendMessage();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // コマンドが実行されたとき
        // 対象のプレイヤーを取得する
        Player target = null;
        switch (args.length) {
            case 0:
                // 引数の長さが0なら
                if (sender instanceof Player) {
                    // 送信者がプレイヤーなら
                    target = (Player) sender;
                }
                break;
            case 1:
                // 引数の長さが1なら
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().equals(args[0])) {
                        // 引数のプレイヤーがオンラインなら
                        if (sender instanceof Player) {
                            // 送信者がプレイヤーなら
                            if (! player.getName().equals(sender.getName())) {
                                // 引数が他のプレイヤーを指定しているなら
                                if (sender.isOp()) {
                                    // 送信者が管理者なら
                                    target = Bukkit.getPlayer(args[0]);
                                }
                            }else {
                                // 引数が送信者を指定しているなら
                                target = (Player) sender;
                            }
                        }else {
                            // 送信者が管理者なら
                            target = Bukkit.getPlayer(args[0]);
                        }
                        break;
                    }
                }
                break;
        }
        if (target != null) {
            // 対象のプレイヤーを取得できているなら
            String state = config.getString("players." + target.getName() + ".state");
            if (state != null) {
                // stateを持っているなら
                if (state.equals("playing") || state.equals("count")) {
                    target = null;
                }
            }
        }
        // 対象のプレイヤーを取得できていないなら
        if (target != null) {
            // 対象のプレイヤーを取得できたなら
            // config.ymlに上書きする
            if (config.get("players." + target.getName()) == null) {
                // まだプレイヤーリストに追加されていないなら
                // プレイヤーリストに追加する
                config.set("players." + target.getName() + ".state","standby");
                sendMessage.sendPlayer(target,"ゲームに参加しました！");
            }else {
                // 既にプレイヤーリストに追加されているなら
                // プレイヤーリストから削除する
                config.set("players." + target.getName(),null);
                sendMessage.sendPlayer(target,"ゲームを退出しました！");
            }
            DuelsPlugin.getPlugin().saveConfig();
            // ゲームを開始できるかを確認する
            List<Player> standbys = playerList.getStandbys();
            if (2 <= standbys.size()) {
                // ゲームを開始できるなら
                Player playerA = standbys.get(0);
                Player playerB = standbys.get(1);
                // config.ymlを上書きする
                config.set("players." + playerA.getName() + ".state","count");
                config.set("players." + playerB.getName() + ".state","count");
                DuelsPlugin.getPlugin().saveConfig();
                // タイマーを開始する
                new BukkitRunnable() {
                    // カウントダウンの秒数
                    int count = 5;
                    @Override
                    public void run() {
                        // カウントダウンが0になったら
                        if (count == 0) {
                            // タスクをキャンセル
                            cancel();
                            // ゲーム開始APIを呼び出す
                            manageGame.createGame(playerA,playerB);
                        } else {
                            // 開始に必要なプレイヤーが揃っているかを確認する
                            if (playerA.isOnline() && playerB.isOnline()) {
                                // すべてのプレイヤーがオンラインなら
                                // カウントダウンメッセージを送信する
                                sendMessage.sendPlayer(playerA,ChatColor.YELLOW + "あと " + ChatColor.GOLD + count + ChatColor.YELLOW + " 秒でゲームを開始します！");
                                playerA.playSound(playerA, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1.0f,1.0f);
                                sendMessage.sendPlayer(playerB,ChatColor.YELLOW + "あと " + ChatColor.GOLD + count + ChatColor.YELLOW + " 秒でゲームを開始します！");
                                playerB.playSound(playerA, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1.0f,1.0f);
                                // カウントダウンを減らす
                                count--;
                            }else {
                                // すべてのプレイヤーがオンラインではないなら
                                List<Player> online = new ArrayList<>();
                                List<Player> offline = new ArrayList<>();
                                for (Player player : standbys) {
                                    if (player.isOnline()) {
                                        online.add(player);
                                    }else {
                                        offline.add(player);
                                    }
                                }
                                manageGame.endGame(online,"カウントダウン中に" + offline + "が退出したため、Duelsにより発行されました。");
                                for (Player player : offline) {
                                    config.set("players." + player.getName(),null);
                                }
                                DuelsPlugin.getPlugin().saveConfig();
                                cancel();
                            }
                        }
                    }
                }.runTaskTimer(DuelsPlugin.getPlugin(), 0, 20);
            }
            sendMessage.sendSender(sender,"対象のプレイヤーを正常に処理しました。");
        }else {
            // 対象のプレイヤーを取得できていないなら
            sendMessage.sendSender(sender, "対象のプレイヤーを取得できませんでした。");
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // タブ補完の処理を書く
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            // 第一引数の補完候補を追加する
            for (Player player : sender.getServer().getOnlinePlayers()) {
                list.add(player.getName());
            }
        }
        return list;
    }
}
