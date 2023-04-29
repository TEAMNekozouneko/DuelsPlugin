package amedouhu.duelsplugin.events;

import amedouhu.duelsplugin.DuelsPlugin;
import amedouhu.duelsplugin.apis.SendMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlayerInteractEvent implements Listener {
    // arena stickの操作を処理する
    FileConfiguration config = DuelsPlugin.getPlugin().getConfig();
    SendMessage sendMessage = new SendMessage();
    @EventHandler
    public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent e) {
        // プレイヤーがクリック操作をしたとき
        ItemStack itemStack = e.getItem();
        if (itemStack != null && itemStack.getType() == Material.STICK) {
            // 棒でクリックされたなら
            ItemMeta itemMeta = itemStack.getItemMeta();
            String displayName = Objects.requireNonNull(itemMeta).getDisplayName();
            if (displayName.equals("arena stick")) {
                Player player = e.getPlayer();
                Location location = player.getLocation();
                String locationString = Objects.requireNonNull(location.getWorld()).getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
                List<String> lore = itemMeta.getLore();
                if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_AIR)) {
                    // 左クリックの場合
                    // spawnAを設定する
                    Objects.requireNonNull(lore).set(1,locationString);
                    // 実行メッセージを送信する
                    sendMessage.sendPlayer(player,"spawnAを設定しました:" + locationString);
                }else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                    // 右クリックの場合
                    // spawnBを設定する
                    Objects.requireNonNull(lore).set(2,locationString);
                    // 実行メッセージを送信する
                    sendMessage.sendPlayer(player,"spawnBを設定しました:" + locationString);
                }
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                // アリーナの作成に必要な情報が設定されているかを確認する
                if (lore != null && !lore.get(1).equals("spawnA(左クリック)") && !lore.get(2).equals("spawnB(右クリック)")) {
                    // 設定されている値が初期値ではないなら
                    // config.ymlに上書きする
                    String arena = lore.get(0);
                    config.set("arenas." + arena + ".isUse",false);
                    config.set("arenas." + arena + ".spawnA",lore.get(1));
                    config.set("arenas." + arena + ".spawnB",lore.get(2));
                    DuelsPlugin.getPlugin().saveConfig();
                    // arena stickを削除する
                    player.getInventory().remove(itemStack);
                    // 実行メッセージを送信する
                    sendMessage.sendPlayer(player,"アリーナ" + lore.get(0) + "を作成しました。");
                }
                e.setCancelled(true);
            }
        }
    }
}
