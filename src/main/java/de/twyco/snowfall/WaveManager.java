package de.twyco.snowfall;

import de.twyco.stegisagt.Stegisagt;
import de.twyco.stegisagt.Util.Config;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Random;

public class WaveManager {

    private static int wave = 0;
    private static int round = 0;
    private static int maxRound = 0;
    private static int slabs = 0;
    private static long time = 0;
    private static long maxTime = 0;
    private static boolean cooldown = false;
    private static boolean cooldown2 = false;
    private static BossBar bossBar;
    private static ArrayList<Snowball> snowballs;
    private static BukkitTask task;
    private static BukkitTask task2;
    private static long time2 = 0;
    private static boolean autoplay = true;
    private static boolean pause = false;
    private static boolean stopRound = false;
    private static boolean isChecked = false;

    public static void reset() {
        if (task != null) {
            task.cancel();
        }
        if (task2 != null) {
            task2.cancel();
        }
        clearSlabs();
        clearBossBar();
        wave = 0;
        round = 0;
        maxRound = 0;
        slabs = 0;
        time = 0;
        maxTime = 0;
        cooldown = false;
        cooldown2 = false;
        bossBar = null;
        snowballs = null;
        task = null;
        task2 = null;
        time2 = 0;
        autoplay = true;
        pause = false;
        stopRound = false;
        isChecked = false;
    }

    public static void playNextWave() {
        if (wave < 7) {
            playWave(wave + 1);
        }
    }

    public static void pauseWave() {
        pause = true;
        showPauseBossBar();
    }

    public static void stop() {
        stopRound = true;
        reset();
    }

    public static void resumeWave() {
        if (!pause) {
            return;
        }
        pause = false;
        showTimerOnBossBar();
    }

    public static void playWave(int wave) {
        stop();
        WaveManager.wave = wave;
        switch (wave) {
            case 1 -> {
                slabs = 6;
                round = 6;
                time = (long) (6D * 20L);
            }
            case 2 -> {
                slabs = 6;
                round = 7;
                time = (long) (5D * 20L);
            }
            case 3 -> {
                slabs = 5;
                round = 8;
                time = (long) (3.5D * 20L);
            }
            case 4 -> {
                slabs = 5;
                round = 8;
                time = (long) (2.5D * 20L);
            }
            case 5 -> {
                slabs = 3;
                round = 10;
                time = (long) (2D * 20L);
            }
            case 6 -> {
                slabs = 2;
                round = 10;
                time = (long) (1.75D * 20L);
            }
            case 7 -> {
                slabs = 4;
                round = 10;
                time = (long) (0.75D * 20L);
            }
        }
        snowballs = new ArrayList<>();
        maxRound = round;
        maxTime = time;
        setStartBossBar();
    }

    public static void nextRound() {
        if (cooldown) {
            return;
        }
        if(stopRound){
            return;
        }
        if (round <= 0) {
            if (!cooldown2) {
                if (wave == 0) {
                    playNextWave();
                    return;
                }
                setWinnerBossBar();
            }
            return;
        }
        if (!snowballs.isEmpty()) {
            return;
        }
        cooldown = true;
        clearSlabs();
        setSlabs();
        showTimerOnBossBar();
        task = Bukkit.getScheduler().runTaskTimer(SnowFall.getInstance(), () -> {
            if (time <= 0) {
                isChecked = false;
                spawnSnowBalls();
                round--;
                time = maxTime;
                cooldown = false;
                task.cancel();
                for (Player player : SnowFall.getInstance().getAllPlayersPlayers()) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1F, 2F);
                }
            } else {
                if (time % 20 == 0) {
                    for (Player player : SnowFall.getInstance().getAllPlayersPlayers()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1F, 1F);
                    }
                }
                if (!isPause()) {
                    time--;
                    updateBossBarProgress(time, maxTime);
                }
                if(stopRound){
                    cooldown = false;
                    task.cancel();
                }
            }
        }, 0, 1);
    }

    private static void setSlabs() {
        Config config = SnowFall.getInstance().getSFConfig();
        Location loc1 = config.getFileConfiguration().getLocation("SnowFall.PlayArea.Location.1");
        Location loc2 = config.getFileConfiguration().getLocation("SnowFall.PlayArea.Location.2");
        if (loc1 == null || loc2 == null) {
            throw new RuntimeException("Some Locations not set!");
        }
        World world = loc1.getWorld();
        if (world == null) {
            return;
        }
        int minX = (int) Math.min(loc1.getX(), loc2.getX());
        int minY = (int) Math.min(loc1.getY(), loc2.getY());
        int minZ = (int) Math.min(loc1.getZ(), loc2.getZ());
        int maxX = (int) Math.max(loc1.getX(), loc2.getX());
        int maxZ = (int) Math.max(loc1.getZ(), loc2.getZ());

        switch (wave) {
            case 1 -> {
                Random random = new Random();
                for (int i = slabs; i > 0; i--) {
                    int rdmX;
                    int y = minY + 3;
                    int rdmZ;
                    Location location;
                    do {
                        rdmX = random.nextInt(maxX - minX + 1) + minX;
                        rdmZ = random.nextInt(maxZ - minZ + 1) + minZ;
                        location = new Location(world, rdmX, y, rdmZ);
                    } while (hasSlabAround(location));
                    world.getBlockAt(location).setType(Material.OAK_SLAB, true);
                }
            }
            case 2 -> {
                Random random = new Random();
                for (int i = slabs; i > 0; i--) {
                    int rdmX;
                    int y = minY + 3;
                    int rdmZ;
                    Location location;
                    do {
                        rdmX = random.nextInt(maxX - minX + 1) + minX;
                        rdmZ = random.nextInt(maxZ - minZ + 1) + minZ;
                        location = new Location(world, rdmX, y, rdmZ);
                    } while (hasSlabAround(location));
                    world.getBlockAt(location).setType(Material.STONE_SLAB, true);
                }
            }
            case 3 -> {
                Random random = new Random();
                for (int i = slabs; i > 0; i--) {
                    int rdmX;
                    int y = minY + 3;
                    int rdmZ;
                    Location location;
                    do {
                        rdmX = random.nextInt(maxX - minX + 1) + minX;
                        rdmZ = random.nextInt(maxZ - minZ + 1) + minZ;
                        location = new Location(world, rdmX, y, rdmZ);
                    } while (hasSlabAround(location));
                    world.getBlockAt(location).setType(Material.SMOOTH_STONE_SLAB, true);
                }
            }
            case 4 -> {
                Random random = new Random();
                for (int i = slabs; i > 0; i--) {
                    int rdmX;
                    int y = minY + 3;
                    int rdmZ;
                    Location location;
                    do {
                        rdmX = random.nextInt(maxX - minX + 1) + minX;
                        rdmZ = random.nextInt(maxZ - minZ + 1) + minZ;
                        location = new Location(world, rdmX, y, rdmZ);
                    } while (hasSlabAround(location));
                    world.getBlockAt(location).setType(Material.SMOOTH_QUARTZ_SLAB, true);
                }
            }
            case 5 -> {
                Random random = new Random();
                for (int i = slabs; i > 0; i--) {
                    int rdmX;
                    int y = minY + 3;
                    int rdmZ;
                    Location location;
                    do {
                        rdmX = random.nextInt(maxX - minX + 1) + minX;
                        rdmZ = random.nextInt(maxZ - minZ + 1) + minZ;
                        location = new Location(world, rdmX, y, rdmZ);
                    } while (hasSlabAround(location));
                    world.getBlockAt(location).setType(Material.POLISHED_DEEPSLATE_SLAB, true);
                }
            }
            case 6 -> {
                Random random = new Random();
                for (int i = slabs; i > 0; i--) {
                    int rdmX;
                    int y = minY + 3;
                    int rdmZ;
                    Location location;
                    do {
                        rdmX = random.nextInt(maxX - minX + 1) + minX;
                        rdmZ = random.nextInt(maxZ - minZ + 1) + minZ;
                        location = new Location(world, rdmX, y, rdmZ);
                    } while (hasSlabAround(location));
                    world.getBlockAt(location).setType(Material.NETHER_BRICK_SLAB, true);
                }
            }
            case 7 -> {
                Random random = new Random();
                for (int i = slabs; i > 0; i--) {
                    int rdmX;
                    int y = minY + 3;
                    int rdmZ;
                    Location location;
                    do {
                        rdmX = random.nextInt(maxX - minX + 1) + minX;
                        rdmZ = random.nextInt(maxZ - minZ + 1) + minZ;
                        location = new Location(world, rdmX, y, rdmZ);
                    } while (hasSlabAround(location));
                    world.getBlockAt(location).setType(Material.RED_NETHER_BRICK_SLAB, true);
                }
            }
        }
    }

    private static boolean hasSlabAround(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return false;
        }
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location testLoc = location.clone().add(x, 0, z);
                Block block = world.getBlockAt(testLoc);
                if (!block.getType().equals(Material.AIR)) {
                    if (block.getType().equals(Material.QUARTZ_SLAB)) {
                        continue;
                    }
                    return true;
                }
            }
        }
        return false;
    }


    public static void clearSlabs() {
        Config config = SnowFall.getInstance().getSFConfig();
        Location loc1 = config.getFileConfiguration().getLocation("SnowFall.PlayArea.Location.1");
        Location loc2 = config.getFileConfiguration().getLocation("SnowFall.PlayArea.Location.2");
        if (loc1 == null || loc2 == null) {
            throw new RuntimeException("Some Locations not set!");
        }
        World world = loc1.getWorld();
        if (world == null) {
            return;
        }
        int minX = (int) Math.min(loc1.getX(), loc2.getX());
        int minY = (int) Math.min(loc1.getY(), loc2.getY());
        int minZ = (int) Math.min(loc1.getZ(), loc2.getZ());
        int maxX = (int) Math.max(loc1.getX(), loc2.getX());
        int maxZ = (int) Math.max(loc1.getZ(), loc2.getZ());
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Block block = world.getBlockAt(x, minY + 3, z);
                block.setType(Material.AIR, true);
            }
        }

    }

    public static void spawnSnowBalls() {
        Config config = SnowFall.getInstance().getSFConfig();
        Location loc1 = config.getFileConfiguration().getLocation("SnowFall.PlayArea.Location.1");
        Location loc2 = config.getFileConfiguration().getLocation("SnowFall.PlayArea.Location.2");
        if (loc1 == null || loc2 == null) {
            throw new RuntimeException("Some Locations not set!");
        }
        World world = loc1.getWorld();
        if (world == null) {
            return;
        }
        int minX = (int) Math.min(loc1.getX(), loc2.getX());
        int minZ = (int) Math.min(loc1.getZ(), loc2.getZ());
        int maxX = (int) Math.max(loc1.getX(), loc2.getX());
        int maxY = (int) Math.max(loc1.getY(), loc2.getY());
        int maxZ = (int) Math.max(loc1.getZ(), loc2.getZ());
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Random random = new Random();
                if (random.nextBoolean() || random.nextBoolean()) {
                    continue;
                }
                double x2 = x + 0.5 + (random.nextInt(3) - 1) * (0.275 + (0.375 - 0.275) * random.nextDouble());
                double y2 = maxY + random.nextDouble() * 2 - 1;
                double z2 = z + 0.5 + (random.nextInt(3) - 1) * (0.275 + (0.375 - 0.275) * random.nextDouble());
                Location location = new Location(world, x2, y2, z2, 180, 90);
                Snowball snowball = (Snowball) world.spawnEntity(location, EntityType.SNOWBALL, false);
                snowball.setVelocity(snowball.getVelocity().setY(-0.75));
                snowballs.add(snowball);
            }
        }
    }

    public static void removePlayerBossBar(Player player) {
        if (!(bossBar == null)) {
            bossBar.removePlayer(player);
        }
    }

    public static void clearBossBar() {
        if (!(bossBar == null)) {
            bossBar.removeAll();
        }
    }

    public static void setWinnerBossBar() {
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
        }
        bossBar.setColor(BarColor.GREEN);
        bossBar.setTitle(ChatColor.GREEN + ChatColor.BOLD.toString() + "Stufe " + wave + " geschafft");
        bossBar.setVisible(true);
        for (Player player : SnowFall.getInstance().getAllPlayersPlayers()) {
            bossBar.addPlayer(player);
        }
        time2 = 100;
        cooldown2 = true;
        clearSlabs();
        task2 = Bukkit.getScheduler().runTaskTimer(SnowFall.getInstance(), () -> {
            if (time2 <= 0) {
                clearBossBar();
                task2.cancel();
                cooldown2 = false;
                if (isAutoplay()) {
                    playNextWave();
                }
            }
            if (!isPause()) {
                updateBossBarProgress(time2, 100);
                time2--;
            }
            if(stopRound){
                cooldown = false;
                task2.cancel();
            }
        }, 0L, 1);
    }

    public static void setStartBossBar() {
        time2 = 100;
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar("", BarColor.PINK, BarStyle.SOLID);
        }
        bossBar.setColor(BarColor.RED);
        bossBar.setTitle(ChatColor.RED + ChatColor.BOLD.toString() + "Wave " + ChatColor.BLUE + ChatColor.BOLD + wave
                + ChatColor.RED + ChatColor.BOLD + " startet in: " + ChatColor.BLUE + ChatColor.BOLD + (time2 % 20));
        bossBar.setVisible(true);
        for (Player player : SnowFall.getInstance().getAllPlayersPlayers()) {
            bossBar.addPlayer(player);
        }
        cooldown2 = true;
        task2 = Bukkit.getScheduler().runTaskTimer(SnowFall.getInstance(), () -> {
            if (time2 <= 0) {
                clearBossBar();
                bossBar = null;
                task2.cancel();
                cooldown2 = false;
                nextRound();
            } else {
                if (!isPause()) {
                    updateBossBarProgress(time2, 100);
                    double countdown = time2;
                    countdown /= 20;
                    countdown *= 10;
                    countdown = (int) countdown;
                    countdown /= 10;
                    bossBar.setTitle(ChatColor.RED + ChatColor.BOLD.toString() + "Wave " + ChatColor.BLUE + ChatColor.BOLD + wave
                            + ChatColor.RED + ChatColor.BOLD + " startet in: " + ChatColor.BLUE + ChatColor.BOLD + countdown);
                    time2--;
                }
            }

            if(stopRound){
                cooldown = false;
                task2.cancel();
            }
        }, 0L, 1);
    }

    private static void showTimerOnBossBar() {
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar("", BarColor.PINK, BarStyle.SOLID);
        }
        bossBar.setColor(BarColor.WHITE);
        bossBar.setTitle(ChatColor.WHITE + ChatColor.BOLD.toString() + "Wave " + ChatColor.BLUE + ChatColor.BOLD + wave
                + ChatColor.GRAY + ChatColor.BOLD + " | " + ChatColor.WHITE + ChatColor.BOLD + "Runde " + ChatColor.BLUE +
                ChatColor.BOLD + (Math.abs((round - maxRound)) + 1)
                + ChatColor.GRAY + ChatColor.BOLD + "/" + ChatColor.BLUE + ChatColor.BOLD + maxRound);
        for (Player player : SnowFall.getInstance().getAllPlayersPlayers()) {
            bossBar.addPlayer(player);
        }
        bossBar.setVisible(true);
    }

    private static void showPauseBossBar() {
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar("", BarColor.PINK, BarStyle.SOLID);
        }
        bossBar.setColor(BarColor.YELLOW);
        bossBar.setTitle(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Pausiert");
        for (Player player : SnowFall.getInstance().getAllPlayersPlayers()) {
            bossBar.addPlayer(player);
        }
        bossBar.setVisible(true);
    }

    private static void updateBossBarProgress(long i, long j) {
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
        }
        double progress = (double) i / j;
        bossBar.setProgress(progress);
    }

    public static void snowBallHitBlock(Snowball snowball, Block block) {
        if (snowballs == null) {
            return;
        }
        if (!isGroundZone(block.getLocation())) {
            if (snowballs.contains(snowball)) {
                snowballs.remove(snowball);
                return;
            }
            return;
        }
        if (!snowballs.contains(snowball)) {
            return;
        }
        snowballs.remove(snowball);
        if(!isChecked){
            isChecked = true;
            ArrayList<Player> checkPlayer = (ArrayList<Player>) SnowFall.getInstance().getPlayingPlayers().clone();
            for (Player player : checkPlayer) {
                if (!checkPlayer(player)) {
                    Stegisagt.killPlayer(player);
                }
            }
        }
        nextRound();
    }

    public static void snowBallHitPlayer(Snowball snowball, Player player) {
        if (snowballs == null) {
            return;
        }
        if (!SnowFall.getInstance().isPlayingPlayer(player)) {
            if (snowballs.contains(snowball)) {
                snowballs.remove(snowball);
                return;
            }
            return;
        }
        if (!snowballs.contains(snowball)) {
            return;
        }
        snowballs.remove(snowball);
        if(!isChecked){
            isChecked = true;
            ArrayList<Player> checkPlayer = (ArrayList<Player>) SnowFall.getInstance().getPlayingPlayers().clone();
            for (Player player1 : checkPlayer) {
                if (!checkPlayer(player1)) {
                    Stegisagt.killPlayer(player1);
                }
            }
        }
        nextRound();
    }

    private static boolean checkPlayer(Player player) {
        Location loc = player.getLocation();
        double x = Math.abs(loc.getX());
        double z = Math.abs(loc.getZ());
        x -= (int) x;
        z -= (int) z;
        boolean validX = (x <= 0.8D) && (x >= 0.2D);
        boolean validZ = (z <= 0.8D) && (z >= 0.2D);

        Block block = player.getWorld().getBlockAt(loc.add(0, 2, 0));
        boolean underSlab = block.getBlockData() instanceof Slab;
        return validX && validZ && underSlab;
    }

    private static boolean isGroundZone(Location location) {
        Config config = SnowFall.getInstance().getSFConfig();
        Location loc1 = config.getFileConfiguration().getLocation("SnowFall.PlayArea.Location.1");
        Location loc2 = config.getFileConfiguration().getLocation("SnowFall.PlayArea.Location.2");
        if (loc1 == null || loc2 == null) {
            throw new RuntimeException("Some Locations not set!");
        }
        int minX = (int) Math.min(loc1.getX(), loc2.getX());
        int minY = (int) Math.min(loc1.getY(), loc2.getY());
        int minZ = (int) Math.min(loc1.getZ(), loc2.getZ());
        int maxX = (int) Math.max(loc1.getX(), loc2.getX());
        int maxZ = (int) Math.max(loc1.getZ(), loc2.getZ());
        boolean validX = (minX <= location.getX() && location.getX() <= maxX);
        boolean validY = minY == location.getY();
        boolean validZ = (minZ <= location.getZ() && location.getZ() <= maxZ);
        return validX && validY && validZ;
    }

    public static boolean isAutoplay() {
        return autoplay;
    }

    public static void setAutoplay(boolean autoplay) {
        WaveManager.autoplay = autoplay;
    }

    public static boolean isPause() {
        return pause;
    }
}

