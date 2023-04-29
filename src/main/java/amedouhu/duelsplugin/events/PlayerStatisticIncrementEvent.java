package amedouhu.duelsplugin.events;

import amedouhu.duelsplugin.DuelsPlugin;
import amedouhu.duelsplugin.apis.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
public class PlayerStatisticIncrementEvent implements Listener {
    // Duelsでの統計値の変動を無効化する
    PlayerList playerList = new PlayerList();
    @EventHandler
    public void onPlayerStatisticIncrement(org.bukkit.event.player.PlayerStatisticIncrementEvent e) {
        // 統計の値が変動したとき
        e.setCancelled(true);
    }
}
