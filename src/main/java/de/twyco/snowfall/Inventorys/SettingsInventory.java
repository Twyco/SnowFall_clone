package de.twyco.snowfall.Inventorys;

import de.twyco.snowfall.Items.*;
import de.twyco.snowfall.WaveManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

public class SettingsInventory {

    private final Inventory inventory;
    private final String title;

    public SettingsInventory() {
        title = ChatColor.AQUA + ChatColor.BOLD.toString() + "Snowball Settings";
        this.inventory = Bukkit.createInventory(null, 5 * 9, title);

        for(int i = 1; i <= 7; i++){
            inventory.setItem(9+i, new WaveItem(i));
        }
        //Autoplay
        inventory.setItem(30, new AutoPlay());
        if (WaveManager.isAutoplay()) {
            inventory.setItem(32, new On());
        } else {
            inventory.setItem(32, new Off());
        }
        for (int i = 0; i < 5 * 9; i++){
            if(inventory.getItem(i) == null){
                inventory.setItem(i, new EmptySlotItem());
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getTitle() {
        return this.title;
    }

}
