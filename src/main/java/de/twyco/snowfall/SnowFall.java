package de.twyco.snowfall;

import de.twyco.snowfall.Items.GameSettings;
import de.twyco.snowfall.Items.PauseItem;
import de.twyco.snowfall.Items.StopGame;
import de.twyco.snowfall.commands.SnowfallCommand;
import de.twyco.snowfall.listener.InventoryClickListener;
import de.twyco.snowfall.listener.PlayerInteractListener;
import de.twyco.snowfall.listener.ProjectileHitListener;
import de.twyco.stegisagt.GameStatus;
import de.twyco.stegisagt.Stegisagt;
import de.twyco.stegisagt.Util.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class SnowFall extends JavaPlugin {

    private static SnowFall instance;
    private static final String prefix = ChatColor.BOLD.toString() + ChatColor.DARK_GRAY + "[" + ChatColor.BOLD + ChatColor.GOLD + "SnowFall" +
            ChatColor.BOLD + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    private Config sfConfig;
    private ArrayList<Player> playingPlayers;
    private ArrayList<Player> allPlayers;
    private ArrayList<Player> modPlayers;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "SnowFall plugin wird geladen...");
        instance = this;
        sfConfig = new Config("Config.yml", getDataFolder());
        registerCommands();
        registerListener();
        resetGame();
    }

    @Override
    public void onDisable() {
        resetGame();
    }

    private void registerCommands() {
        Bukkit.getPluginCommand("snowfall").setExecutor(new SnowfallCommand());
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new ProjectileHitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
    }

    public static SnowFall getInstance() {
        return instance;
    }

    public Config getSFConfig() {
        return sfConfig;
    }

    public String getPrefix() {
        return prefix;
    }

    ////////////////////////

    public ArrayList<Player> getPlayingPlayers() {
        return playingPlayers;
    }

    public boolean isPlayingPlayer(Player player) {
        return playingPlayers.contains(player);
    }

    public void addPlayingPlayer(Player player) {
        playingPlayers.add(player);
    }

    public void removePlayingPlayer(Player player) {
        playingPlayers.remove(player);
    }

    ////////////////////////

    public ArrayList<Player> getAllPlayersPlayers() {
        return allPlayers;
    }

    public void addAllPlayer(Player player) {
        allPlayers.add(player);
    }

    public void removeAllPlayer(Player player) {
        allPlayers.remove(player);
    }

    ////////////////////////

    public ArrayList<Player> getModPlayers() {
        return modPlayers;
    }

    public boolean isModPlayer(Player player) {
        return modPlayers.contains(player);
    }

    public void addModPlayer(Player player) {
        modPlayers.add(player);
    }

    ////////////////////////

    public static void startGame(ArrayList<Player> players, ArrayList<Player> mods) {
        Config config = getInstance().getSFConfig();
        Location location = config.getFileConfiguration().getLocation("SnowFall.Spawn.Location.Spawn");
        if (location == null) {
            return;
        }
        Stegisagt.setGameStatus(GameStatus.PLAYING_SNOWFALL);
        instance.setListenerSettings();
        for (Player player : players) {
            player.getInventory().clear();
            instance.addPlayingPlayer(player);
            instance.addAllPlayer(player);
            player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.setGameMode(GameMode.SURVIVAL);
        }
        for (Player player : mods) {
            instance.addModPlayer(player);
            instance.addAllPlayer(player);
            player.getInventory().setItem(6, new PauseItem());
            player.getInventory().setItem(7, new GameSettings());
            player.getInventory().setItem(8, new StopGame());
            player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.setGameMode(GameMode.CREATIVE);
        }
    }

    public void stopGame() {
        Stegisagt.getInstance().setPlayerVisibility(true);
        WaveManager.stop();
        Stegisagt.setGameStatus(GameStatus.PLAYING);
        for (Player player : getInstance().getModPlayers()) {
            Stegisagt.getInstance().giveModItems(player);
            teleportBack(player);
        }
        for (Player player : getInstance().getPlayingPlayers()) {
            player.getInventory().clear();
            teleportBack(player);
        }
        instance.resetGame();
    }

    private void teleportBack(Player player) {
        player.sendTitle("", ChatColor.RED + "Teleport in 5 Sekunden.", 20, 30, 20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
            @Override
            public void run() {
                player.setGameMode(GameMode.SURVIVAL);
                if (Stegisagt.isDead(player.getUniqueId())) {
                    Stegisagt.revivePlayer(player);
                }
                Stegisagt.teleportToAliveOrDead(player);
            }
        }, 5 * 20L);
    }

    public static void killPlayer(Player player) {
        instance.removePlayingPlayer(player);
        instance.removeAllPlayer(player);
        WaveManager.removePlayerBossBar(player);
    }

    private void resetGame() {
        playingPlayers = new ArrayList<>();
        allPlayers = new ArrayList<>();
        modPlayers = new ArrayList<>();
        WaveManager.clearSlabs();
        WaveManager.clearBossBar();
        WaveManager.reset();
    }

    public void setListenerSettings() {
        Stegisagt.getInstance().setPvp(false);
        Stegisagt.getInstance().setFallDamage(false);
        Stegisagt.getInstance().setHunger(false);
        Stegisagt.getInstance().setBuildPlace(false);
        Stegisagt.getInstance().setBuildBreak(false);
        Stegisagt.getInstance().setBlockDrop(false);
        Stegisagt.getInstance().setPlayerCollision(false);
        Stegisagt.getInstance().setEntityDrop(false);
        Stegisagt.getInstance().setPlayerVisibility(false);
    }

}
