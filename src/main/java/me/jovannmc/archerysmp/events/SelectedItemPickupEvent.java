package me.jovannmc.archerysmp.events;

import me.jovannmc.archerysmp.ArcherySMP;
import org.bukkit.Bukkit;
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
        Bukkit.getLogger().info("Player: " + player.getName());

        Bukkit.getLogger().info("Item: " + e.getItem().getItemStack().getType().toString());
        ItemMeta itemMeta = e.getItem().getItemStack().getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        if (dataContainer.has(plugin.getRoleKey(), PersistentDataType.STRING)) {
            Bukkit.getLogger().info("Role: " + dataContainer.get(plugin.getRoleKey(), PersistentDataType.STRING));
            String role = dataContainer.get(plugin.getRoleKey(), PersistentDataType.STRING);
            if (role.equals("archer")) {
                Bukkit.getLogger().info("Adding archer");
                plugin.archerHandler.addArcher(player, false);
            } else if (role.equals("hunter")) {
                Bukkit.getLogger().info("Adding hunter");
                plugin.hunterHandler.addHunter(player, false);
            }
        }
    }
}
