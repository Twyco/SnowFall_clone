package de.twyco.snowfall.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GameSettings extends ItemStack {

    public GameSettings(){
        super(Material.SNOWBALL, 1);
        ItemMeta itemMeta = getItemMeta();
        if(itemMeta == null) {
            return;
        }
        itemMeta.setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Snowball Settings");
        setItemMeta(itemMeta);
    }

}
