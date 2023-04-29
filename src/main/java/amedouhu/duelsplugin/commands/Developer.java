package amedouhu.duelsplugin.commands;

import amedouhu.duelsplugin.DuelsPlugin;
import amedouhu.duelsplugin.apis.SendMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Developer implements CommandExecutor, TabCompleter {
    // 「developer」コマンドの処理クラス

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // コマンドが実行されたとき
        SendMessage sendMessage = new SendMessage();
        if (sender.isOp()) {
            // 送信者が管理者なら
            if (args.length == 1) {
                // 引数の長さが1なら
                switch (args[0]) {
                    case "config":
                        String config = DuelsPlugin.getPlugin().getConfig().saveToString();
                        sendMessage.sendSender(sender,"config.yml\n" + config);
                        break;
                    case "plugin":
                        Plugin plugin = DuelsPlugin.getPlugin();
                        sendMessage.sendSender(sender,"PluginVersion:" + plugin.getDescription().getVersion() + " ApiVersion:" + plugin.getDescription().getAPIVersion());
                        break;
                    case "server":
                        String version = Bukkit.getServer().getVersion();
                        sendMessage.sendSender(sender,"ServerVersion:" + version);
                        break;
                    case  "playerClear":
                        DuelsPlugin.getPlugin().getConfig().set("players",null);
                        sendMessage.sendSender(sender,"プレイヤーをクリアしました。");
                        break;
                    case "ねこぞうねこさん":
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            if (player.getName().equals("nekozouneko")) {
                                // 送信者がねこぞうねこさんなら
                                List<String> messages = new ArrayList<>();
                                messages.add("いつもお世話になってます。");
                                messages.add("Bukkit.broadcastMessage(”バケツはなくさないように気を付けてくださいね。”);");
                                messages.add("Twitterのフォローありがとうございます。");
                                messages.add("お忙しいとは思いますが、お体ご自愛下さい。");
                                messages.add("私のスキンは「洞窟物語」という好きなゲーム作品から来ています。とても面白いのでお時間あれば遊んでみてください。原作はフリーゲームなので無料で遊べます。");
                                messages.add("このプラグインですが、納品遅れて本当にすみません。");
                                messages.add("サーバーの皆様本当に優しい方ばかりでありがたいです。");
                                messages.add("Mod開発は勉強中です。");
                                messages.add("パケットは手を出しただけでめまいがします。");
                                messages.add("ランダムメッセージは10種類しか用意できておりません。くだらないジョークコマンドで少量ではありますが容量を使用してすみません。そのうちアップデートで削除します。");
                                Random random = new Random();
                                int size = messages.size();
                                int index = random.nextInt(size);
                                // ChatColorの値の配列を取得
                                ChatColor[] colors = ChatColor.values();

                                // 配列の要素数を取得
                                int size1 = colors.length;

                                // ランダムなインデックスを生成
                                int index1 = (int) (Math.random() * size);

                                // 配列からChatColorを取得
                                ChatColor color = colors[index];
                                player.sendMessage(color + messages.get(index));
                            }else {
                                sendMessage.sendSender(sender,"ねこぞうねこさん権限を確認してください。");
                            }
                        }
                        break;
                    default:
                        sendMessage.sendSender(sender,"デバッグオプションが不明です。");
                        break;
                }
            }else {
                sendMessage.sendSender(sender,"コマンドオプションを指定してください。");
            }
        }else {
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
            list.add("config");
            list.add("plugin");
            list.add("server");
            list.add("ねこぞうねこさん");
        }
        return list;
    }
}
