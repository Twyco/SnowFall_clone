package de.twyco.snowfall.commands;

import de.twyco.snowfall.Items.GameSettings;
import de.twyco.snowfall.Items.PauseItem;
import de.twyco.snowfall.Items.PlayAreaSelectionTool;
import de.twyco.snowfall.Items.StopGame;
import de.twyco.snowfall.SnowFall;
import de.twyco.snowfall.WaveManager;
import de.twyco.stegisagt.GameStatus;
import de.twyco.stegisagt.Stegisagt;
import de.twyco.stegisagt.Util.Config;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;

public class SnowfallCommand implements CommandExecutor, TabCompleter {

    private final SnowFall instance;

    public SnowfallCommand(){
        instance = SnowFall.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String alias, String[] args) {
        if (!(s instanceof Player)) {
            return false;
        }
        Player player = (Player) s;
        if (Stegisagt.getGameStatus().equals(GameStatus.CLOSED)) {
            if (!player.isOp()) {
                return false;
            }
            if (args.length == 0) {
                player.getInventory().addItem(new PlayAreaSelectionTool());
                return true;
            } else if (args.length == 1) {
                Config config = instance.getSFConfig();
                if (args[0].equalsIgnoreCase("spawnPos")) {
                    Location location = config.getFileConfiguration().getLocation("SnowFall.Spawn.Location.Spawn");
                    if (location == null) {
                        player.sendMessage(instance.getPrefix() + ChatColor.RED + "Die Spawnposition wurde noch nicht festgelegt!");
                        return false;
                    }
                    player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    return true;
                }else if (args[0].equalsIgnoreCase("play")) {
                    WaveManager.playNextWave();
                    return true;
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set")) {
                    Config config = instance.getSFConfig();
                    if (args[1].equalsIgnoreCase("spawn")) {
                        Location location = player.getLocation();
                        config.getFileConfiguration().set("SnowFall.Spawn.Location.Spawn", location);
                        config.save();
                        player.sendMessage(instance.getPrefix() + ChatColor.GREEN + "Die Spawn Position wurde festgelegt!");
                        return true;
                    }
                    return false;
                }
            }
        } else if (Stegisagt.getGameStatus().equals(GameStatus.PLAYING_SNOWFALL)) {
            if(!instance.isModPlayer(player)){
                return false;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("items")) {
                    player.getInventory().setItem(6, new PauseItem());
                    player.getInventory().setItem(7, new GameSettings());
                    player.getInventory().setItem(8, new StopGame());
                    return true;
                } else if (args[0].equalsIgnoreCase("stop")) {
                    instance.stopGame();
                    return true;
                }else if (args[0].equalsIgnoreCase("pause")) {
                    WaveManager.pauseWave();
                    return true;
                }else if (args[0].equalsIgnoreCase("resume")) {
                    WaveManager.resumeWave();
                    return true;
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("wave")) {
                    WaveManager.playWave(Integer.parseInt(args[1]));
                    return true;
                }else if (args[0].equalsIgnoreCase("autoplay")) {
                    WaveManager.setAutoplay(Boolean.parseBoolean(args[1]));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (args.length == 0) return list;
        if (args.length == 1) {
            list.add("items");
            list.add("stop");
            list.add("wave");
            list.add("pause");
            list.add("resume");
            list.add("autoplay");
        }else if (args.length == 2) {
            if(args[0].equalsIgnoreCase("set")){
                list.add("spawn");
            }else if(args[0].equalsIgnoreCase("wave")){
                list.add("<1-7>");
            }else if(args[0].equalsIgnoreCase("autoplay")){
                list.add("<true|false>");
            }
        }
        ArrayList<String> completerList = new ArrayList<>();
        String currentArg = args[args.length - 1].toLowerCase();
        for (String s : list) {
            String s1 = s.toLowerCase();
            if (s1.startsWith(currentArg)) {
                completerList.add(s);
            }
        }
        return completerList;
    }
}
