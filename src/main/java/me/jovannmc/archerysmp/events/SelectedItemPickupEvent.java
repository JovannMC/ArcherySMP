package me.jovannmc.archerysmp.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class SelectedItemPickupEvent implements Listener {
    @EventHandler
    public void onSelectedPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) { return; }
        Player player = (Player) e.getEntity();

        if (e.getItem().getItemStack().getItemMeta().getDisplayName().equals("Archer's Bow")) {
            e.setCancelled(true);
        }
    }
}
