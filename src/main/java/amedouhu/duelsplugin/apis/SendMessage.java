package amedouhu.duelsplugin.apis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SendMessage {
    // メッセージを送信するクラス
    public void sendServer(String message) {
        // サーバーにメッセージを送信する
        Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.RED + "Duels" + ChatColor.GRAY + "] " + ChatColor.WHITE + message);
    }
    public void sendConsole(String message) {
        // コンソールにメッセージを送信する
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + "Duels" + ChatColor.GRAY + "] " + ChatColor.WHITE + message);
    }
    public void sendPlayer(Player dear,String message) {
        // プレイヤーにメッセージを送信する
        dear.sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + "Duels" + ChatColor.GRAY + "] " + ChatColor.WHITE + message);
    }
    public void sendSender(CommandSender sender,String message) {
        // コマンド送信者のメッセージを送信する
        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + "Duels" + ChatColor.GRAY + "] " + ChatColor.WHITE + message);
    }
}
