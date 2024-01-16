package de.twyco.snowfall.Items;

import de.twyco.snowfall.WaveManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PauseItem extends ItemStack {

    public PauseItem(){
        super(WaveManager.isPause() ? Material.REDSTONE_TORCH : Material.TORCH, 1);
        ItemMeta itemMeta = getItemMeta();
        if(itemMeta == null){
            return;
        }
        itemMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() +
                (WaveManager.isPause() ? "Click to Resume" : "Click to Pause"));
        setItemMeta(itemMeta);
    }

}
