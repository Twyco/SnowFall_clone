package de.twyco.snowfall.listener;

import de.twyco.snowfall.Inventorys.SettingsInventory;
import de.twyco.snowfall.Items.GameSettings;
import de.twyco.snowfall.Items.PauseItem;
import de.twyco.snowfall.Items.PlayAreaSelectionTool;
import de.twyco.snowfall.Items.StopGame;
import de.twyco.snowfall.SnowFall;
import de.twyco.snowfall.WaveManager;
import de.twyco.stegisagt.GameStatus;
import de.twyco.stegisagt.Stegisagt;
import de.twyco.stegisagt.Util.Config;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PlayerInteractListener implements Listener {

    private final SnowFall instance;
    private boolean stopGame;
    private boolean stopMinDelay;

    public PlayerInteractListener() {
        instance = SnowFall.getInstance();
        stopGame = false;
        stopMinDelay = false;
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (Stegisagt.getGameStatus().equals(GameStatus.CLOSED)) {
            Action action = event.getAction();
            if (!action.equals(Action.RIGHT_CLICK_BLOCK) && !action.equals(Action.LEFT_CLICK_BLOCK)) {
                return;
            }
            if (event.getHand() != EquipmentSlot.HAND) {
                return;
            }
            Player player = event.getPlayer();
            if (!player.isOp()) {
                return;
            }
            ItemStack is = player.getInventory().getItemInMainHand();
            if (is.getItemMeta() == null) {
                return;
            }
            if (is.getType().equals(new PlayAreaSelectionTool().getType())) {
                if (!is.getItemMeta().equals(new PlayAreaSelectionTool().getItemMeta())) {
                    return;
                }
                Block block = event.getClickedBlock();
                Location location = block.getLocation();
                Config config = instance.getSFConfig();
                boolean leftClick = action.equals(Action.LEFT_CLICK_BLOCK);
                if (leftClick) {//ERSTE SETZEN
                    config.getFileConfiguration().set("SnowFall.PlayArea.Location.1", location);
                    config.save();
                    player.sendMessage(instance.getPrefix() + ChatColor.GREEN +
                            "Du hast die " + ChatColor.YELLOW + "1" + ChatColor.GREEN + " Play Area Position gesetzt.");
                } else {//ZWEITE SETZEN
                    config.getFileConfiguration().set("SnowFall.PlayArea.Location.2", location);
                    config.save();
                    player.sendMessage(instance.getPrefix() + ChatColor.GREEN +
                            "Du hast die " + ChatColor.YELLOW + "2" + ChatColor.GREEN + " Play Area Position gesetzt.");
                }
                Location firstLocation = config.getFileConfiguration().getLocation("SnowFall.PlayArea.Location.1");
                Location sndLocation = config.getFileConfiguration().getLocation("SnowFall.PlayArea.Location.2");
                if (firstLocation == null || sndLocation == null) {
                    return;
                }
                player.sendMessage(instance.getPrefix() + ChatColor.GREEN +
                        "Du hast die gesamte Play Area gespeichert.");
            }
        } else if (Stegisagt.getGameStatus().equals(GameStatus.PLAYING_SNOWFALL)) {
            Player player = event.getPlayer();
            if (!instance.isModPlayer(player)) {
                return;
            }
            if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                return;
            }
            if (event.getHand() != EquipmentSlot.HAND) {
                return;
            }
            ItemStack is = player.getInventory().getItemInMainHand();
            if (is.getItemMeta() == null) {
                return;
            }
            if (is.getType().equals(new StopGame().getType())) {
                if (is.getItemMeta().equals(new StopGame().getItemMeta())) {
                    if (stopGame && stopMinDelay) {
                        stopGame = false;
                        instance.stopGame();
                        player.sendMessage(instance.getPrefix() + ChatColor.RED + "Das Spiel wurde beendet!");
                    } else {
                        stopGame = true;
                        stopMinDelay = false;
                        instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, () -> stopMinDelay = true, 5L);
                        instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, () -> {
                            if (stopGame) {
                                stopGame = false;
                                player.sendMessage(instance.getPrefix() + ChatColor.RED + "zum Abbrechen Doppelklicken");
                            }
                        }, 20L);
                    }
                }
            } else if (is.getType().equals(new GameSettings().getType())) {
                player.openInventory(new SettingsInventory().getInventory());
                event.setCancelled(true);
            } else if (is.getType().equals(new PauseItem().getType())) {
                if(WaveManager.isPause()){
                    WaveManager.resumeWave();
                }else {
                    WaveManager.pauseWave();
                }
                player.getInventory().setItemInMainHand(new PauseItem());
                event.setCancelled(true);
            }
        }
    }
}
