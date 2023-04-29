package amedouhu.duelsplugin.commands;

import amedouhu.duelsplugin.DuelsPlugin;
import amedouhu.duelsplugin.apis.ArenaList;
import amedouhu.duelsplugin.apis.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Settings implements CommandExecutor, TabCompleter {
    // 「settings」コマンドの処理クラス
    FileConfiguration config = DuelsPlugin.getPlugin().getConfig();
    ArenaList arenaList = new ArenaList();
    SendMessage sendMessage = new SendMessage();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender.isOp()) {
            // 送信者が管理者なら
            if (1 <= args.length) {
                // 設定項目が指定されているなら
                Player target;
                switch (args[0]) {
                    case "arena":
                        if (args.length == 2) {
                            // 引数の長さが2なら
                            if (config.get("arenas." + args[1]) == null) {
                                // まだアリーナが作成されていないなら
                                if (sender instanceof Player) {
                                    // 送信者がプレイヤーなら
                                    // arena stickを付与する
                                    Player player = (Player) sender;
                                    ItemStack itemStack = new ItemStack(Material.STICK);
                                    ItemMeta itemMeta = itemStack.getItemMeta();
                                    Objects.requireNonNull(itemMeta).setDisplayName("arena stick");
                                    itemMeta.setLore(Arrays.asList(args[1],"spawnA(左クリック)","spawnB(右クリック)"));
                                    itemStack.setItemMeta(itemMeta);
                                    player.getInventory().addItem(itemStack);
                                    // 実行メッセージを送信する
                                    sendMessage.sendSender(sender,"処理は正常に実行されました。");
                                }else {
                                    // 実行メッセージを送信する
                                    sendMessage.sendSender(sender,"アリーナの作成はゲーム内から行ってください。");
                                }
                            }else {
                                // 既にアリーナが作成されているなら
                                // アリーナを削除する
                                config.set("arenas." + args[1],null);
                                DuelsPlugin.getPlugin().saveConfig();
                                // 実行メッセージを送信する
                                sendMessage.sendSender(sender,"アリーナ" + args[1] + "は正常に削除されました。");
                            }
                        }else {
                            // 引数の長さが2ではないなら
                            // 実行メッセージを送信する
                            sendMessage.sendSender(sender,"対象のアリーナを指定する必要があります。");
                        }
                        break;
                    case "damage":
                        if (args.length == 2) {
                            // 引数の長さが2なら
                            // config.ymlを上書きする
                            boolean damage = Boolean.parseBoolean(args[1]);
                            config.set("damage",damage);
                            DuelsPlugin.getPlugin().saveConfig();
                            // 実行メッセージを送信する
                            sendMessage.sendSender(sender,"処理は正常に実行されました。");
                        }else {
                            // 実行メッセージを送信する
                            sendMessage.sendSender(sender,"対象の値を取得できませんでした。");
                        }
                        break;
                    case "inventory":
                        // 対象のプレイヤーを取得する
                        target = null;
                        switch (args.length) {
                            case 1:
                                if (sender instanceof Player) {
                                    // 送信者がプレイヤーなら
                                    target = (Player) sender;
                                }
                                break;
                            case 2:
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    if (player.getName().equals(args[1])) {
                                        // 引数のプレイヤーがオンラインなら
                                        target = player;
                                        break;
                                    }
                                }
                                break;
                        }
                        // 対象のプレイヤーを取得できたかを確認する
                        if (target != null) {
                            // 対象のプレイヤーを取得できているなら
                            Inventory inventory = target.getInventory();
                            // config.ymlを初期化する
                            config.set("inventory",null);
                            // config.ymlを上書きする
                            for (int i=0;i<43;i++) {
                                // スロットにアイテムを設置する
                                if (inventory.getItem(i) != null) {
                                    // ループ中のスロットにアイテムが設定されているなら
                                    ItemStack itemStack = inventory.getItem(i);
                                    Map<String, Object> itemMap = Objects.requireNonNull(itemStack).serialize();
                                    config.createSection("inventory." + i,itemMap);
                                    DuelsPlugin.getPlugin().saveConfig();
                                }
                            }
                            DuelsPlugin.getPlugin().saveConfig();
                            // 実行メッセージを送信する
                            sendMessage.sendSender(sender,"処理は正常に実行されました。");
                        }else {
                            // 実行メッセージを送信する
                            sendMessage.sendSender(sender,"対象のプレイヤーを取得できませんでした。");
                        }
                        break;
                    case "limit":
                        if (args.length == 2) {
                            // 引数の長さが2なら
                            // config.ymlを上書きする
                            int limit = Integer.parseInt(args[1]);
                            config.set("limit",limit);
                            DuelsPlugin.getPlugin().saveConfig();
                            // 実行メッセージを送信する
                            sendMessage.sendSender(sender,"処理は正常に実行されました。");
                        }else {
                            // 実行メッセージを送信する
                            sendMessage.sendSender(sender,"対象の値を取得できませんでした。");
                        }
                        break;
                    case "lobby":
                        // 対象のプレイヤーを取得する
                        target = null;
                        switch (args.length) {
                            case 1:
                                if (sender instanceof Player) {
                                    // 送信者がプレイヤーなら
                                    target = (Player) sender;
                                }
                                break;
                            case 2:
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    if (player.getName().equals(args[1])) {
                                        // 引数のプレイヤーがオンラインなら
                                        target = player;
                                        break;
                                    }
                                }
                                break;
                        }
                        // 対象のプレイヤーを取得できたかを確認する
                        if (target != null) {
                            // 対象のプレイヤーを取得できているなら
                            Location location = target.getLocation();
                            String locationString = Objects.requireNonNull(location.getWorld()).getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
                            config.set("lobby",locationString);
                            DuelsPlugin.getPlugin().saveConfig();
                            // 実行メッセージを送信する
                            sendMessage.sendSender(sender,"処理は正常に実行されました。");
                        }else {
                            // 実行メッセージを送信する
                            sendMessage.sendSender(sender,"対象のプレイヤーを取得できませんでした。");
                        }
                        break;
                    default:
                        // 実行メッセージを送信する
                        sendMessage.sendSender(sender,"設定項目が不明です。");
                        break;
                }
            }else {
                // 実行メッセージを送信する
                sendMessage.sendSender(sender,"設定項目を指定する必要があります。");
            }
        }else {
            // 送信者が管理者ではないなら
            sendMessage.sendSender(sender,"管理者権限を確認してください。");
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // タブ補完の処理を書く
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            // 第一引数の補完候補を追加する
            list.add("arena");
            list.add("damage");
            list.add("inventory");
            list.add("limit");
            list.add("lobby");
        }else if (args.length == 2) {
            // 第二引数の補完候補を追加する
            switch (args[0]) {
                case "arena":
                    // 第一引数:arena
                    list = arenaList.getArenas();
                    break;
                case "damage":
                    list.add("true");
                    list.add("false");
                    break;
                case "inventory":
                case "lobby":
                    // 第一引数:inventory
                    for (Player player : sender.getServer().getOnlinePlayers()) {
                        list.add(player.getName());
                    }
                    break;
                case "limit":
                    // 第一引数:limit
                    for (int i=1;i<10;i++) {
                        list.add(String.valueOf(i));
                    }
                    break;
            }
        }
        return list;
    }
}
