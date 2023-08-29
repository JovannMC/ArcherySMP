package me.jovannmc.archerysmp.events;

import me.jovannmc.archerysmp.ArcherySMP;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SelectedItemPickupEvent implements Listener {
    private final ArcherySMP plugin = ArcherySMP.getPlugin(ArcherySMP.class);
    
    @EventHandler
    public void onSelectedPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) { return; }
        Player player = (Player) e.getEntity();

        if (e.getItem().equals(Material.BOW) || e.getItem().equals(Material.CROSSBOW)) {
            ItemMeta itemMeta = e.getItem().getItemStack().getItemMeta();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

            if (dataContainer.has(plugin.getRoleKey(), PersistentDataType.STRING)) {
                String role = dataContainer.get(plugin.getRoleKey(), PersistentDataType.STRING);
                if (role.equals("archer")) {
                    plugin.archerHandler.addArcher(player);
                } else if (role.equals("hunter")) {
                    plugin.hunterHandler.addHunter(player);
                }
            }
        }
    }
}
