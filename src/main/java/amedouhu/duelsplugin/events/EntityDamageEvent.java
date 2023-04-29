package amedouhu.duelsplugin.events;

import amedouhu.duelsplugin.DuelsPlugin;
import amedouhu.duelsplugin.apis.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;
import java.util.Objects;

public class EntityDamageEvent implements Listener {
    // プレイヤーからの攻撃以外のダメージは無効化する
    FileConfiguration config = DuelsPlugin.getPlugin().getConfig();
    PlayerList playerList = new PlayerList();
    @EventHandler
    public void onEntityDamage(org.bukkit.event.entity.EntityDamageEvent e) {
        // ダメージが発生したとき
        if (e.getEntity() instanceof Player) {
            // ダメージを受けたのがプレイヤーなら
            Player player = (Player) e.getEntity();
            List<Player> players = playerList.getPlayers();
            if (players.contains(player) && Objects.requireNonNull(config.get("players." + player.getName() + ".state")).equals("playing")) {
                // ダメージを受けたのがDuelsのプレイヤーなら
                e.setCancelled(true);
                if (e.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    // ダメージを与えたエンティティを取得
                    Entity damageSource = ((EntityDamageByEntityEvent) e).getDamager();
                    // ダメージを与えたエンティティがプレイヤーかどうかチェック
                    if ((damageSource instanceof Player)) {
                        // ダメージを与えたのがプレイヤーなら
                        e.setCancelled(false);
                    }
                }
            }else {
                // ダメージを受けたのが一般のプレイヤーなら
                if (config.getBoolean("damage")) {
                    // 設定が有効なら
                    e.setCancelled(true);
                }
            }
        }
    }
}
