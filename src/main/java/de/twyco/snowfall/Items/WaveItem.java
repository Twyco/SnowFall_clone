package de.twyco.snowfall.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WaveItem extends ItemStack {

    public WaveItem(int wave) {
        super();
        ItemMeta itemMeta = getItemMeta();
        switch (wave) {
            case 1 -> {
                setType(Material.OAK_SLAB);
                setAmount(1);
                itemMeta = getItemMeta();
            }
            case 2 -> {
                setType(Material.STONE_SLAB);
                setAmount(1);
                itemMeta = getItemMeta();
            }
            case 3 -> {
                setType(Material.SMOOTH_STONE_SLAB);
                setAmount(1);
                itemMeta = getItemMeta();
            }
            case 4 -> {
                setType(Material.SMOOTH_QUARTZ_SLAB);
                setAmount(1);
                itemMeta = getItemMeta();
            }
            case 5 -> {
                setType(Material.POLISHED_DEEPSLATE_SLAB);
                setAmount(1);
                itemMeta = getItemMeta();
            }
            case 6 -> {
                setType(Material.NETHER_BRICK_SLAB);
                setAmount(1);
                itemMeta = getItemMeta();
            }
            case 7 -> {
                setType(Material.RED_NETHER_BRICK_SLAB);
                setAmount(1);
                itemMeta = getItemMeta();
            }
        }
        itemMeta.setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Wave " + wave);
        setItemMeta(itemMeta);
    }

}
