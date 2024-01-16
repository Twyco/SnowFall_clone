package de.twyco.snowfall.listener;

import de.twyco.snowfall.Items.*;
import de.twyco.stegisagt.GameStatus;
import de.twyco.stegisagt.Stegisagt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (Stegisagt.getGameStatus().equals(GameStatus.CLOSED)) {
            Player player = event.getPlayer();
            ItemStack is = player.getInventory().getItemInMainHand();
            if (is.getType().equals(new PlayAreaSelectionTool().getType())) {
                if (is.getItemMeta() == null) {
                    return;
                }
                if (is.getItemMeta().equals(new PlayAreaSelectionTool().getItemMeta())) {
                    event.setCancelled(true);
                }
            }
        }else if(Stegisagt.getGameStatus().equals(GameStatus.PLAYING_SNOWFALL)) {
            Player player = event.getPlayer();
            ItemStack is = player.getInventory().getItemInMainHand();
            if (is.getType().equals(new StopGame().getType())) {
                if (is.getItemMeta() == null) {
                    return;
                }
                if (is.getItemMeta().equals(new StopGame().getItemMeta())) {
                    event.setCancelled(true);
                }
            }else if (is.getType().equals(new PauseItem().getType())) {
                if (is.getItemMeta() == null) {
                    return;
                }
                if (is.getItemMeta().equals(new PauseItem().getItemMeta())) {
                    event.setCancelled(true);
                }
            }else if (is.getType().equals(new GameSettings().getType())) {
                if (is.getItemMeta() == null) {
                    return;
                }
                if (is.getItemMeta().equals(new GameSettings().getItemMeta())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}