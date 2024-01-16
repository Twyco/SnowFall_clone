package de.twyco.snowfall.listener;

import de.twyco.snowfall.WaveManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileHitListener implements Listener {

    @EventHandler
    public void onProjectileHit(final ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball) {
            if(event.getHitBlock() != null){
                WaveManager.snowBallHitBlock((Snowball) event.getEntity(), event.getHitBlock());
            }else if(event.getHitEntity() != null){
                if(event.getHitEntity() instanceof Player){
                    WaveManager.snowBallHitPlayer((Snowball) event.getEntity(), (Player) event.getHitEntity());
                }
            }
        }
    }
}
